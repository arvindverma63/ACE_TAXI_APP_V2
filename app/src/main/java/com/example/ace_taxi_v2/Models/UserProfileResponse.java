package com.example.ace_taxi_v2.Models;
public class UserProfileResponse {
    private String fullName;
    private int id;
    private String email;
    private String phoneNumber;

    // Default Constructor
    public UserProfileResponse() {
    }

    // Parameterized Constructor
    public UserProfileResponse(String fullName, int id, String email, String phoneNumber) {
        this.fullName = fullName;
        this.id = id;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    // Getters and Setters
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // Override toString for Debugging
    @Override
    public String toString() {
        return "UserProfileResponse{" +
                "fullName='" + fullName + '\'' +
                ", id=" + id +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }
}
