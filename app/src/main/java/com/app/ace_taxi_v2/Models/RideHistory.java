package com.app.ace_taxi_v2.Models;

public class RideHistory {
    private final String pickupLocation;
    private final String dropLocation;
    private final String fare;
    private final String date;
    private final String time;

    public RideHistory(String pickupLocation, String dropLocation, String fare, String date, String time) {
        this.pickupLocation = pickupLocation;
        this.dropLocation = dropLocation;
        this.fare = fare;
        this.date = date;
        this.time = time;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }

    public String getDropLocation() {
        return dropLocation;
    }

    public String getFare() {
        return fare;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }
}
