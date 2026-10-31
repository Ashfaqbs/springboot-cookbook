package com.ashfaq.example.lifecyclemethods.eg2;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Service
public class FileUploadService {

	
//	You have a service that handles uploading and processing files. Once the file is uploaded,
//	it’s saved to a temporary location, and your application processes it in the background. To ensure safety,
//	you want to delete any temporary files when the application shuts down to avoid stale files.
	private final String TEMP_DIR = "tmp/uploads";

	// Constructor: Basic service creation
	public FileUploadService() {
		System.out.println("Constructor: FileUploadService created");
	}

	// @PostConstruct: Create temporary directory for file uploads
	@PostConstruct
	public void setupTempDirectory() {
		System.out.println("PostConstruct: Creating temp directory for file uploads");
		try {
			Path tempDirPath = Paths.get(TEMP_DIR);
			if (!Files.exists(tempDirPath)) {
				Files.createDirectories(tempDirPath);
				System.out.println("PostConstruct: Temp directory created");
			} else {
				System.out.println("PostConstruct: Temp directory already exists");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void uploadFile(byte[] fileData, String fileName) throws IOException {
		Path filePath = Paths.get(TEMP_DIR, fileName);
		Files.write(filePath, fileData);
		System.out.println("File uploaded to: " + filePath.toString());
	}

	// @PreDestroy: Clean up temporary files when the application shuts down
	@PreDestroy
	public void cleanupTempFiles() {
		System.out.println("PreDestroy: Deleting temp files");
		try {
			File tempDir = new File(TEMP_DIR);
			for (File file : tempDir.listFiles()) {
				if (file.isFile()) {
					file.delete();
				}
			}
			System.out.println("PreDestroy: Temp files deleted");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
