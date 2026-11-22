package com.ashfaq.jobqueue.controlplane.service;

import com.ashfaq.jobqueue.controlplane.domain.Job;
import com.ashfaq.jobqueue.controlplane.domain.JobStatus;
import com.ashfaq.jobqueue.controlplane.exception.InvalidUploadException;
import com.ashfaq.jobqueue.controlplane.exception.JobNotFoundException;
import com.ashfaq.jobqueue.controlplane.messaging.JobMessage;
import com.ashfaq.jobqueue.controlplane.messaging.JobProducer;
import com.ashfaq.jobqueue.controlplane.repository.JobRepository;
import com.ashfaq.jobqueue.controlplane.storage.IncomingStorageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JobServiceTest {

    @Mock
    private JobRepository jobRepository;

    @Mock
    private IncomingStorageService incomingStorageService;

    @Mock
    private JobProducer jobProducer;

    private JobService jobService;

    private JobService newService() {
        return new JobService(jobRepository, incomingStorageService, jobProducer);
    }

    @Test
    void shouldQueueJobAndPublishMessageWhenSubmitIsValid() {
        jobService = newService();
        MockMultipartFile file = new MockMultipartFile("file", "report.csv", "text/csv", "a,b,c".getBytes());
        when(incomingStorageService.store(any(UUID.class), any())).thenReturn("/data/incoming/generated_report.csv");

        Job job = jobService.submitJob("nightly-report", file);

        assertThat(job.getStatus()).isEqualTo(JobStatus.QUEUED);
        assertThat(job.getName()).isEqualTo("nightly-report");
        assertThat(job.getOriginalFileName()).isEqualTo("report.csv");

        verify(jobRepository).save(job);

        ArgumentCaptor<JobMessage> messageCaptor = ArgumentCaptor.forClass(JobMessage.class);
        verify(jobProducer).publishUploadRequested(messageCaptor.capture());
        JobMessage published = messageCaptor.getValue();
        assertThat(published.jobId()).isEqualTo(job.getId().toString());
        assertThat(published.incomingPath()).isEqualTo("/data/incoming/generated_report.csv");
    }

    @Test
    void shouldRejectBlankJobName() {
        jobService = newService();
        MockMultipartFile file = new MockMultipartFile("file", "report.csv", "text/csv", "a,b,c".getBytes());

        assertThatThrownBy(() -> jobService.submitJob("  ", file))
                .isInstanceOf(InvalidUploadException.class);

        verifyNoInteractions(jobRepository, jobProducer);
    }

    @Test
    void shouldRejectEmptyFile() {
        jobService = newService();
        MockMultipartFile emptyFile = new MockMultipartFile("file", "empty.csv", "text/csv", new byte[0]);

        assertThatThrownBy(() -> jobService.submitJob("nightly-report", emptyFile))
                .isInstanceOf(InvalidUploadException.class);

        verifyNoInteractions(jobRepository, jobProducer);
    }

    @Test
    void shouldReturnJobWhenIdExists() {
        jobService = newService();
        UUID id = UUID.randomUUID();
        Job job = new Job(id, "nightly-report", "report.csv", "/data/incoming/x", JobStatus.COMPLETED);
        when(jobRepository.findById(id)).thenReturn(Optional.of(job));

        Job result = jobService.getJob(id);

        assertThat(result).isSameAs(job);
    }

    @Test
    void shouldThrowWhenJobIdDoesNotExist() {
        jobService = newService();
        UUID id = UUID.randomUUID();
        when(jobRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> jobService.getJob(id))
                .isInstanceOf(JobNotFoundException.class);
    }
}
