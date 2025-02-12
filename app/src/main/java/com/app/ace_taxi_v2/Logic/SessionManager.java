package com.app.ace_taxi_v2.Logic;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "MyAppPrefs";
    private static final String KEY_TOKEN = "JWT_TOKEN";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USERNAME = "username";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    // Save user token and ID to SharedPreferences
    public void saveSession(String token, int userId,String username) {
        editor.putString(KEY_TOKEN, token);
        editor.putInt(KEY_USER_ID, userId);
        editor.putString(KEY_USERNAME,username);
        editor.apply();
    }

    // Retrieve the user token
    public String getToken() {
        return sharedPreferences.getString(KEY_TOKEN, null);
    }

    // Retrieve the user ID
    public int getUserId() {
        return sharedPreferences.getInt(KEY_USER_ID, -1);
    }

    public String getUsername(){
        return sharedPreferences.getString(KEY_USERNAME,null);
    }

    // Check if the user is logged in
    public boolean isLoggedIn() {
        return getToken() != null;
    }

    // Clear session data
    public void clearSession() {
        editor.clear();
        editor.apply();
    }
}
