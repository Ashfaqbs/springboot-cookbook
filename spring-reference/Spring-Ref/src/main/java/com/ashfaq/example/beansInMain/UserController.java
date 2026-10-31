package com.ashfaq.example.beansInMain;


import java.util.UUID;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(
            @RequestHeader HttpHeaders headers,
            @RequestBody @Validated UserRequest request) {

        // Extracting headers
        String authHeader = headers.getFirst("Authorization");
        String customHeader = headers.getFirst("Custom-Header");

        // Validating headers
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new UserResponse(null, "Unauthorized: Missing or invalid token"));
        }

        if (customHeader == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new UserResponse(null, "Missing Custom-Header"));
        }

        // Simulating user registration
        String userId = UUID.randomUUID().toString();
        return ResponseEntity.ok(new UserResponse(userId, "User registered successfully"));
    }
}
