CREATE TABLE jobs (
    id                 UUID PRIMARY KEY,
    name               VARCHAR(255) NOT NULL,
    original_file_name VARCHAR(255) NOT NULL,
    incoming_path      VARCHAR(500) NOT NULL,
    storage_path       VARCHAR(500),
    status             VARCHAR(20) NOT NULL,
    error_message      VARCHAR(1000),
    created_at         TIMESTAMPTZ NOT NULL,
    updated_at         TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_jobs_status ON jobs (status);
