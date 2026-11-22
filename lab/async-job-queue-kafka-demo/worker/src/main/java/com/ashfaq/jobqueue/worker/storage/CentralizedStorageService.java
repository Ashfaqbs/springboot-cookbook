package com.ashfaq.jobqueue.worker.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Simulates fetching a job's file from wherever the control plane staged it
 * ("download to memory") and pushing the processed result to the durable,
 * centralized storage location every downstream reader relies on. In this
 * demo both locations are directories on a shared Docker volume; in a real
 * deployment the incoming side would be a staging bucket/queue and the
 * storage side would be the permanent object store (S3, GCS, etc.).
 */
@Service
public class CentralizedStorageService {

    private final Path storageDir;

    public CentralizedStorageService(@Value("${jobqueue.storage.storage-dir}") String storageDir) {
        this.storageDir = Path.of(storageDir);
        try {
            Files.createDirectories(this.storageDir);
        } catch (IOException e) {
            throw new UncheckedIOException("Unable to create centralized storage directory: " + storageDir, e);
        }
    }

    public byte[] download(String incomingPath) {
        try {
            return Files.readAllBytes(Path.of(incomingPath));
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read incoming file: " + incomingPath, e);
        }
    }

    public String upload(String jobId, String originalFileName, byte[] content) {
        Path target = storageDir.resolve(jobId + "_" + originalFileName);
        try {
            Files.write(target, content);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to write file to centralized storage: " + target, e);
        }
        return target.toString();
    }
}
