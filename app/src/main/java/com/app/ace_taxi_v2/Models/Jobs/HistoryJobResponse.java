package com.app.ace_taxi_v2.Models.Jobs;

import java.util.List;

public class HistoryJobResponse {
    public List<HistoryBooking> bookings;
    public List<HistoryBooking> getBookings() {
        return bookings;
    }

    public void setBookings(List<HistoryBooking> bookings) {
        this.bookings = bookings;
    }
}
