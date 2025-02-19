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

    // Retrieve booking shift safely
    public String getBookingShift() {
        return sharedPreferences.getString(KEY_BOOKING_SHIFT, ""); // Avoids null
    }

    // Save booking ID correctly
    public void saveBookingId(int bookingId) {
        editor.putString(KEY_BOOKING_ID, String.valueOf(bookingId));
        editor.apply();
    }

    // Retrieve booking ID safely
    public String getBookingId() {
        return sharedPreferences.getString(KEY_BOOKING_ID, ""); // Avoids null
    }

    // Clear booking data safely
    public void clearBookingData() {
        editor.remove(KEY_BOOKING_ID);
        editor.remove(KEY_BOOKING_SHIFT);
        editor.commit(); // Ensures immediate write
    }
}
