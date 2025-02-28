package com.app.ace_taxi_v2.Components;

import android.content.Context;
import android.content.SharedPreferences;

public class BookingStartStatus {
    private static final String PREF_NAME = "booking_prefs";
    private static final String BOOKING_ID_KEY = "booking_id";

    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;

    public BookingStartStatus(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    // Save booking ID
    public void setBookingId(String bookingId) {
        editor.putString(BOOKING_ID_KEY, bookingId);
        editor.apply();
    }

    // Retrieve booking ID
    public String getBookingId() {
        return sharedPreferences.getString(BOOKING_ID_KEY, null);
    }

    // Clear booking ID (if needed)
    public void clearBookingId() {
        editor.remove(BOOKING_ID_KEY);
        editor.apply();
    }
}
