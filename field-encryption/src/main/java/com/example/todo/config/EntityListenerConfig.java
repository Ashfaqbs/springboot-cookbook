package com.example.todo.config;

import com.example.todo.crypto.EncryptionService;
import com.example.todo.listener.TodoHashListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

/**
 * Wires the EncryptionService into TodoHashListener's static field.
 *
 * This runs during Spring context startup, before any JPA persist/update operations,
 * so the listener is always ready when Hibernate fires @PrePersist / @PreUpdate.
 */
@Configuration
public class EntityListenerConfig {

    @Autowired
    public void configureListeners(EncryptionService encryptionService) {
        TodoHashListener.setEncryptionService(encryptionService);
    }
}
