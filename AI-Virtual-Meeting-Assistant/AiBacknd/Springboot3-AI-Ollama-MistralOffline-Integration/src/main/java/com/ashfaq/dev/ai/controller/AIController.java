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

	// this gives data in one shot
//	http://localhost:8080/ai/prompt/what%20is%20the%20latest%20version%20of%20Java
//	resp :
//		As of my knowledge up to October 2021, the latest released version of Oracle JDK and OpenJDK, which are two popular implementations of the Java SE platform, is Java Development Kit (JDK) 16. JDK 16 was released on September 14, 2021. It's important to note that using the latest version may not always be recommended for production environments due to potential stability and compatibility issues. It's a good practice to 
//		evaluate new versions for your specific use case before deploying them in a production environment.
	@GetMapping("/prompt/{chat}")
	public String promptResponse(@PathVariable("chat") String prompt) {

		String response = chatClient.call(prompt);

		return response;

	}

	// data in stream or chunk by chunk
//	http://localhost:8080/ai/prompt?chat=%22hello%20man%22
//	Hello! How can I help you today? If you have any specific questions or topics you'd like to discuss, feel free to ask. Otherwise, I'm here to provide information and answer any queries you may have. Let me know what I can do for you.
//	chunk by chunk

	@GetMapping("/prompt")
	public Flux<String> promptResponsew(@RequestParam("chat") String prompt) {

		System.out.print("Prompt: "  + prompt);
		Flux<String> response = chatClient.stream(prompt);

		return response;

	}

}
