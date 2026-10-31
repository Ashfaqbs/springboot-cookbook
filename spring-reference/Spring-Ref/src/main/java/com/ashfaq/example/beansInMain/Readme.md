### Accessing Beans in the `main` Method of a Spring Boot Application

In a Spring Boot application, beans are managed by the Spring Context. If you need to access a service bean inside the `main` method, you need to retrieve the `ApplicationContext` and fetch the bean from it.

---

## **1. Calling Service Functions Inside `main` Method**
### **Example Setup**
Let's assume we have a `UserService` with a method `getUserDetails()`.

#### **UserService.java (Service Bean)**
```java
package com.example.demo.service;

import org.springframework.stereotype.Service;

@Service
public class UserService {
    public String getUserDetails() {
        return "User: Mohammed Ashfaq, Role: Developer";
    }
}
```

#### **Main Class: Accessing `UserService` in `main`**
```java
package com.example.demo;

import com.example.demo.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class DemoApplication {
    public static void main(String[] args) {
        // Start the application and get the ApplicationContext
        ApplicationContext context = SpringApplication.run(DemoApplication.class, args);

        // Get the UserService bean from the context
        UserService userService = context.getBean(UserService.class);
        
        // Call the function
        String userDetails = userService.getUserDetails();
        System.out.println(userDetails);
    }
}
```
---
## **2. Calling Static Utility Functions Inside `main`**
Static methods do not require a Spring bean, so you can call them directly.

#### **Utility Class: `AppUtils.java`**
```java
package com.example.demo.util;

public class AppUtils {
    public static String formatMessage(String message) {
        return "Formatted: " + message.toUpperCase();
    }
}
```

#### **Main Class: Calling `AppUtils`**
```java
package com.example.demo;

import com.example.demo.util.AppUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);

        // Call static utility function directly
        String formattedMessage = AppUtils.formatMessage("hello spring boot");
        System.out.println(formattedMessage);
    }
}
```
---
## **3. Calling Controller APIs from `main` Using `RestTemplate`**
To call a controller method from the `main` method, you can use `RestTemplate`.

### **Controller: `UserController.java`**
```java
package com.example.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {
    
    @GetMapping("/user")
    public String getUser(@RequestHeader Map<String, String> headers) {
        return "User Data with Headers: " + headers;
    }
}
```

### **Main Class: Calling Controller API with Headers**
```java
package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.Collections;

@SpringBootApplication
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);

        String url = "http://localhost:8080/api/user"; // API URL

        // Create RestTemplate instance
        RestTemplate restTemplate = new RestTemplate();

        // Create HTTP Headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer my-jwt-token"); // Example header
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        // Create HTTP Entity with headers
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Make the GET request
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        // Print the response
        System.out.println("Response: " + response.getBody());
    }
}
```
---
### **Summary**
1. **Service Beans** → Retrieve from `ApplicationContext` and call methods.
2. **Utility Methods** → Call directly if they are `static`.
3. **Controller APIs** → Use `RestTemplate`, set headers, and send HTTP requests.

---




Let's design a **better API** that:  
✔ Accepts a **structured JSON request body** (e.g., user details).  
✔ Requires **request headers**, including a custom header.  
✔ Responds with a **customized JSON response**.  
✔ Includes **validation** and proper handling of headers.  
✔ Will be called from the `main` method using `RestTemplate`.  

---

## **1. Designing the API**
We'll create a `UserController` with an endpoint `/api/user/register` that:  
✅ **Consumes JSON** (User details).  
✅ **Requires headers** (e.g., `Authorization` and `Custom-Header`).  
✅ **Responds with structured JSON** (e.g., `userId`, `status`).  

---

### **Controller: `UserController.java`**
```java
package com.example.demo.controller;

import com.example.demo.model.UserRequest;
import com.example.demo.model.UserResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

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
```
---

## **2. Request and Response Models**
### **UserRequest.java (Request Body Model)**
```java
package com.example.demo.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class UserRequest {
    
    @NotBlank(message = "Name cannot be blank")
    private String name;

    @Email(message = "Invalid email format")
    private String email;

    public UserRequest() {}

    public UserRequest(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
```
---

### **UserResponse.java (Response Model)**
```java
package com.example.demo.model;

public class UserResponse {
    private String userId;
    private String status;

    public UserResponse(String userId, String status) {
        this.userId = userId;
        this.status = status;
    }

    public String getUserId() { return userId; }
    public String getStatus() { return status; }
}
```
---

## **3. Calling API from `main` using `RestTemplate`**
Now, we'll call this API from the `main` method.  
We'll:  
✅ Send **JSON body**.  
✅ Add **Authorization header** (`Bearer Token`).  
✅ Add **Custom header**.  
✅ Handle the **response** properly.  

### **Updated `DemoApplication.java`**
```java
package com.example.demo;

import com.example.demo.model.UserRequest;
import com.example.demo.model.UserResponse;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@SpringBootApplication
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);

        String url = "http://localhost:8080/api/user/register"; // API endpoint

        // Creating RestTemplate instance
        RestTemplate restTemplate = new RestTemplate();

        // Creating JSON request body
        UserRequest request = new UserRequest("Mohammed Ashfaq", "ashfaq@example.com");

        // Creating HTTP Headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("Authorization", "Bearer my-jwt-token"); // Simulating Auth
        headers.set("Custom-Header", "SpecialClient"); // Custom Header

        // Creating HTTP Entity (combining headers + body)
        HttpEntity<UserRequest> entity = new HttpEntity<>(request, headers);

        // Making the POST request
        ResponseEntity<UserResponse> response = restTemplate.exchange(url, HttpMethod.POST, entity, UserResponse.class);

        // Printing the response
        if (response.getStatusCode() == HttpStatus.OK) {
            UserResponse userResponse = response.getBody();
            System.out.println("User Registered: " + userResponse.getUserId());
            System.out.println("Status: " + userResponse.getStatus());
        } else {
            System.out.println("Request Failed: " + response.getStatusCode());
        }
    }
}
```
---

## **4. Improvements and Additional Features**
✅ **Validation**: The request body is validated using `@Validated`.  
✅ **Custom Headers**: The API requires a `Custom-Header` in the request.  
✅ **Error Handling**: It handles missing/invalid headers properly.  
✅ **UUID Generation**: Generates a unique user ID for each registration.  
✅ **Structured Response**: Returns a JSON object instead of plain text.  

---

### **Example Request and Response**
📌 **Request JSON (`POST /api/user/register`)**
```json
{
    "name": "Mohammed Ashfaq",
    "email": "ashfaq@example.com"
}
```

📌 **Request Headers**
```
Authorization: Bearer my-jwt-token
Custom-Header: SpecialClient
Content-Type: application/json
```

📌 **Response JSON**
```json
{
    "userId": "e937ad90-5fc9-4cd8-b7cf-7e9c3e5f2e3a",
    "status": "User registered successfully"
}
```

---

### **Summary**
1. **Controller**  
   - Accepts JSON request (`UserRequest`).  
   - Requires `Authorization` and `Custom-Header`.  
   - Returns a structured response (`UserResponse`).  
   - Performs validation.  
2. **Main Method (`RestTemplate`)**  
   - Sends JSON request.  
   - Adds headers properly.  
   - Handles the API response correctly.  

---