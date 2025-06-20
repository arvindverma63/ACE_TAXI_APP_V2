package com.app.ace_taxi_v2.JobModals;

public class BookingRequest {

    private String pickup;
    private String pickupPostcode;
    private String destination;
    private String destinationPostcode;
    private String name;
    private int userid;
    private int durationMinutes;
    private double mileage;
    private String mileageText;
    private String durationText;
    private double price;

    // Getters and Setters

    public String getPickup() {
        return pickup;
    }

    public void setPickup(String pickup) {
        this.pickup = pickup;
    }

    public String getPickupPostcode() {
        return pickupPostcode;
    }

    public void setPickupPostcode(String pickupPostcode) {
        this.pickupPostcode = pickupPostcode;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDestinationPostcode() {
        return destinationPostcode;
    }

    public void setDestinationPostcode(String destinationPostcode) {
        this.destinationPostcode = destinationPostcode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public double getMileage() {
        return mileage;
    }

    public void setMileage(double mileage) {
        this.mileage = mileage;
    }

    public String getMileageText() {
        return mileageText;
    }

    public void setMileageText(String mileageText) {
        this.mileageText = mileageText;
    }

    public String getDurationText() {
        return durationText;
    }

    public void setDurationText(String durationText) {
        this.durationText = durationText;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
