package com.app.ace_taxi_v2.Logic.Service;

import android.content.Context;
import android.content.SharedPreferences;

public class CurrentShiftStatus {
    private static final String PREF_NAME = "shift_status";
    private static final String KEY_SHIFT_STATUS = "shift_status";

    private static CurrentShiftStatus instance;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public CurrentShiftStatus(Context context) {
        sharedPreferences = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    // Singleton instance
    public static synchronized CurrentShiftStatus getInstance(Context context) {
        if (instance == null) {
            instance = new CurrentShiftStatus(context);
        }
        return instance;
    }

    public void saveStatus(String status) {
        editor.putString(KEY_SHIFT_STATUS, status);
        editor.apply();
    }

    public String getStatus() {
        return sharedPreferences.getString(KEY_SHIFT_STATUS, "OFF"); // Default to "OFF"
    }

    public void clearShiftStatus() {
        editor.remove(KEY_SHIFT_STATUS);
        editor.apply();
    }
}
