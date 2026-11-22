package com.ashfaq.jobqueue.controlplane.dto;

import com.ashfaq.jobqueue.controlplane.domain.JobStatus;

import java.util.UUID;

public record UploadResponse(UUID id, JobStatus status) {
}
