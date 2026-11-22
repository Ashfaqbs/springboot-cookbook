package com.ashfaq.jobqueue.controlplane.service;

import com.ashfaq.jobqueue.controlplane.domain.Job;
import com.ashfaq.jobqueue.controlplane.domain.JobStatus;
import com.ashfaq.jobqueue.controlplane.exception.InvalidUploadException;
import com.ashfaq.jobqueue.controlplane.exception.JobNotFoundException;
import com.ashfaq.jobqueue.controlplane.messaging.JobMessage;
import com.ashfaq.jobqueue.controlplane.messaging.JobProducer;
import com.ashfaq.jobqueue.controlplane.repository.JobRepository;
import com.ashfaq.jobqueue.controlplane.storage.IncomingStorageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
public class JobService {

    private final JobRepository jobRepository;
    private final IncomingStorageService incomingStorageService;
    private final JobProducer jobProducer;

    public JobService(JobRepository jobRepository, IncomingStorageService incomingStorageService, JobProducer jobProducer) {
        this.jobRepository = jobRepository;
        this.incomingStorageService = incomingStorageService;
        this.jobProducer = jobProducer;
    }

    @Transactional
    public Job submitJob(String name, MultipartFile file) {
        if (!StringUtils.hasText(name)) {
            throw new InvalidUploadException("Job name must not be blank");
        }
        if (file == null || file.isEmpty()) {
            throw new InvalidUploadException("Uploaded file must not be empty");
        }

        UUID jobId = UUID.randomUUID();
        String incomingPath = incomingStorageService.store(jobId, file);
        String originalFileName = StringUtils.hasText(file.getOriginalFilename()) ? file.getOriginalFilename() : "upload.bin";

        Job job = new Job(jobId, name, originalFileName, incomingPath, JobStatus.QUEUED);
        jobRepository.save(job);

        jobProducer.publishUploadRequested(new JobMessage(jobId.toString(), name, originalFileName, incomingPath));

        return job;
    }

    @Transactional(readOnly = true)
    public Job getJob(UUID jobId) {
        return jobRepository.findById(jobId).orElseThrow(() -> new JobNotFoundException(jobId));
    }
}
