package com.app.ace_taxi_v2.Models;

import java.time.LocalDateTime;
import java.util.List;

public class QuotesRequest {
    private String pickupPostcode;
    private List<String> viaPostcodes[];
    private String destinationPostcode;
    private String pickupDateTime;
    private int passengers;
    private boolean priceFromBase;

    // Constructors
    public QuotesRequest() {}

    public QuotesRequest(String pickupPostcode, String destinationPostcode,
                         String pickupDateTime, int passengers, boolean priceFromBase) {
        this.pickupPostcode = pickupPostcode;
        this.destinationPostcode = destinationPostcode;
        this.pickupDateTime = pickupDateTime;
        this.passengers = passengers;
        this.priceFromBase = priceFromBase;
    }

    // Getters and Setters
    public String getPickupPostcode() { return pickupPostcode; }
    public void setPickupPostcode(String pickupPostcode) { this.pickupPostcode = pickupPostcode; }

    public String getDestinationPostcode() { return destinationPostcode; }
    public void setDestinationPostcode(String destinationPostcode) { this.destinationPostcode = destinationPostcode; }

    public String getPickupDateTime() { return pickupDateTime; }
    public void setPickupDateTime(String pickupDateTime) { this.pickupDateTime = pickupDateTime; }

    public int getPassengers() { return passengers; }
    public void setPassengers(int passengers) { this.passengers = passengers; }

    public boolean isPriceFromBase() { return priceFromBase; }
    public void setPriceFromBase(boolean priceFromBase) { this.priceFromBase = priceFromBase; }

    @Override
    public String toString() {
        return "QuotesRequest{" +
                "pickupPostcode='" + pickupPostcode + '\'' +
                ", viaPostcodes=" + viaPostcodes +
                ", destinationPostcode='" + destinationPostcode + '\'' +
                ", pickupDateTime=" + pickupDateTime +
                ", passengers=" + passengers +
                ", priceFromBase=" + priceFromBase +
                '}';
    }
}
