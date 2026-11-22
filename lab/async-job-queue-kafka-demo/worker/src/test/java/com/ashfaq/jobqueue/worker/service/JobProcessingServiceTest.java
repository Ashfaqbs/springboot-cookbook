package com.ashfaq.jobqueue.worker.service;

import com.ashfaq.jobqueue.worker.domain.Job;
import com.ashfaq.jobqueue.worker.domain.JobStatus;
import com.ashfaq.jobqueue.worker.messaging.JobMessage;
import com.ashfaq.jobqueue.worker.repository.JobRepository;
import com.ashfaq.jobqueue.worker.storage.CentralizedStorageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JobProcessingServiceTest {

    @Mock
    private JobRepository jobRepository;

    @Mock
    private CentralizedStorageService centralizedStorageService;

    private JobProcessingService jobProcessingService;

    @Test
    void shouldMarkJobCompletedWhenStorageSucceeds() {
        jobProcessingService = new JobProcessingService(jobRepository, centralizedStorageService);
        UUID id = UUID.randomUUID();
        String incomingPath = "/data/incoming/" + id + "_report.csv";
        Job job = new Job(id, "nightly-report", "report.csv", incomingPath, JobStatus.QUEUED);
        JobMessage message = new JobMessage(id.toString(), "nightly-report", "report.csv", incomingPath);

        when(jobRepository.findById(id)).thenReturn(Optional.of(job));
        when(centralizedStorageService.download(incomingPath)).thenReturn("a,b,c".getBytes());
        when(centralizedStorageService.upload(eq(id.toString()), eq("report.csv"), any()))
                .thenReturn("/data/storage/" + id + "_report.csv");

        jobProcessingService.process(message);

        assertThat(job.getStatus()).isEqualTo(JobStatus.COMPLETED);
        assertThat(job.getStoragePath()).isEqualTo("/data/storage/" + id + "_report.csv");
        verify(jobRepository, times(2)).save(job);
    }

    @Test
    void shouldMarkJobFailedWhenStorageThrows() {
        jobProcessingService = new JobProcessingService(jobRepository, centralizedStorageService);
        UUID id = UUID.randomUUID();
        String incomingPath = "/data/incoming/" + id + "_report.csv";
        Job job = new Job(id, "nightly-report", "report.csv", incomingPath, JobStatus.QUEUED);
        JobMessage message = new JobMessage(id.toString(), "nightly-report", "report.csv", incomingPath);

        when(jobRepository.findById(id)).thenReturn(Optional.of(job));
        when(centralizedStorageService.download(incomingPath))
                .thenThrow(new RuntimeException("disk unavailable"));

        jobProcessingService.process(message);

        assertThat(job.getStatus()).isEqualTo(JobStatus.FAILED);
        assertThat(job.getErrorMessage()).isEqualTo("disk unavailable");
    }

    @Test
    void shouldSkipMessageWhenJobIsUnknown() {
        jobProcessingService = new JobProcessingService(jobRepository, centralizedStorageService);
        UUID id = UUID.randomUUID();
        JobMessage message = new JobMessage(id.toString(), "nightly-report", "report.csv", "/data/incoming/x");
        when(jobRepository.findById(id)).thenReturn(Optional.empty());

        jobProcessingService.process(message);

        verify(jobRepository, never()).save(any());
    }
}
