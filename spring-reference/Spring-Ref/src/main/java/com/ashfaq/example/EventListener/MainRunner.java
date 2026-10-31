package com.ashfaq.example.EventListener;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class MainRunner implements CommandLineRunner {
	private final EventPublisher eventPublisher;

	public MainRunner(EventPublisher eventPublisher) {
		this.eventPublisher = eventPublisher;
	}

	@Override
	public void run(String... args) throws Exception {
		eventPublisher.publishEvent("Hello from Custom Event!");
//op
//		Event Received: Hello from Custom Event!
//		Event Published: Hello from Custom Event!
	
	}
}
