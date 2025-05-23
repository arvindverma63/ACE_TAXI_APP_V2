package com.app.ace_taxi_v2.Models;

import java.io.Serializable;

public class NotificationModel implements Serializable {
    private int serialNumber; // Auto-generated Serial Number
    private String notificationNumber; // Unique identifier generated at insertion
    private String jobId;
    private String navId;
    private String title;
    private String message;
    private String passenger;
    private String datetime;

    // Default constructor (required for Gson deserialization)
    public NotificationModel() {}

    // Constructor (without serialNumber and notificationNumber, set later)
    public NotificationModel(String jobId, String navId, String title, String message, String passenger, String datetime) {
        this.jobId = jobId != null ? jobId : "N/A";
        this.navId = navId != null ? navId : "N/A";
        this.title = title != null ? title : "Untitled";
        this.message = message != null ? message : "No message available";
        this.passenger = passenger != null ? passenger : "No";
        this.datetime = datetime != null ? datetime : "dd/mm/yyyy : HH:mm";
    }

    // Getters & Setters
    public int getSerialNumber() { return serialNumber; }
    public void setSerialNumber(int serialNumber) { this.serialNumber = serialNumber; }

    public String getNotificationNumber() { return notificationNumber; }
    public void setNotificationNumber(String notificationNumber) { this.notificationNumber = notificationNumber; }

    public String getJobId() { return jobId; }
    public void setJobId(String jobId) { this.jobId = jobId; }

    public String getNavId() { return navId; }
    public void setNavId(String navId) { this.navId = navId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getPassenger() { return passenger; }
    public void setPassenger(String passenger) { this.passenger = passenger; }

    public String getDatetime() { return datetime; }
    public void setDatetime(String datetime) { this.datetime = datetime; }

    @Override
    public String toString() {
        return "NotificationModel{" +
                "serialNumber=" + serialNumber +
                ", notificationNumber='" + notificationNumber + '\'' +
                ", jobId='" + jobId + '\'' +
                ", navId='" + navId + '\'' +
                ", title='" + title + '\'' +
                ", message='" + message + '\'' +
                ", passenger='" + passenger + '\'' +
                ", datetime='" + datetime + '\'' +
                '}';
    }
}