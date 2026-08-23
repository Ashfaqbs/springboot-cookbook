package com.ashfaq.dev.ai.controller;

import org.springframework.ai.ollama.OllamaChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/ai")
public class AIController {

	@Autowired
	private OllamaChatClient chatClient;
	@GetMapping("/prompt/{chat}")
	public String promptResponse(@PathVariable("chat") String prompt) {

		String response = chatClient.call(prompt);

		return response;

	}
	

}
