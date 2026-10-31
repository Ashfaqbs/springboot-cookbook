package com.ashfaq.example.EventListener;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class EventPublisher {

    private final ApplicationEventPublisher publisher;

    public EventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    public void publishEvent(String message) {
        CustomEvent event = new CustomEvent(message);
        publisher.publishEvent(event);// who and all are listening will get this 
        //for eg the func handleCustomEvent is listening to this so it will be called
        System.out.println("Event Published: " + message);
    }
}
