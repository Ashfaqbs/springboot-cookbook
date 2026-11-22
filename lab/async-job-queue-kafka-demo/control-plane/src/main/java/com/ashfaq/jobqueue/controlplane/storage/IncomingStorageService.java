package com.ashfaq.jobqueue.controlplane.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

/**
 * Writes freshly uploaded files to the shared "incoming" staging area that
 * the worker later reads from. This staging step exists to mirror a real
 * deployment, where the control plane and worker run as separate processes
 * and only share a file reference (path or object key), never the raw bytes.
 */
@Service
public class IncomingStorageService {

    private final Path incomingDir;

    public IncomingStorageService(@Value("${jobqueue.storage.incoming-dir}") String incomingDir) {
        this.incomingDir = Path.of(incomingDir);
        try {
            Files.createDirectories(this.incomingDir);
        } catch (IOException e) {
            throw new UncheckedIOException("Unable to create incoming storage directory: " + incomingDir, e);
        }
    }

    public String store(UUID jobId, MultipartFile file) {
        String safeName = StringUtils.cleanPath(file.getOriginalFilename() != null ? file.getOriginalFilename() : "upload.bin");
        Path target = incomingDir.resolve(jobId + "_" + safeName);
        try {
            file.transferTo(target);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to store uploaded file for job " + jobId, e);
        }
        return target.toString();
    }
}
