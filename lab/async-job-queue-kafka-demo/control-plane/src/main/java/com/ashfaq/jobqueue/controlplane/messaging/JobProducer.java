package com.ashfaq.jobqueue.controlplane.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class JobProducer {

    private static final Logger log = LoggerFactory.getLogger(JobProducer.class);

    private final KafkaTemplate<String, JobMessage> kafkaTemplate;

    public JobProducer(KafkaTemplate<String, JobMessage> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishUploadRequested(JobMessage message) {
        kafkaTemplate.send(JobTopics.UPLOAD_REQUESTED, message.jobId(), message)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish job {} to {}", message.jobId(), JobTopics.UPLOAD_REQUESTED, ex);
                    } else {
                        log.info("Published job {} to {} at offset {}", message.jobId(), JobTopics.UPLOAD_REQUESTED,
                                result.getRecordMetadata().offset());
                    }
                });
    }
}
