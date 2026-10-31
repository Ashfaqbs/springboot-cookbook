package com.ashfaq.dev.InsuranceManager;

import java.time.LocalDate;

public class Insurance {

	private String holder;
	private String type;
	private String company;
	private LocalDate expiryDate; // Changed type to LocalDate

	public Insurance() {
	}

	public Insurance(String holderName, String type, String company, LocalDate expiryDate) {
		this.holder = holderName;
		this.type = type;
		this.company = company;
		this.expiryDate = expiryDate;
	}

	// Getters and setters

	public String getHolderName() {
		return holder;
	}

	public void setHolderName(String holderName) {
		this.holder = holderName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public LocalDate getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(LocalDate expiryDate) {
		this.expiryDate = expiryDate;
	}

	@Override
	public String toString() {
		return "Insurance [holder=" + holder + ", type=" + type + ", company=" + company + ", expiryDate=" + expiryDate
				+ "]";
	}

	

}
