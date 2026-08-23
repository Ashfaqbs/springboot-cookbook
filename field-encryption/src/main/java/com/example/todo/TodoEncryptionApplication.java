package com.example.todo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan  // picks up @ConfigurationProperties beans automatically
public class TodoEncryptionApplication {

    public static void main(String[] args) {
        SpringApplication.run(TodoEncryptionApplication.class, args);
    }
}
