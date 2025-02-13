package com.app.ace_taxi_v2.Logic.Service;

import android.content.Context;
import android.content.SharedPreferences;

public class CurrentBookingSession {
    private static final String PREF_NAME = "Current_status";
    private static final String KEY_BOOKING_SHIFT = "booking_shift";
    private static final String KEY_BOOKING_ID = "booking_id";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public CurrentBookingSession(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }


    // Save booking shift
    public void saveBookingShift(String bookingShift) {
        editor.putString(KEY_BOOKING_SHIFT, bookingShift);
        editor.apply();
    }

    // Retrieve booking shift
    public String getBookingShift() {
        return sharedPreferences.getString(KEY_BOOKING_SHIFT, null);
    }

    // Save booking ID
    public void saveBookingId(String id) {
        editor.putString(KEY_BOOKING_ID, id);
        editor.apply(); // **Fix: Apply changes**
    }

    // Retrieve booking ID
    public String getBookingId() {
        return sharedPreferences.getString(KEY_BOOKING_ID, null);
    }


    // Optional: Clear only booking-related data (keeping other stored preferences)
    public void clearBookingData() {
        editor.remove(KEY_BOOKING_ID);
        editor.remove(KEY_BOOKING_SHIFT);
        editor.apply();
    }
}
