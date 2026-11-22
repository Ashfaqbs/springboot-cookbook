package com.ashfaq.jobqueue.worker.messaging;

import com.ashfaq.jobqueue.worker.service.JobProcessingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
public class JobConsumer {

    private static final Logger log = LoggerFactory.getLogger(JobConsumer.class);

    private final JobProcessingService jobProcessingService;

    public JobConsumer(JobProcessingService jobProcessingService) {
        this.jobProcessingService = jobProcessingService;
    }

    @KafkaListener(topics = JobTopics.UPLOAD_REQUESTED, groupId = "${spring.kafka.consumer.group-id}")
    public void onMessage(JobMessage message, Acknowledgment acknowledgment) {
        log.info("Received job {} from {}", message.jobId(), JobTopics.UPLOAD_REQUESTED);
        try {
            jobProcessingService.process(message);
        } finally {
            acknowledgment.acknowledge();
        }
    }
}
