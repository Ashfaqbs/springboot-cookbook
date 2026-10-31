package com.ashfaq.example.EventListener;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class EventListenerClass {

    @EventListener
    public void handleCustomEvent(CustomEvent event) {
        System.out.println("Event Received: " + event.getMessage());
    }
}
