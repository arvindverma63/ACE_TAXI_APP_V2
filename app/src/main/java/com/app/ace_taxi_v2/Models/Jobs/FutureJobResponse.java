package com.app.ace_taxi_v2.Models.Jobs;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class FutureJobResponse {
    @SerializedName("bookings")
    private List<Booking> bookings;
    @SerializedName("logout")
    private boolean logout;

    public List<Booking> getBookings() {
        return bookings;
    }

    public boolean isLogout() {
        return logout;
    }
}