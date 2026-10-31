package com.ashfaq.example.qualifier;

public class Employee {
	private String name;
	private String role;

	// Constructor
	public Employee(String name, String role) {
		this.name = name;
		this.role = role;
	}

	// Getters and Setters
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	@Override
	public String toString() {
		return "Employee{name='" + name + "', role='" + role + "'}";
	}
}
