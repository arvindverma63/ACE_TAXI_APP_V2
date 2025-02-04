package com.example.ace_taxi_v2.Models.Jobs;

import java.util.List;

public class DateRangeList {
    public List<DateRangeResponse> bookings;

    public List<DateRangeResponse> getBookings() {
        return bookings;
    }

    public void setBookings(List<DateRangeResponse> booking) {
        this.bookings = booking;
    }
}
