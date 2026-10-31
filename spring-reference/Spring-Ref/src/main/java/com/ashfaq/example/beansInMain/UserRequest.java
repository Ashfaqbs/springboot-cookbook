package com.ashfaq.example.beansInMain;

public class UserRequest {
    
    // @NotBlank(message = "Name cannot be blank") Can add Validation lib
    private String name;

    // @Email(message = "Invalid email format")
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
