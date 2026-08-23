package com.ashfaq.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class AopSb3J21Application {

	public static void main(String[] args) {
		SpringApplication.run(AopSb3J21Application.class, args);
	}

}
