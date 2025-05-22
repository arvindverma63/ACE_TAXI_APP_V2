package com.app.ace_taxi_v2.Models.AddressIO;

import com.google.gson.annotations.SerializedName;

public class PostcodeResponse {

    @SerializedName("postcode")
    private String postcode;

    public String getPostcode() {
        return postcode;
    }
}
