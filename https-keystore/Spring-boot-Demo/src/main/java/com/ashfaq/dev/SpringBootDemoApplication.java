package com.ashfaq.dev;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class SpringBootDemoApplication implements CommandLineRunner {

	@Autowired
	Sample sample;

	@GetMapping("/name")
	public String appNameCaller() {

		return sample.getAppName();

	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println("App Name from CommandLineRunner: " + sample.appName);
	}

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(SpringBootDemoApplication.class, args);

//		Environment env = context.getEnvironment();
//		String appName = env.getProperty("spring.application.name");
	}

}
