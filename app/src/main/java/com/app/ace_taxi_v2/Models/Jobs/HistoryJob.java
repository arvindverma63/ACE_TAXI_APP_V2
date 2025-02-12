package com.app.ace_taxi_v2.Models.Jobs;

public class HistoryJob {
    private final String pickupDateTime;
    private final int passengers;
    private final String pickupAddress;
    private final String destinationAddress;

    public HistoryJob(String pickupDateTime, int passengers, String pickupAddress, String destinationAddress) {
        this.pickupDateTime = pickupDateTime;
        this.passengers = passengers;
        this.pickupAddress = pickupAddress;
        this.destinationAddress = destinationAddress;
    }

    public String getPickupDateTime() {
        return pickupDateTime;
    }

    public int getPassengers() {
        return passengers;
    }

    public String getPickupAddress() {
        return pickupAddress;
    }

    public String getDestinationAddress() {
        return destinationAddress;
    }
}
