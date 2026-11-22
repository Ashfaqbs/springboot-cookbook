package com.ashfaq.jobqueue.worker.repository;

import com.ashfaq.jobqueue.worker.domain.Job;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JobRepository extends JpaRepository<Job, UUID> {
}
