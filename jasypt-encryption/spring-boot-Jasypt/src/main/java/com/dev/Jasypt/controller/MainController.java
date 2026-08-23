/**
 * Code developed by Ashfaq (© [Year])
 * GitHub: https://github.com/DarkSharkAsh
 */

package com.dev.Jasypt.controller;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {

	@Value("${mysecretKey}")
	private String secrectKey;

	@Value("${mysecretKey1}")
	private String newsecrectKey;

	
	
	@Value("${myKey}")
	private String Key;

	@GetMapping("/secretkey")
	public String getSecrectKey() {

		return secrectKey;
	}
	
	
	@GetMapping("/key")
	public String getKey() {

		return Key;
	}

	@GetMapping("/secretkey1")
	public String getSecrectKey1() {

		return newsecrectKey;
	}
	
}
