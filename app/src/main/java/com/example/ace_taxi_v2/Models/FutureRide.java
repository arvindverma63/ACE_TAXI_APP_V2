package com.example.ace_taxi_v2.Models;

public class FutureRide {
    private String rideName;
    private String date;
    private String time;
    private String pickupLocation;
    private String dropLocation;
    private double fare;

    // Constructor
    public FutureRide(String rideName, String date, String time, String pickupLocation, String dropLocation, double fare) {
        this.rideName = rideName;
        this.date = date;
        this.time = time;
        this.pickupLocation = pickupLocation;
        this.dropLocation = dropLocation;
        this.fare = fare;
    }

    // Getters
    public String getRideName() {
        return rideName;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }

    public String getDropLocation() {
        return dropLocation;
    }

    public double getFare() {
        return fare;
    }
}
