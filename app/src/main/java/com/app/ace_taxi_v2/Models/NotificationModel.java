package com.app.ace_taxi_v2.Models;

public class NotificationModel {
    private String jobId;
    private String navId;
    private String title;
    private String message;

    // Constructor
    public NotificationModel(String jobId, String navId, String title, String message) {
        this.jobId = jobId != null ? jobId : "N/A";
        this.navId = navId != null ? navId : "N/A";
        this.title = title != null ? title : "Untitled";
        this.message = message != null ? message : "No message available";
    }

    // Getters & Setters
    public String getJobId() { return jobId; }
    public void setJobId(String jobId) { this.jobId = jobId; }

    public String getNavId() { return navId; }
    public void setNavId(String navId) { this.navId = navId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    @Override
    public String toString() {
        return "NotificationModel{" +
                "jobId='" + jobId + '\'' +
                ", navId='" + navId + '\'' +
                ", title='" + title + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
