package com.example.ace_taxi_v2.Logic.Service;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ace_taxi_v2.Activity.NotificationModalActivity;

import java.util.HashSet;
import java.util.Set;

public class NotificationModalSession {
    private static final String PREF_NAME = "NotificationData";
    private static final String KEY_NOTIFICATIONS = "notifications";
    private static final String KEY_NOTIFICATION_COUNT = "notification_count";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public NotificationModalSession(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    // Save Notification Data
    public void saveNotificationData(String jobId, String navId, String title) {
        Set<String> notifications = sharedPreferences.getStringSet(KEY_NOTIFICATIONS, new HashSet<>());
        notifications.add(jobId + "|" + navId + "|" + title);
        editor.putStringSet(KEY_NOTIFICATIONS, notifications);
        incrementNotificationCount();
        editor.apply();
    }

    // Get All Notifications
    public Set<String> getAllNotifications() {
        return sharedPreferences.getStringSet(KEY_NOTIFICATIONS, new HashSet<>());
    }

    // Get Latest Notification Details
    public String[] getLatestNotification() {
        Set<String> notifications = getAllNotifications();
        if (notifications.isEmpty()) {
            return null;
        }
        return notifications.iterator().next().split("\\|");
    }

    // Get Job ID of Latest Notification
    public String getLatestJobId() {
        String[] details = getLatestNotification();
        return (details != null && details.length > 0) ? details[0] : null;
    }

    // Get Nav ID of Latest Notification
    public String getLatestNavId() {
        String[] details = getLatestNotification();
        return (details != null && details.length > 1) ? details[1] : null;
    }

    // Get Title of Latest Notification
    public String getLatestTitle() {
        String[] details = getLatestNotification();
        return (details != null && details.length > 2) ? details[2] : null;
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
        editor.remove(KEY_NOTIFICATIONS);
        editor.remove(KEY_NOTIFICATION_COUNT);
        editor.apply();
    }


}
