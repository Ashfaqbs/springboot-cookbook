package com.ashfaq.example.service;

import org.springframework.ai.ollama.OllamaChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DataTransformationService {

	@Autowired
	private OllamaChatClient chatClient;

	public String validateAndTransformData(String sourceData, String targetSchema) {
		// Pre-prompt for validation
		String validationPrompt = String.format(
				"Validate if the provided source data matches the target schema or can be converted. "
						+ "Source Data: %s\nTarget Schema: %s\nProvide detailed issues or confirmation.",
				sourceData.trim(), targetSchema.trim());

		// Validation response
		String validationResponse = chatClient.call(validationPrompt);

		// Check if validation failed
		if (validationResponse.contains("cannot")) {
			return "Not able to convert: " + validationResponse;
		}

		// Pre-prompt for cleaning
		String cleaningPrompt = String.format(
				"Clean and transform the source data according to the target schema. "
						+ "Source Data: %s\nTarget Schema: %s\nProvide the cleaned data.",
				sourceData.trim(), targetSchema.trim());

		// Cleaning response
		String cleanedData = chatClient.call(cleaningPrompt);

		return "Successfully transformed data: " + cleanedData;
	}
}
