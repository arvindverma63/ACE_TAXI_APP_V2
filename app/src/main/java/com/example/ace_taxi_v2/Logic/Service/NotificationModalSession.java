package com.example.ace_taxi_v2.Logic.Service;

import android.content.Context;
import android.content.SharedPreferences;

public class NotificationModalSession {
    private static final String PREF_NAME = "NotificationData";
    private static final String KEY_JOB_ID = "jobid";
    private static final String KEY_NAV_ID = "navId";
    private static final String KEY_TITLE = "title";
    private static final String KEY_NOTIFICATION_COUNT = "notification_count";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public NotificationModalSession(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    // Save Notification Data
    public void saveNotificationData(String jobId, String navId, String title) {
        editor.putString(KEY_JOB_ID, jobId);
        editor.putString(KEY_NAV_ID, navId);
        editor.putString(KEY_TITLE, title);
        editor.apply();
    }

    // Get Job ID
    public String getJobId() {
        return sharedPreferences.getString(KEY_JOB_ID, null);
    }

    // Get Nav ID
    public String getNavId() {
        return sharedPreferences.getString(KEY_NAV_ID, null);
    }

    // Get Title
    public String getTitle() {
        return sharedPreferences.getString(KEY_TITLE, null);
    }

    // Increment Notification Count
    public void incrementNotificationCount() {
        int count = getNotificationCount();
        editor.putInt(KEY_NOTIFICATION_COUNT, count + 1);
        editor.apply();
    }

    // Get Notification Count
    public int getNotificationCount() {
        return sharedPreferences.getInt(KEY_NOTIFICATION_COUNT, 0);
    }

    // Clear Notification Count
    public void clearNotificationCount() {
        editor.putInt(KEY_NOTIFICATION_COUNT, 0);
        editor.apply();
    }

    // Clear All Notification Data
    public void clearNotificationData() {
        editor.clear();
        editor.apply();
    }
}
