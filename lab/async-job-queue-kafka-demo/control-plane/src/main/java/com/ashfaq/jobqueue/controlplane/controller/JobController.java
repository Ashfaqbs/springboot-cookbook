package com.ashfaq.jobqueue.controlplane.controller;

import com.ashfaq.jobqueue.controlplane.domain.Job;
import com.ashfaq.jobqueue.controlplane.dto.JobStatusResponse;
import com.ashfaq.jobqueue.controlplane.dto.UploadResponse;
import com.ashfaq.jobqueue.controlplane.service.JobService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping("/upload")
    public ResponseEntity<UploadResponse> upload(@RequestParam("file") MultipartFile file,
                                                  @RequestParam("name") String name) {
        Job job = jobService.submitJob(name, file);
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(new UploadResponse(job.getId(), job.getStatus()));
    }

    @GetMapping("/status/{id}")
    public ResponseEntity<JobStatusResponse> status(@PathVariable UUID id) {
        Job job = jobService.getJob(id);
        return ResponseEntity.ok(JobStatusResponse.from(job));
    }
}
