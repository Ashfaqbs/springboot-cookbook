package com.ashfaq.example.controller;

import org.springframework.ai.ollama.OllamaChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ashfaq.example.model.CodeCompletionRequest;
import com.ashfaq.example.model.CodeReviewRequest;

@RestController
@RequestMapping("/api/code")
public class CodeApiController {

	@Autowired
	private OllamaChatClient chatClient;

	// 1. API for Code Completion
	@PostMapping("/complete")
	public String completeCode(@RequestBody CodeCompletionRequest request) {
		// Trim the input prompt and language
		String trimmedCode = request.getCode().trim();
		String language = request.getLanguage().trim();

		// Pre-prompt for code completion
		String prePrompt = "You are a coding assistant. Complete or generate the code based on the given input. "
				+ "Ensure your response strictly adheres to the provided language: " + language + ". "
				+ "Optimize for readability and functionality.";

		// Construct the final prompt
		String prompt = prePrompt + "\nInput Code/Context: " + trimmedCode;

		// Call the OllamaChatClient and return response
		String response = chatClient.call(prompt);
		return response;
	}

	// 2. API for Code Review and Enhancement
	@PostMapping("/review")
	public String reviewCode(@RequestBody CodeReviewRequest request) {
		// Trim the input code and description
		String trimmedCode = request.getCode().trim();

		// Pre-prompt for code review
		String prePrompt = "You are a code reviewer and optimizer. Review the given code and provide feedback. "
				+ "Enhance the code for better performance, readability, and efficiency while retaining its functionality.";

		// Construct the final prompt
		String prompt = prePrompt + "\nInput Code: " + trimmedCode;

		// Call the OllamaChatClient and return response
		String response = chatClient.call(prompt);
		return response;
	}

}