package com.app.ace_taxi_v2.Models.AddressIO;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class AddressIOLocationResponse {

    @SerializedName("suggestions")
    private List<Suggestion> suggestions;

    public List<Suggestion> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(List<Suggestion> suggestions) {
        this.suggestions = suggestions;
    }

    public static class Suggestion {

        @SerializedName("distance")
        private double distance;

        @SerializedName("location")
        private String location;

        @SerializedName("url")
        private String url;

        @SerializedName("id")
        private String id;

        public double getDistance() {
            return distance;
        }

        public void setDistance(double distance) {
            this.distance = distance;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
}
