package com.ashfaq.example.configurationproperties;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class SampleRunnerForCP implements CommandLineRunner {

	private AppConfigForCP appConfigForCP;

	public SampleRunnerForCP(AppConfigForCP appConfigForCP) {
		this.appConfigForCP = appConfigForCP;
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println(appConfigForCP.getName());
		System.out.println(appConfigForCP.getVersion());
		System.out.println(appConfigForCP.getDescription());
	}
	
	//OP
//	My Application
//	1.0.0
//	A Spring Boot demo application
}
