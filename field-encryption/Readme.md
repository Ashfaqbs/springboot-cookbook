# todo-encryption

Reference project demonstrating **Encryption at Rest** with AES-256-GCM + HMAC-SHA256 blind indexes in a Spring Boot 3 + JPA application.

> For the full conceptual guide, use cases, and step-by-step porting instructions see **[ENCRYPTION-GUIDE.md](./ENCRYPTION-GUIDE.md)**.

## Purpose
This is a learning/reference project. Build once, refer back whenever you need to add field-level encryption to a real project.

## Tech Stack
- Java 17 + Spring Boot 3.2
- Spring Data JPA + Hibernate 6
- H2 (in-memory, PostgreSQL-compatible mode) — swap for PostgreSQL in prod
- Flyway for schema migrations
- Spring Validation

## How to Run
```bash
./mvnw spring-boot:run
# App starts on http://localhost:8080
# H2 console: http://localhost:8080/h2-console (JDBC URL: jdbc:h2:mem:tododb)
```

## API Endpoints

### Todo CRUD (JPA-backed, encrypted in DB)
```
GET    /api/v1/todos               — list all
GET    /api/v1/todos/{id}          — get by id
GET    /api/v1/todos/search?title= — exact-match search via blind index
POST   /api/v1/todos               — create
PUT    /api/v1/todos/{id}          — update
DELETE /api/v1/todos/{id}          — delete
```

### Vault (Map-backed, no DB — shows encryption is storage-agnostic)
```
POST   /api/v1/vault/{key}         — encrypt a string and store in memory
GET    /api/v1/vault/{key}         — retrieve and decrypt by key
```
Example:
```bash
curl -X POST http://localhost:8080/api/v1/vault/mykey \
  -H "Content-Type: application/json" \
  -d '{"value":"super-secret-123"}'
# → {"key":"mykey","stored_as":"ENC:h081C46k..."}   (map holds ciphertext)

curl http://localhost:8080/api/v1/vault/mykey
# → {"key":"mykey","value":"super-secret-123"}       (always returns plaintext)
```

## Project Structure
```
src/main/java/com/example/todo/
  config/
    EncryptionConfig.java        — @ConfigurationProperties for keys, fails fast if missing
    EntityListenerConfig.java    — wires EncryptionService into TodoHashListener static field
  crypto/
    EncryptionService.java       — AES-256-GCM encrypt/decrypt + HMAC-SHA256 hash
    EncryptedStringConverter.java — JPA AttributeConverter (transparent encrypt/decrypt)
  domain/
    Todo.java                    — Entity with @Convert and @EntityListeners
  listener/
    TodoHashListener.java        — @PrePersist/@PreUpdate: computes hash columns
  repository/
    TodoRepository.java          — findByTitleHash() for blind index search
  service/
    TodoService.java             — CRUD + search (hashes before querying)
  controller/
    TodoController.java          — REST endpoints
  dto/
    TodoRequest.java             — record: create/update body
    TodoResponse.java            — record: API response (always plaintext)
  migration/
    DataEncryptionMigration.java — ApplicationRunner: encrypts any existing plaintext rows
  exception/
    TodoNotFoundException.java
    GlobalExceptionHandler.java
```

## Key Design Decisions

### Why AES-256-GCM?
Authenticated encryption — decryption throws if the ciphertext was tampered with. Random IV per encryption means identical plaintexts produce different ciphertexts (no frequency analysis).

### Why HMAC-SHA256 blind indexes?
AES-GCM with random IVs breaks `WHERE title = ?`. HMAC is deterministic: `hash("buy milk")` always returns the same hex string, enabling exact-match index lookups without decryption.

### Why static field in TodoHashListener?
JPA EntityListeners are instantiated by Hibernate (`new TodoHashListener()`), bypassing Spring DI. Static field is the simplest reliable solution. `EntityListenerConfig` sets it during application startup.

### Why @Autowired in EncryptedStringConverter?
Spring Boot 3 + Hibernate 6 automatically registers `SpringBeanContainer`, allowing field injection into `@Converter` beans. This is standard supported behavior.

### Ordering guarantee
`@PrePersist`/`@PreUpdate` fires BEFORE `AttributeConverter`. So when `TodoHashListener` runs, entity fields are still plaintext — correct for hashing.

## Switching to PostgreSQL
1. In `pom.xml`: swap H2 for `postgresql` driver
2. In `application.yml`: update `spring.datasource.url`, `driver-class-name`, credentials
3. In the Flyway migration: change `TIMESTAMP` → `TIMESTAMPTZ`
4. No Java code changes needed — the pattern is DB-agnostic

## Encryption Keys
- `encryption.key` — 32-byte AES-256 key
- `encryption.hmac-key` — 32-byte HMAC key
- `encryption.hmac-algorithm` — `HmacSHA256` (default) | `HmacSHA384` | `HmacSHA512`
- In production: supply via environment variables `ENCRYPTION_KEY` / `ENCRYPTION_HMAC_KEY`
- App fails fast on startup if keys are missing or wrong length
- **Warning:** changing `hmac-algorithm` after data exists invalidates all blind indexes — requires full rehash migration
