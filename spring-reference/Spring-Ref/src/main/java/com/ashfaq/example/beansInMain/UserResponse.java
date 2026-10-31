package com.ashfaq.example.beansInMain;


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
