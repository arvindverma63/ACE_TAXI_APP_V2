package com.example.ace_taxi_v2.Models;

public class NotificationModel {

    private String title;
    private String body;
    private long timestamp;

    public NotificationModel(String title, String body, long timestamp) {
        this.title = title;
        this.body = body;
        this.timestamp = timestamp;
    }

    // Getters
    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
