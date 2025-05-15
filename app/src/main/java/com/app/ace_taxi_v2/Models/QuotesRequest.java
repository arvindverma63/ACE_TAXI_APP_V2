package com.app.ace_taxi_v2.Models;

import java.time.LocalDateTime;
import java.util.List;

public class QuotesRequest {
    private String pickupPostcode;
    private List<String> viaPostcodes;
    private String destinationPostcode;
    private LocalDateTime pickupDateTime;
    private int passengers;
    private boolean priceFromBase;

    // Constructors
    public QuotesRequest() {}

    public QuotesRequest(String pickupPostcode, List<String> viaPostcodes, String destinationPostcode,
                         LocalDateTime pickupDateTime, int passengers, boolean priceFromBase) {
        this.pickupPostcode = pickupPostcode;
        this.viaPostcodes = viaPostcodes;
        this.destinationPostcode = destinationPostcode;
        this.pickupDateTime = pickupDateTime;
        this.passengers = passengers;
        this.priceFromBase = priceFromBase;
    }

    // Getters and Setters
    public String getPickupPostcode() { return pickupPostcode; }
    public void setPickupPostcode(String pickupPostcode) { this.pickupPostcode = pickupPostcode; }

    public List<String> getViaPostcodes() { return viaPostcodes; }
    public void setViaPostcodes(List<String> viaPostcodes) { this.viaPostcodes = viaPostcodes; }

    public String getDestinationPostcode() { return destinationPostcode; }
    public void setDestinationPostcode(String destinationPostcode) { this.destinationPostcode = destinationPostcode; }

    public LocalDateTime getPickupDateTime() { return pickupDateTime; }
    public void setPickupDateTime(LocalDateTime pickupDateTime) { this.pickupDateTime = pickupDateTime; }

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
