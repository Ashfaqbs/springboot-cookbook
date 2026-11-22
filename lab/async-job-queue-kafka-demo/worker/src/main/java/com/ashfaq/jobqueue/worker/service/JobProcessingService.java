package com.ashfaq.jobqueue.worker.service;

import com.ashfaq.jobqueue.worker.domain.Job;
import com.ashfaq.jobqueue.worker.domain.JobStatus;
import com.ashfaq.jobqueue.worker.messaging.JobMessage;
import com.ashfaq.jobqueue.worker.repository.JobRepository;
import com.ashfaq.jobqueue.worker.storage.CentralizedStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class JobProcessingService {

    private static final Logger log = LoggerFactory.getLogger(JobProcessingService.class);

    private final JobRepository jobRepository;
    private final CentralizedStorageService centralizedStorageService;

    public JobProcessingService(JobRepository jobRepository, CentralizedStorageService centralizedStorageService) {
        this.jobRepository = jobRepository;
        this.centralizedStorageService = centralizedStorageService;
    }

    /**
     * Processes one queued job: mark in-progress, move the file from the
     * incoming staging path to centralized storage, mark completed. Any
     * failure is captured on the job row as FAILED rather than rethrown -
     * the caller (the Kafka listener) still acknowledges the message, since
     * this demo has no retry topic or dead-letter queue.
     */
    @Transactional
    public void process(JobMessage message) {
        UUID jobId = UUID.fromString(message.jobId());
        Job job = jobRepository.findById(jobId).orElse(null);
        if (job == null) {
            log.warn("Ignoring message for unknown job {}", jobId);
            return;
        }

        job.setStatus(JobStatus.IN_PROGRESS);
        jobRepository.save(job);

        try {
            byte[] content = centralizedStorageService.download(message.incomingPath());
            String storagePath = centralizedStorageService.upload(message.jobId(), message.originalFileName(), content);

            job.setStoragePath(storagePath);
            job.setStatus(JobStatus.COMPLETED);
            jobRepository.save(job);
            log.info("Job {} completed, stored at {}", jobId, storagePath);
        } catch (RuntimeException e) {
            log.error("Job {} failed", jobId, e);
            job.setStatus(JobStatus.FAILED);
            job.setErrorMessage(e.getMessage());
            jobRepository.save(job);
        }
    }
}
