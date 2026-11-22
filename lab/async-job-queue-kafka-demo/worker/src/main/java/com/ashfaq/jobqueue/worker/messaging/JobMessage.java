package com.ashfaq.jobqueue.worker.messaging;

public record JobMessage(
        String jobId,
        String name,
        String originalFileName,
        String incomingPath
) {
}
