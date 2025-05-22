package com.app.ace_taxi_v2.Models.AddressIO;

import com.google.gson.annotations.SerializedName;

public class Suggestion {

    @SerializedName("address")
    private String address;

    @SerializedName("url")
    private String url;

    @SerializedName("id")
    private String id;

    // Getters
    public String getAddress() {
        return address;
    }

    public String getUrl() {
        return url;
    }

    public String getId() {
        return id;
    }
}
