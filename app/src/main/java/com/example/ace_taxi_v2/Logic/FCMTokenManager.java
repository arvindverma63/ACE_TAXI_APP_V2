package com.example.ace_taxi_v2.Logic;

import android.content.Context;
import android.content.SharedPreferences;

public class FCMTokenManager {
    private static final String PREF_NAME = "MyAppPrefs";
    private static final String FCM_TOKEN = "fcm_token";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public FCMTokenManager(Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.editor = sharedPreferences.edit();
    }

    // Save the FCM token
    public void setToken(String token) {
        editor.putString(FCM_TOKEN, token);
        editor.apply(); // Save changes
    }

    // Retrieve the FCM token
    public String getToken() {
        return sharedPreferences.getString(FCM_TOKEN, null);
    }
}