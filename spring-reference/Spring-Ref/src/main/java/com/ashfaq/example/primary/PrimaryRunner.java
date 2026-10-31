package com.ashfaq.example.primary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class PrimaryRunner implements CommandLineRunner {
	// Notice that we don't use @Qualifier here`
	@Autowired
	private XEmployee employee;

	@Override
	public void run(String... args) throws Exception {
		printEmployeeDetails();
	}

	public void printEmployeeDetails() {
//		System.out.println("Injected XEmployee: " + employee);
//		Injected Employee: XEmployee{name='John Doe', role='Developer'}
	}

}
