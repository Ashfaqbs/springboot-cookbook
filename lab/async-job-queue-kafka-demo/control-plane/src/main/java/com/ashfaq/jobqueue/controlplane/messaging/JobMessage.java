package com.ashfaq.jobqueue.controlplane.messaging;

/**
 * Kafka payload for a queued job. Mirrored by an identical record in the
 * worker module - the two services do not share a library, so the message
 * contract is duplicated intentionally rather than coupling the deployables.
 */
public record JobMessage(
        String jobId,
        String name,
        String originalFileName,
        String incomingPath
) {
}
