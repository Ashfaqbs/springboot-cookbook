package com.ashfaq.example.executiontimesample;

import org.springframework.stereotype.Service;

@Service
public class OrderService {

	@LogExecutionTime
    public void processOrder() {
        System.out.println("Processing order...");
        try {
            // Simulating some delay in processing
            Thread.sleep(3000);  // 3-second delay to simulate long-running task
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Order processed.");
    }
}
