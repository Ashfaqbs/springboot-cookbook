/**
 * Code developed by Ashfaq (© [Year])
 * GitHub: https://github.com/DarkSharkAsh
 */

package com.main.prometheus.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/metrics")
public class MetricsController {

	@GetMapping("/")
	public String func1() {

		return "is_Online";
	}

	@GetMapping("/test")
	public String func2() {
		return "test";
	}

	@GetMapping("/consume-cpu")
	public void consumeCpu() {
		// Simulate CPU-intensive operation
		for (int i = 0; i < 100000000; i++) {
			Math.sqrt(i);
		}
	}

	@GetMapping("/consume-heap")
	public void consumeHeap() {
		// Simulate heap memory usage
		byte[] data = new byte[1024 * 1024 * 10]; // 10 MB
	}

	@GetMapping("/consume-non-heap")
	public void consumeNonHeap() {
		// Simulate non-heap memory usage
		String s = "This is a non-heap memory usage example";
	}

	@GetMapping("/gc")
	public void runGarbageCollector() {
		// Trigger garbage collection
		System.gc();
	}

	@GetMapping("/consume-cpu-loop")
	public void consumeCpuInLoop() {
		// Simulate CPU-intensive operation
		for (int i = 0; i < 1000000; i++) {
			Math.sin(i);
		}
	}

	@GetMapping("/consume-heavy-heap")
	public void consumeHeavyHeap() {
		// Simulate heap memory usage
		byte[] data = new byte[10024 * 10024 * 1000]; // 100 MB
	}
	
	
	  @GetMapping("/consume-heavy-non-heap")
	    public void consumeHeavyNonHeap() {
	        // Simulate non-heap memory usage
	        List<String> strings = new ArrayList<>();
	        int iterations = 10000000; // Define the number of iterations
	        for (int i = 0; i < iterations; i++) {
	            strings.add("This is a non-heap memory usage example.");
	        }
	    }	
	  
	  
	  @GetMapping("/consume-heavy-cpu")
	    public void consumeHeavyCpu() {
	        // Simulate heavy CPU usage
	        int iterations = 1000000000; // Adjust the number of iterations as needed
	        for (int i = 0; i < iterations; i++) {
	            Math.sin(i);
	        }
	    }

	  @GetMapping("/consume-heavy-cpu2")
	    public void consumeHeavyCpu2() {
		  int numThreads = 200; // Number of threads to spawn

	        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);

	        for (int i = 0; i < numThreads; i++) {
	            executorService.submit(() -> {
	                while (true) {
	                    // Simulate heavy CPU usage
	                }
	            });
	        }
	    }
	  
}
