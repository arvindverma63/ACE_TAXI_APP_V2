package com.app.ace_taxi_v2.Models.AddressIO;

public class GetAddressLocationRequest {
    private Location location;

    public GetAddressLocationRequest(double latitude, double longitude) {
        this.location = new Location(latitude, longitude);
    }

    public static class Location {
        private double latitude;
        private double longitude;

        public Location(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }
}
