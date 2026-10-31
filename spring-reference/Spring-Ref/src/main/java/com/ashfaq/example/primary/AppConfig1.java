package com.ashfaq.example.primary;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class AppConfig1 {

    @Bean
    @Primary
    public XEmployee employeeOne() {
        return new XEmployee("John Doe", "Developer");
    }

    @Bean
    public XEmployee employeeTwo() {
        return new XEmployee("Jane Smith", "Manager");
    }
}
