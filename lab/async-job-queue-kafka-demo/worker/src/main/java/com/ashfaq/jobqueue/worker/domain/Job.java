package com.ashfaq.jobqueue.worker.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Mirrors control-plane's Job entity, mapped onto the same "jobs" table.
 * The control plane owns the Flyway migration; the worker only ever
 * updates rows the control plane already created.
 */
@Entity
@Table(name = "jobs")
public class Job {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(name = "original_file_name", nullable = false)
    private String originalFileName;

    @Column(name = "incoming_path", nullable = false)
    private String incomingPath;

    @Column(name = "storage_path")
    private String storagePath;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private JobStatus status;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    protected Job() {
    }

    public Job(UUID id, String name, String originalFileName, String incomingPath, JobStatus status) {
        this.id = id;
        this.name = name;
        this.originalFileName = originalFileName;
        this.incomingPath = incomingPath;
        this.status = status;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public String getIncomingPath() {
        return incomingPath;
    }

    public String getStoragePath() {
        return storagePath;
    }

    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }

    public JobStatus getStatus() {
        return status;
    }

    public void setStatus(JobStatus status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }
}
