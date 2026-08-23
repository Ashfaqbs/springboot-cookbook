# Encryption at Rest — Field-Level Encryption Guide

A practical guide for understanding, using, and porting the pattern in this project to your own Spring Boot application.

---

## What problem does this solve?

A database breach is not hypothetical — SQL injection, stolen backups, misconfigured cloud storage, rogue employees with DB access. When it happens, if you stored plaintext, everything is exposed instantly.

Field-level encryption means the database column stores ciphertext (encrypted text that is unreadable without the key — the original data can only be recovered by decrypting it with the correct key). Even with full read access to the database, an attacker sees:

```
email      = "ENC:AgF+xK3mNp7cZ0Lq..."   ← meaningless without the key
email_hash = "a1b2c3d4..."                ← a hash, not reversible
```

The encryption key lives in the application layer (env var / secret manager), not in the database. So even if the DB is fully compromised, attackers cannot read anything — the ciphertext is useless without the key.

---

## When to use this

Use field-level encryption for columns that contain:

| Data type | Examples |
|-----------|----------|
| Identity | name, email, phone number, national ID |
| Financial | card number, account number, UPI ID, RRN, auth code |
| Medical | diagnosis, prescription, test results |
| Credentials | OTP codes, security answers |
| Location | home address, GPS coordinates |

Do NOT encrypt everything. Columns that are foreign keys, used for joins, or carry no sensitive meaning (status flags, counters, timestamps) should stay plaintext. Over-encrypting hurts query performance and makes debugging harder.

---

## How it works — the full picture

### Write path

```
user.setEmail("john@example.com")
        │
        ▼
EntityListener (@PrePersist / @PreUpdate)
  ├─ email is still PLAINTEXT at this point
  └─ email_hash = HMAC-SHA256("john@example.com")
        │            deterministic 64-char hex
        ▼
AttributeConverter (convertToDatabaseColumn)
  ├─ generate random 12-byte IV
  ├─ AES-256-GCM encrypt("john@example.com")
  ├─ combine: [IV + ciphertext + auth_tag]
  ├─ Base64 encode
  └─ prefix "ENC:"
        │
        ▼
DB stores:
  email      = "ENC:AgF+xK3mNp..."   (different every time due to random IV)
  email_hash = "a1b2c3..."            (same every time — deterministic HMAC)
```

> **Note — what is `ENC:` ?**
> `ENC:` is just a marker tag the code manually prepends to every encrypted value before saving. It stands for **ENC**rypted and has no other special meaning. Its only purpose is so `decrypt()` can tell the difference between an already-encrypted value (starts with `ENC:`) and a plaintext value from before encryption was added (no prefix) — in which case it returns the value as-is instead of crashing. Without this prefix, the code would have no way to know whether a DB value needs decrypting or not.

### Read path

```
DB returns:
  email = "ENC:AgF+xK3mNp..."
        │
        ▼
AttributeConverter (convertToEntityAttribute)
  ├─ detect "ENC:" prefix
  ├─ Base64 decode
  ├─ extract first 12 bytes → IV
  ├─ AES-256-GCM decrypt remaining bytes
  └─ verify auth tag (throws if tampered)
        │
        ▼
user.getEmail() = "john@example.com"   ← app always sees plaintext
```

The application code never knows encryption is happening. `getEmail()` always returns a plaintext string.

### Search path

```
search("john@example.com")
        │
        ▼
hash = HMAC-SHA256("john@example.com") = "a1b2c3..."
        │
        ▼
SELECT * FROM users WHERE email_hash = 'a1b2c3...'
        │
        ▼
results loaded → converter decrypts email on read → app gets plaintext
```

No decryption happens during the lookup. The index on `email_hash` makes this fast.

---

## Technology choices explained

### AES-256-GCM

- **AES-256**: 256-bit key, military-grade symmetric encryption
- **GCM mode**: Galois/Counter Mode — provides _authenticated encryption_
  - "Authenticated" means: if anyone tampers with the ciphertext, decryption throws an exception
  - Without authentication (e.g., AES-CBC), an attacker could flip bytes in the ciphertext and the decryption would silently return garbage
- **Random IV per encryption**: the same plaintext encrypted twice produces different ciphertext, preventing frequency analysis attacks

### HMAC-SHA256 (blind index)

#### Why does hashing exist at all here?

Encryption alone breaks search. After encrypting, the DB stores `"ENC:AgF+xK3mNp..."`. A query like `WHERE email = 'ash001x@mail.com'` finds nothing — the column has ciphertext, not plaintext. So to keep search working, a hash of the plaintext is stored in a separate `*_hash` column. Search then becomes:

```text
app: hash("ash001x@mail.com") = "a1b2c3..."
SQL: WHERE email_hash = 'a1b2c3...'   ← fast index lookup, no decryption needed
```

The DB never sees the plaintext. The hash is enough to find the right row.

#### What if two different emails produce the same hash? (collision)

With HMAC-SHA256 the output space is 2^256 possible values:

```text
115,792,089,237,316,195,423,570,985,008,687,907,853,
269,984,665,640,564,039,457,584,007,913,129,639,936
```

To put that in perspective — there are roughly 10^80 atoms in the observable universe. 2^256 is approximately 10^77. You would need to hash more unique inputs than atoms in the universe before a collision becomes likely. In practice, this is considered impossible.

If a collision somehow occurred (it won't), both rows would be returned from the DB. The converter would decrypt both and the app would get two results. Not a security breach — just an extremely improbable data oddity.

- **HMAC**: Hash-based Message Authentication Code — a keyed hash
- **Why keyed?** A plain SHA-256 hash of "john@example.com" is the same for everyone. An attacker with the hash table could run a dictionary attack. A keyed HMAC requires the HMAC key — without it, the attacker cannot verify guesses
- **Why deterministic?** The hash must be the same every time so `WHERE email_hash = ?` can match rows

### JPA AttributeConverter

- Hooks into Hibernate at the column level
- `convertToDatabaseColumn` → called when writing to DB
- `convertToEntityAttribute` → called when reading from DB
- Applied per-field via `@Convert(converter = EncryptedStringConverter.class)`
- Zero changes to service/controller code

### JPA EntityListener

- Hooks into Hibernate lifecycle events (`@PrePersist`, `@PreUpdate`)
- Fires **before** `AttributeConverter` — so the field is still plaintext when the hash is computed
- Used to auto-compute `*_hash` columns before every save

---

## The search limitation

This is the most important trade-off to understand.

| Query type | Works? | Reason |
|-----------|--------|--------|
| `WHERE email = ?` | No | DB column is ciphertext |
| `WHERE email_hash = HMAC(?)` | Yes | Deterministic hash lookup |
| `WHERE email LIKE '%@gmail.com'` | No | No partial hash possible |
| `WHERE email_hash IN (HMAC(?), HMAC(?))` | Yes | Multi-value exact match |
| `ORDER BY email` | No | Sorts by ciphertext, meaningless |
| Range queries (`>`, `<`, `BETWEEN`) | No | No order-preserving encryption here |

If your use case requires `LIKE`, full-text search, or range queries on encrypted columns, this pattern alone is not enough. Options:
- Store a normalized/truncated blind index (e.g., hash of first 3 chars) for prefix search — reduces security
- Use a dedicated encrypted search solution (e.g., Elasticsearch with field-level encryption plugin)
- Decrypt in application and filter in memory — only viable for small datasets

---

## The Vault API — encryption without a database

This project includes a second demo at `/api/v1/vault` that stores encrypted values in a plain `Map<String, String>`.

```
POST /api/v1/vault/{key}
Body: {"value": "super-secret-123"}

Response:
{
  "key": "mypassword",
  "stored_as": "ENC:h081C46kCVgpn2ck..."   ← what sits in the map
}
```

```
GET /api/v1/vault/{key}

Response:
{
  "key": "mypassword",
  "value": "super-secret-123"              ← always decrypted on retrieval
}
```

**Why this exists:** it shows that `EncryptionService` is storage-agnostic. The encrypt/decrypt calls are the same whether the ciphertext ends up in:
- A JPA entity column (via `AttributeConverter`)
- A `Map` (this demo)
- Redis (call `encrypt()` before `redisTemplate.opsForValue().set(...)`)
- A file
- MongoDB document field
- A message payload before publishing to Kafka

The JPA `AttributeConverter` is just a convenience wrapper. Underneath, it's always: `encrypt()` before write, `decrypt()` after read.

---

## Configurable HMAC algorithm

The HMAC algorithm for blind indexes is configurable via `application.yml`:

```yaml
encryption:
  hmac-algorithm: HmacSHA256   # default
  # options: HmacSHA256 | HmacSHA384 | HmacSHA512
```

| Algorithm | Output size | Notes |
|-----------|-------------|-------|
| HmacSHA256 | 64 hex chars (32 bytes) | Default, fast, widely used |
| HmacSHA384 | 96 hex chars (48 bytes) | Higher collision resistance |
| HmacSHA512 | 128 hex chars (64 bytes) | Maximum, use on sensitive indexes |

**Critical warning:** changing `hmac-algorithm` after data exists breaks all searches. All stored `*_hash` columns must be recomputed — write a migration similar to `DataEncryptionMigration.java` that re-saves every row. Pick the algorithm once and commit to it.

---

## Adding encryption to your own project

### Step 1 — Copy the crypto layer

Copy these files into your project, change the package:

```
crypto/EncryptionService.java
crypto/EncryptedStringConverter.java
config/EncryptionConfig.java
config/EntityListenerConfig.java
```

These have no dependencies on the Todo domain — they're fully reusable.

### Step 2 — Add keys to your config

```yaml
encryption:
  key: "${ENCRYPTION_KEY}"           # 32-byte env var in prod
  hmac-key: "${ENCRYPTION_HMAC_KEY}" # 32-byte env var in prod
  hmac-algorithm: HmacSHA256
```

For local dev, set defaults in `application-local.yml`. Never commit real keys.

### Step 3 — Decide which fields to encrypt

For each entity, identify sensitive columns. Add a corresponding `*_hash` column only for fields you need to search by.

```
entity field → encrypted?  → searchable? → add _hash column?
─────────────────────────────────────────────────────────────
email        → yes         → yes         → yes (email_hash)
phone        → yes         → yes         → yes (phone_hash)
name         → yes         → no          → no
password     → use bcrypt, NOT this pattern
status       → no          → yes         → n/a (plain column)
```

### Step 4 — Add schema columns

```sql
-- encrypted column: TEXT to hold arbitrary-length ciphertext
ALTER TABLE users ADD COLUMN email TEXT;
ALTER TABLE users ADD COLUMN email_hash VARCHAR(64);

CREATE INDEX idx_users_email_hash ON users(email_hash);
```

### Step 5 — Annotate the entity

```java
@Entity
@EntityListeners(YourHashListener.class)
public class User {

    @Convert(converter = EncryptedStringConverter.class)
    @Column(name = "email")
    private String email;

    @Column(name = "email_hash", length = 64)
    private String emailHash;

    // no @Convert on emailHash — it's stored as plain hex
}
```

### Step 6 — Write the hash listener

Copy `TodoHashListener.java` and adapt for your entity:

```java
public class UserHashListener {

    private static EncryptionService encryptionService;

    public static void setEncryptionService(EncryptionService service) {
        UserHashListener.encryptionService = service;
    }

    @PrePersist
    @PreUpdate
    public void computeHashes(User user) {
        if (encryptionService == null) return;
        user.setEmailHash(encryptionService.hash(user.getEmail()));
        user.setPhoneHash(encryptionService.hash(user.getPhone()));
    }
}
```

Register it in `EntityListenerConfig`:

```java
@Autowired
public void configureListeners(EncryptionService encryptionService) {
    TodoHashListener.setEncryptionService(encryptionService);
    UserHashListener.setEncryptionService(encryptionService);  // add this
}
```

### Step 7 — Update the repository

```java
Optional<User> findByEmailHash(String emailHash);
```

### Step 8 — Hash before searching in the service

```java
public Optional<User> findByEmail(String email) {
    String hash = encryptionService.hash(email);
    return userRepository.findByEmailHash(hash);
}
```

### Step 9 — Handle existing plaintext data

If the table already has rows with plaintext values, write a migration like `DataEncryptionMigration.java`:

```java
@Component
public class UserEncryptionMigration implements ApplicationRunner {

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        List<User> plaintext = userRepository.findAllWithPlaintextEmail();
        plaintext.forEach(userRepository::save);  // listener + converter handle the rest
    }
}
```

Add the native query to find unencrypted rows:

```java
@Query(value = "SELECT * FROM users WHERE email NOT LIKE 'ENC:%'", nativeQuery = true)
List<User> findAllWithPlaintextEmail();
```

---

## Key management in production

| Approach | Use when |
|----------|----------|
| Environment variables | Simplest. Fine for single-service deployments |
| AWS Secrets Manager / GCP Secret Manager | Multi-service, secret rotation needed |
| HashiCorp Vault | Self-hosted, complex key hierarchy, audit trails |
| Kubernetes Secrets | K8s deployments — combine with sealed-secrets or external-secrets operator |

Rules regardless of approach:
- Never hardcode keys in source code
- Never log keys (check your startup logs)
- Rotate keys by: decrypt all data with old key → re-encrypt with new key → swap key in secret manager (key rotation migration)
- Keep separate keys for separate environments — dev keys should never touch prod data

---

## What this pattern does NOT protect against

| Threat | Protected? | Notes |
|--------|-----------|-------|
| DB dump / stolen backup | Yes | Ciphertext is useless without the app key |
| SQL injection reading DB | Yes | Attacker sees ciphertext |
| Compromised application server | No | App has the key — attacker can call decrypt |
| Compromised encryption key | No | Re-encrypt all data with a new key |
| Timing attacks on hash lookups | Partial | Use constant-time comparison for auth-critical hashes |
| Side-channel attacks | No | Out of scope for app-level encryption |

This is application-layer encryption, not a replacement for:
- Database access controls and audit logging
- TLS for data in transit
- OS/disk-level encryption (e.g., AWS EBS encryption)

Defense in depth: use all of the above together.
