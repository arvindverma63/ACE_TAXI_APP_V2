package com.app.ace_taxi_v2.Models.Jobs;

import java.util.List;

public class FutureJobResponse {
    private List<Booking> bookings;

    public List<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }
}
