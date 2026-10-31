package com.ashfaq.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = { "com.ashfaq.outsidepackage", "com.ashfaq.example" // need to define the default base
																					// package as well
		// else component are not picked from default package
})
@SpringBootApplication
public class SpringRefApplication {

	public static void main(String[] args) {
	// SpringApplication.run(SpringRefApplication.class, args);

	 // Start the application and get the ApplicationContext
        ApplicationContext context = SpringApplication.run(SpringRefApplication.class, args);



        // beansInMain package 
        // Get the UserService bean from the context
        // UserService userService = context.getBean(UserService.class);

        // Call the function
        // String userDetails = userService.getUserDetails();
        // System.out.println(userDetails);
        /*
         * User: Mohammed Ashfaq, Role: Developer
         */


           // Call static utility function directly
        // String formattedMessage = AppUtils.formatMessage("hello spring boot");
        // System.out.println(formattedMessage);


// Calling the API 
        //  String url = "http://localhost:8080/api/user/register"; // API endpoint

        // // Creating RestTemplate instance
        // RestTemplate restTemplate = new RestTemplate();

        // // Creating JSON request body
        // UserRequest request = new UserRequest("Mohammed Ashfaq", "ashfaq@example.com");

        // // Creating HTTP Headers
        // HttpHeaders headers = new HttpHeaders();
        // headers.setContentType(MediaType.APPLICATION_JSON);
        // headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        // headers.set("Authorization", "Bearer my-jwt-token"); // Simulating Auth
        // headers.set("Custom-Header", "SpecialClient"); // Custom Header

        // // Creating HTTP Entity (combining headers + body)
        // HttpEntity<UserRequest> entity = new HttpEntity<>(request, headers);

        // // Making the POST request
        // ResponseEntity<UserResponse> response = restTemplate.exchange(url, HttpMethod.POST, entity, UserResponse.class);

        // // Printing the response
        // if (response.getStatusCode() == HttpStatus.OK) {
        //     UserResponse userResponse = response.getBody();
        //     System.out.println("User Registered: " + userResponse.getUserId());
        //     System.out.println("Status: " + userResponse.getStatus());
        // } else {
        //     System.out.println("Request Failed: " + response.getStatusCode());
        // }
        /*
         * User Registered: 1a213105-b527-4980-9d4f-6bd269a2fd39
Status: User registered successfully
         */




        
	} 




}
