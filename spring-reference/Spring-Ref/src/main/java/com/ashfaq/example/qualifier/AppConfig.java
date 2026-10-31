package com.ashfaq.example.qualifier;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class AppConfig {

//	@Bean
//	public Employee employeeOne() {
//		return new Employee("John Doe", "Developer");
//	}
//
//	@Bean
//	public Employee employeeTwo() {
//		return new Employee("Jane Smith", "Manager");
//	}

//	OR .
	@Bean(name = "developerEmployee")
	public Employee employeeOne() {
		return new Employee("John Doe", "Developer");
	}
 
	@Bean(name = "managerEmployee")
	public Employee employeeTwo() {
		return new Employee("Jane Smith", "Manager");
	}
}
