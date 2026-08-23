package com.ashfaq.dev.controller;


import java.util.Collections;

import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HeaderExampleController {

    @GetMapping("/example")
    public ResponseEntity<String> exampleEndpoint() {
        HttpHeaders headers = new HttpHeaders();
        
        // Adding custom headers
        headers.add("Custom-Header", "CustomValue");
        headers.add("Another-Header", "AnotherValue");
        
        // Adding standard headers
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setCacheControl(CacheControl.noCache());
        headers.set("User-Agent", "PostmanRuntime/7.28.4");
        headers.set("Authorization", "Bearer your_token_here");
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        
        return new ResponseEntity<>("Headers added to response", headers, HttpStatus.OK);
    }
}
