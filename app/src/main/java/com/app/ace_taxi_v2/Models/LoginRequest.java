package com.app.ace_taxi_v2.Models;
public class LoginRequest {
    private String userName;
    private String password;

    public LoginRequest(String userName,String password){
        this.userName = userName;
        this.password = password;
    }

}
