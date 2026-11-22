# Async Job Queue - Kafka Demo

A minimal but complete implementation of the **async job queue** pattern: a
client hands off a long-running unit of work over HTTP, gets back a job ID
immediately, and polls (or gets notified) for the result later instead of
holding the connection open.

## The pattern

Synchronous request/response breaks down once a request needs real work done
on it - virus-scanning an upload, transcoding a video, generating a report.
Blocking the HTTP thread for that duration doesn't scale and gives the
client nothing to do but wait on a connection that can time out.

The async job queue pattern splits the request into two halves connected by
a durable, replayable queue:

1. A **control plane** accepts the request, does the minimum work needed to
   accept responsibility for it (validate input, persist a job record,
   stage the payload), and returns immediately with a job ID.
2. A **worker**, running as a separate process, consumes queued jobs at its
   own pace and does the actual work, updating the job's status as it goes.
3. The client polls a status endpoint (or subscribes to a webhook/websocket
   in a fancier setup) using the job ID to find out when it's done.

Kafka is the queue here instead of, say, a database table polled by a cron
job, because it gives you: durability across restarts, natural
back-pressure (the worker pulls at its own pace), the ability to run
multiple worker instances as one consumer group for horizontal scaling, and
replay if a worker needs to reprocess a window of jobs.

## Architecture

```
                 POST /upload                      GET /status/{id}
                      |                                   ^
                      v                                   |
              +----------------+                  +----------------+
              |  control-plane |----------------->|   PostgreSQL   |
              |   (REST API)   |    write job     |     "jobs"     |
              +----------------+                  +----------------+
                      |                                   ^
                      | publish JobMessage                | update status
                      v                                   |
              +----------------+                  +----------------+
              |     Kafka      |----------------->|     worker     |
              | jobs.upload.   |    consume       | (Kafka consumer)|
              |   requested    |                  +----------------+
              +----------------+                          |
                                                            v
                                              +--------------------------+
                                              | shared volume (/data)    |
                                              | incoming/  -> storage/   |
                                              +--------------------------+
```

Both services read and write the same `jobs` table - they're two
deployables of one logical service, not independent microservices with
separate data ownership, so a shared schema is intentional here rather than
a shortcut.

### Request flow

1. `POST /upload` (multipart: `file`, `name`) hits **control-plane**.
2. It generates a job ID (`UUID`), streams the file to a shared "incoming"
   staging directory, inserts a `jobs` row with status `QUEUED`, and
   publishes a `JobMessage {jobId, name, originalFileName, incomingPath}`
   to the `jobs.upload.requested` Kafka topic, keyed by job ID.
3. It responds `202 Accepted` with `{ id, status }` - the HTTP request is
   done; the actual processing hasn't started yet.
4. **worker** consumes the message from its `job-worker-group` consumer
   group, flips the row to `IN_PROGRESS`, reads ("downloads") the file
   bytes from the incoming path, writes ("uploads") them to the
   centralized storage directory, flips the row to `COMPLETED` with the
   final `storagePath`, and only then acknowledges the Kafka message
   (manual ack, `enable.auto.commit=false`) - so a worker crash mid-job
   leaves the message unacknowledged and it gets redelivered.
5. `GET /status/{id}` on **control-plane** reads the same `jobs` row at any
   point to report `QUEUED` / `IN_PROGRESS` / `COMPLETED` / `FAILED`.

### Why two storage directories instead of one

`incoming/` and `storage/` are both just folders on the same shared Docker
volume in this demo, but they stand in for two different real systems: a
transient staging area the control plane writes to, and the durable,
centralized store (think S3/GCS) every downstream consumer reads from. The
worker's "download to memory, then upload to centralized storage" step is
what actually happens when those are different systems; keeping the two
paths distinct here makes that boundary explicit even though both are local
disk today.

### Failure handling (and what's deliberately left out)

If the worker throws while moving the file, the job is marked `FAILED` with
the exception message and the Kafka message is still acknowledged - this
demo has no retry topic or dead-letter queue, so a failed job is a dead end
you'd have to resubmit manually. A production version of this would add a
bounded number of retries via a retry topic and a DLQ for anything that
still fails after that, per the usual Kafka consumer resiliency pattern.

## Tech stack

| Concern              | Choice                                          |
|-----------------------|--------------------------------------------------|
| Language / runtime    | Java 17, Spring Boot 3.3.5                      |
| Control plane         | Spring Web (REST), Spring Data JPA              |
| Worker                | Spring Kafka consumer, Spring Data JPA          |
| Queue                 | Apache Kafka + Zookeeper (Confluent images)     |
| Database              | PostgreSQL 16                                   |
| Schema migrations     | Flyway (owned by control-plane only)            |
| Containerization      | Docker multi-stage builds, Docker Compose       |

## Project layout

```
async-job-queue-kafka-demo/
  control-plane/     REST API: POST /upload, GET /status/{id}
  worker/             Kafka consumer that processes queued jobs
  docker-compose.yml  Zookeeper + Kafka + Postgres + both services
```

Each Spring Boot module follows the standard layered structure
(`controller` / `service` / `repository` / `domain` / `dto` / `messaging` /
`storage` / `config` / `exception`).

## Running it locally

Requires Docker and Docker Compose. No local Java or Maven install needed -
everything builds inside the containers.

```bash
docker compose up --build
```

This starts, in dependency order (each gate is a real healthcheck, not a
fixed sleep):

1. `zookeeper` and `kafka`
2. `postgres`
3. `control-plane` (runs Flyway migrations on startup, creating `jobs`)
4. `worker` (waits for control-plane to be healthy, so the table exists
   before it starts consuming)

Once everything is up:

```bash
# Submit a job
curl -F "file=@/path/to/some/file.csv" -F "name=nightly-report" \
  http://localhost:8080/upload
# -> {"id":"5f1c...","status":"QUEUED"}

# Poll for status using the returned id
curl http://localhost:8080/status/5f1c...
# -> {"id":"5f1c...","name":"nightly-report","originalFileName":"file.csv",
#     "status":"COMPLETED","storagePath":"/data/storage/5f1c..._file.csv",
#     "errorMessage":null,"createdAt":"...","updatedAt":"..."}
```

Poll `GET /status/{id}` a few times if you catch it while still `QUEUED` or
`IN_PROGRESS` - processing is near-instant for small files on a local
machine, but the state machine is real and will show it if you're fast
enough.

Tear down (including the Postgres data volume and the shared job storage):

```bash
docker compose down -v
```

### Running the services outside Docker

Both modules default their Postgres and Kafka hosts to `localhost` (see
each `application.yml`), so you can also run `zookeeper`, `kafka`, and
`postgres` via `docker compose up zookeeper kafka postgres` and then run
each Spring Boot app from your IDE or `./mvnw spring-boot:run` - just make
sure `STORAGE_INCOMING_DIR` / `STORAGE_STORAGE_DIR` point at directories
both processes can actually see on your host filesystem.

## Production hardening: what's simplified here, and what to add

This is a lab, sized to demonstrate the pattern clearly rather than to run in
production. A few things below are already done the "right" way because
they cost nothing extra; most are called out as gaps on purpose, so you can
see the shape of the real fix rather than just being told "this is a demo."

### Already done, and worth noticing why

- **The worker acks only after the job is durably updated, not after
  reading the message.** `JobConsumer` calls `acknowledgment.acknowledge()`
  in a `finally` block *after* `JobProcessingService.process()` has already
  written `COMPLETED`/`FAILED` to Postgres, with `enable.auto.commit=false`
  and `AckMode.MANUAL_IMMEDIATE`. If the worker process dies mid-job, the
  offset was never committed, so the broker redelivers the message to
  another consumer in the group instead of silently losing the job. Acking
  on receipt instead (or relying on Kafka's own auto-commit interval) is
  the most common way at-least-once processing quietly turns into
  at-most-once.
- **The job row's primary key doubles as the Kafka message key.** Keying by
  `jobId` means every message for a given job lands on the same partition,
  so per-job ordering is preserved even with multiple partitions.
- **Non-root containers, pinned base image tags, multi-stage builds,
  explicit resource-free healthchecks gating startup order** - all present
  because they're cheap, not because this is a demo.

### The real gap: publish isn't part of the database transaction

`JobService.submitJob()` saves the `jobs` row and calls
`jobProducer.publishUploadRequested(...)` as two separate steps. If Kafka
is unreachable at that exact moment (broker restart, network blip), the
job sits in Postgres as `QUEUED` forever - nothing will ever republish it,
and `GET /status/{id}` will lie to the caller indefinitely. The current
code only logs the publish failure; it doesn't surface it to the client or
retry it.

The standard fix is the **transactional outbox pattern**: in the same DB
transaction that inserts the `jobs` row, insert an `outbox_events` row
containing the same payload. A separate poller (or Debezium reading the
WAL via CDC) reads unpublished outbox rows and publishes them to Kafka,
marking them published only after a broker ack. This guarantees "job
persisted" and "job will eventually be published" happen atomically,
without needing distributed transactions across Postgres and Kafka.

### Idempotency on reprocessing

Because acks happen after processing, a crash *between* the DB commit and
the offset commit causes a redelivery of a job that's already
`COMPLETED`. `JobProcessingService.process()` doesn't currently guard
against this - it would re-download and re-upload the file, harmlessly but
wastefully. A one-line guard (`if (job.getStatus() == COMPLETED) return;`
before doing any work) makes the handler properly idempotent, which is
required reading for any at-least-once consumer, not just this one.

### No retry topic or dead-letter queue

A failed job today is a dead end - it's marked `FAILED` in Postgres and the
message is acked, so nobody retries it automatically. Per the standard
Kafka consumer resiliency pattern, a real deployment would:

1. Retry a bounded number of times (e.g. via Spring Kafka's
   `DefaultErrorHandler` with a `FixedBackOff`) for transient failures
   (storage temporarily unavailable).
2. Route anything that still fails to a `jobs.upload.requested.dlq` topic
   with the original headers/exception attached, instead of silently
   marking `FAILED` and moving on - so a human or a replay tool can inspect
   and reprocess it later without re-reading application logs.

### Plain JSON instead of Avro/Protobuf + Schema Registry

The Kafka payload is a hand-serialized JSON record via
`spring-kafka`'s `JsonSerializer`/`JsonDeserializer` with
`USE_TYPE_INFO_HEADERS` disabled and a hardcoded `VALUE_DEFAULT_TYPE`. That
works for two services deployed in lockstep from the same repo, but it has
no schema evolution story - renaming a field breaks the consumer with no
compile-time warning on either side. A real deployment should register the
`JobMessage` schema (Avro or Protobuf) with a Schema Registry and let it
enforce compatibility across independent deploys of control-plane and
worker.

### Shared filesystem volume instead of real object storage

`/data/incoming` and `/data/storage` are directories on one Docker volume
that both containers happen to mount - that only works because both
services run on the same Docker host. The moment either scales to multiple
nodes, this breaks. The fix is what the naming already implies: an actual
object store (S3, GCS, MinIO) with the control plane generating a
presigned upload URL or writing directly to a `staging/` prefix, and the
worker copying to a `processed/` prefix - same logical flow, but it
actually works across machines.

### Whole file loaded into memory

`CentralizedStorageService.download()` reads the entire file into a
`byte[]`. Fine for the CSV-sized files this demo expects; for anything
approaching the JVM heap size, this should stream (`InputStream` to
`OutputStream`, or multipart streaming upload to object storage) instead of
buffering the whole payload in memory per in-flight job.

### Everything else worth a line

- **Secrets**: `docker-compose.yml` hardcodes `jobqueue`/`jobqueue` for
  Postgres. Fine for a local lab, never for anything real - use an `.env`
  file kept out of git, or a proper secrets manager.
- **No auth on `/upload` or `/status/{id}`**: anyone who can reach port
  8080 can submit jobs and read any job's status by guessing/enumerating
  UUIDs (which, being random UUIDv4s, isn't practically guessable, but
  that's not the same as authorization). Add auth + ownership checks
  before this is internet-facing.
- **No rate limiting** on `/upload` - a public-facing version needs it,
  especially since each request currently does synchronous disk I/O on the
  request thread.
- **Correlation IDs**: nothing currently ties a `jobId`'s control-plane log
  lines to its worker log lines beyond eyeballing the UUID. Putting `jobId`
  in the logging MDC on both sides makes `grep`-ing a job's full lifecycle
  across two services' logs trivial.
- **Only unit tests exist** (Mockito, mocked repository/Kafka/storage).
  There's no integration test that actually exercises real Kafka and
  Postgres via Testcontainers - worth adding before trusting this beyond a
  lab.
- **Kafka producer isn't configured for idempotence**: `acks=all` is set,
  but `enable.idempotence=true` (plus bounding
  `max.in.flight.requests.per.connection`) is what actually prevents
  duplicate messages on producer-side retries. Cheap to add, currently
  missing.
- **Single partition by default** (`KAFKA_AUTO_CREATE_TOPICS_ENABLE` with
  Kafka's default partition count): the worker's consumer group can only
  parallelize up to the topic's partition count, so scaling worker
  replicas beyond that does nothing. A real deployment creates the topic
  explicitly with a partition count sized to the expected worker fleet.

## Tests

```bash
cd control-plane && ./mvnw test
cd worker && ./mvnw test
```

Unit tests cover the service layer (`JobService`, `JobProcessingService`)
with Mockito, exercising the queue-and-publish path, validation failures,
the not-found path, and both the success and failure branches of message
processing.
