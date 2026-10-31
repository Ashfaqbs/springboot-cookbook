package com.ashfaq.example.beansInMain;


import org.springframework.stereotype.Service;

@Service
public class UserService {
    public String getUserDetails() {
        return "User: Mohammed Ashfaq, Role: Developer";
    }
}
