package com.ashfaq.jobqueue.controlplane.dto;

import com.ashfaq.jobqueue.controlplane.domain.Job;
import com.ashfaq.jobqueue.controlplane.domain.JobStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

public record JobStatusResponse(
        UUID id,
        String name,
        String originalFileName,
        JobStatus status,
        String storagePath,
        String errorMessage,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
    public static JobStatusResponse from(Job job) {
        return new JobStatusResponse(
                job.getId(),
                job.getName(),
                job.getOriginalFileName(),
                job.getStatus(),
                job.getStoragePath(),
                job.getErrorMessage(),
                job.getCreatedAt(),
                job.getUpdatedAt()
        );
    }
}
