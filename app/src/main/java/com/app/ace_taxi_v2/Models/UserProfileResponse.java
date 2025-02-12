package com.app.ace_taxi_v2.Models;

public class UserProfileResponse {
    private String fullname;
    private int id;
    private String email;
    private String telephone;
    private String colorCode;
    private String vehicleReg;
    private String vehicleMake;
    private String vehicleModel;
    private String vehicleColour;
    private String fcm;
    private String lastLogin;

    // Default Constructor
    public UserProfileResponse() {
    }

    // Parameterized Constructor
    public UserProfileResponse(String fullname, int id, String email, String telephone,
                               String colorCode, String vehicleReg, String vehicleMake,
                               String vehicleModel, String vehicleColour, String fcm,
                               String lastLogin) {
        this.fullname = fullname;
        this.id = id;
        this.email = email;
        this.telephone = telephone;
        this.colorCode = colorCode;
        this.vehicleReg = vehicleReg;
        this.vehicleMake = vehicleMake;
        this.vehicleModel = vehicleModel;
        this.vehicleColour = vehicleColour;
        this.fcm = fcm;
        this.lastLogin = lastLogin;
    }

    // Getters and Setters
    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
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

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    public String getVehicleReg() {
        return vehicleReg;
    }

    public void setVehicleReg(String vehicleReg) {
        this.vehicleReg = vehicleReg;
    }

    public String getVehicleMake() {
        return vehicleMake;
    }

    public void setVehicleMake(String vehicleMake) {
        this.vehicleMake = vehicleMake;
    }

    public String getVehicleModel() {
        return vehicleModel;
    }

    public void setVehicleModel(String vehicleModel) {
        this.vehicleModel = vehicleModel;
    }

    public String getVehicleColour() {
        return vehicleColour;
    }

    public void setVehicleColour(String vehicleColour) {
        this.vehicleColour = vehicleColour;
    }

    public String getFcm() {
        return fcm;
    }

    public void setFcm(String fcm) {
        this.fcm = fcm;
    }

    public String getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(String lastLogin) {
        this.lastLogin = lastLogin;
    }

    // Override toString for Debugging
    @Override
    public String toString() {
        return "UserProfileResponse{" +
                "fullname='" + fullname + '\'' +
                ", id=" + id +
                ", email='" + email + '\'' +
                ", telephone='" + telephone + '\'' +
                ", colorCode='" + colorCode + '\'' +
                ", vehicleReg='" + vehicleReg + '\'' +
                ", vehicleMake='" + vehicleMake + '\'' +
                ", vehicleModel='" + vehicleModel + '\'' +
                ", vehicleColour='" + vehicleColour + '\'' +
                ", fcm='" + fcm + '\'' +
                ", lastLogin='" + lastLogin + '\'' +
                '}';
    }
}
