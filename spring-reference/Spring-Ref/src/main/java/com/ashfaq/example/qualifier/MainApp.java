package com.ashfaq.example.qualifier;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class MainApp implements CommandLineRunner {

	@Autowired
	@Qualifier("developerEmployee")
	private Employee developerEmployee;

	@Autowired
	@Qualifier("managerEmployee")
	private Employee managerEmployee;

	@Override
	public void run(String... args) throws Exception {
		
//		System.out.println("Developer Employee: " + developerEmployee);
//		System.out.println("Manager Employee: " + managerEmployee);
//		Developer Employee: Employee{name='John Doe', role='Developer'}
//		Manager Employee: Employee{name='Jane Smith', role='Manager'}
	}

	public static void main(String[] args) {
		System.out.println("data from main app");
	}
}
