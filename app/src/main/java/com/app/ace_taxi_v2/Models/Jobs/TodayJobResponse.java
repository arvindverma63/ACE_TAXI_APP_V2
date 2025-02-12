package com.app.ace_taxi_v2.Models.Jobs;

import java.util.List;

public class TodayJobResponse {

    private List<TodayBooking> bookings;
    public List<TodayBooking> getBookings() {
        return bookings;
    }

    public void setBookings(List<TodayBooking> bookings) {
        this.bookings = bookings;
    }
}
