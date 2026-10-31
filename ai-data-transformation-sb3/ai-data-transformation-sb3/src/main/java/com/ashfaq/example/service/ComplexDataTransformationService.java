package com.ashfaq.example.service;

import org.springframework.ai.ollama.OllamaChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ComplexDataTransformationService {

	@Autowired
	private OllamaChatClient chatClient;

	public String validateAndTransformData(String sourceData, String targetSchema) {
		// Step 1: Validate Source Data Against Target Schema
		String validationPrompt = String.format(
				"Validate the source data against the provided nested target schema. "
						+ "Identify missing fields, incorrect types, or invalid values. "
						+ "Source Data: %s\nTarget Schema: %s\nProvide a detailed validation report.",
				sourceData.trim(), targetSchema.trim());

		String validationResponse = chatClient.call(validationPrompt);

		if (validationResponse.contains("cannot")) {
			return "Validation Failed: " + validationResponse;
		}

		// Step 2: Transform and Enrich Data
		String transformationPrompt = String.format(
				"Transform the source data to match the target schema, including type casting, "
						+ "adding default values for missing fields, and ensuring nested structures align. "
						+ "Source Data: %s\nTarget Schema: %s\nReturn the transformed data.",
				sourceData.trim(), targetSchema.trim());

		String transformedData = chatClient.call(transformationPrompt);

		return "Successfully transformed data: " + transformedData;
	}
}
