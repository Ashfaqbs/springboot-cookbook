package com.ashfaq.example.lazy;

import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
class SampleController {

	private final SampleService myService;

	// Constructor-based dependency injection
	public SampleController(@Lazy SampleService myService) {
		this.myService = myService;
		System.out.println("SampleController initialized");
	}

	@GetMapping("/getData")
	public String getData() {
		System.out.println("Endpoint '/getData' hit");
		return myService.getData();
	}
}