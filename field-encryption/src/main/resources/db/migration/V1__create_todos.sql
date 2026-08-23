-- ─── Todos table ────────────────────────────────────────────────────────────
--
-- title / description store AES-256-GCM ciphertext (TEXT so it can hold any length).
-- title_hash stores an HMAC-SHA256 blind index so we can do exact-match searches
-- without ever decrypting. See EncryptionService.hash().
--
-- PostgreSQL note: change TIMESTAMP → TIMESTAMPTZ for production.
--   H2 MODE=PostgreSQL accepts TIMESTAMP and it behaves like TIMESTAMPTZ in UTC.

CREATE TABLE todos
(
    id          UUID        NOT NULL,
    title       TEXT        NOT NULL,
    title_hash  VARCHAR(64),
    description TEXT,
    completed   BOOLEAN     NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP   NOT NULL,
    updated_at  TIMESTAMP   NOT NULL,
    CONSTRAINT pk_todos PRIMARY KEY (id)
);

-- Fast exact-match lookup: WHERE title_hash = HMAC('buy milk')
CREATE INDEX idx_todos_title_hash ON todos (title_hash);
