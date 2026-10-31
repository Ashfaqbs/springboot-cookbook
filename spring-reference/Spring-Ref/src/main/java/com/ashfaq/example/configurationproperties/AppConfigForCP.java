package com.ashfaq.example.configurationproperties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app")
public class AppConfigForCP {
	private String name;
	private String version;
	private String description;

	// Getters and Setters
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

//	public AppConfigForCP(String name, String version, String description) {
//		this.name = name;
//		this.version = version;
//		this.description = description;
//		System.out.println("Configuration Properties initialized");
//		System.out.println("Value of name is " + name);
//		System.out.println("Value of version is " + version);
//		System.out.println("Value of description is " + description);
//		System.out.println("Configuration Properties END");
//
//	}

}
