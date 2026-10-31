package com.ashfaq.example.lazy;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
@Lazy
public class SampleService {

	public SampleService() {
		System.out.println("SampleService initialized");
	}

	public String getData() {
		System.out.println("SampleService.getData() called");
		return "Hello from SampleService!";
	}

}
