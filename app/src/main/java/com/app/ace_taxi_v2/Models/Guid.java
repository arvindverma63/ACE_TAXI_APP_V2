package com.app.ace_taxi_v2.Models;

import android.content.Context;
import android.content.SharedPreferences;

public class Guid {
    private static final String PREF_NAME = "AceTaxiPrefs";
    private static final String KEY_GUID = "guid";
    private final SharedPreferences prefs;

    public Guid(Context context) {
        // Initialize SharedPreferences
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public String getGuid() {
        // Retrieve guid from SharedPreferences, return null if not set
        return prefs.getString(KEY_GUID, null);
    }

    public void setGuid(String guid) {
        // Store guid in SharedPreferences
        SharedPreferences.Editor editor = prefs.edit();
        if (guid != null) {
            editor.putString(KEY_GUID, guid);
        } else {
            editor.remove(KEY_GUID); // Clear guid if null
        }
        editor.apply(); // Asynchronously save changes
    }

    public void clearGuid() {
        // Optional: Method to clear the guid
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(KEY_GUID);
        editor.apply();
    }
}