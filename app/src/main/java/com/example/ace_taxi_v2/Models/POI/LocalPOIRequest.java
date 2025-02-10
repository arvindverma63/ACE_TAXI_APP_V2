package com.example.ace_taxi_v2.Models.POI;


public class LocalPOIRequest {
    public String searchTerm;

    public LocalPOIRequest(String searchTerm){
        this.searchTerm = searchTerm;
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }
}
