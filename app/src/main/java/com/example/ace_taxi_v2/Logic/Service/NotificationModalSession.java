package com.example.ace_taxi_v2.Logic.Service;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class NotificationModalSession {
    private static final String PREF_NAME = "NotificationData";
    private static final String KEY_NOTIFICATIONS = "notifications_json"; // 🔹 Now using JSON format
    private static final String KEY_NOTIFICATION_COUNT = "notification_count";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Gson gson;

    public NotificationModalSession(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        gson = new Gson(); // 🔹
    }

    // ✅ Save Notification Data using JSON storage (instead of a single string)
    public void saveNotificationData(String jobId, String navId, String title, String message) {
        if (jobId == null) jobId = "N/A";
        if (navId == null) navId = "N/A";
        if (title == null) title = "Untitled";
        if (message == null) message = "No message available";

        List<String> notifications = getAllNotifications();
        notifications.add(jobId + "|" + navId + "|" + title + "|" + message);

        // 🔹 Convert list to JSON string
        String jsonNotifications = gson.toJson(notifications);
        editor.putString(KEY_NOTIFICATIONS, jsonNotifications);
        incrementNotificationCount();
        boolean isSaved = editor.commit(); // 🔹 Ensures data is saved immediately

        // Debug Log
        Log.d("NotificationDebug", "Saved: " + isSaved + ", New Data: " + jsonNotifications);
    }

    public List<String> getAllNotifications() {
        String jsonNotifications = sharedPreferences.getString(KEY_NOTIFICATIONS, "[]");

        // Debugging Log to check what is being retrieved
        Log.d("NotificationDebug", "Retrieving Notifications (Raw JSON): " + jsonNotifications);

        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        List<String> notifications = gson.fromJson(jsonNotifications, type);

        // Ensure we never return null
        if (notifications == null) {
            Log.d("NotificationDebug", "getAllNotifications() returning empty list because JSON parsing failed.");
            return new ArrayList<>();
        }

        Log.d("NotificationDebug", "Parsed Notifications: " + notifications.toString());
        return notifications;
    }


    public String[] getLatestNotification() {
        List<String> notifications = getAllNotifications();

        if (notifications.isEmpty()) {
            Log.d("NotificationDebug", "No stored notifications found!");
            return null;
        }

        String lastNotification = notifications.get(notifications.size() - 1);
        Log.d("NotificationDebug", "Last notification raw data: " + lastNotification);

        // Ensure we correctly split the string into 4 parts
        String[] details = lastNotification.split("\\|");

        if (details.length < 4) {
            Log.d("NotificationDebug", "Stored notification is incomplete! Details Length: " + details.length);
            return null;
        }

        return details;
    }

    public String getLatestMessage() {
        String[] details = getLatestNotification();

        if (details == null || details.length < 4) {
            Log.d("NotificationDebug", "Latest message not found. Returning default.");
            return "No messages available.";
        }

        Log.d("NotificationDebug", "Latest message retrieved successfully: " + details[3]);
        return details[3];
    }

    // ✅ Get Job ID of Latest Notification
    public String getLatestJobId() {
        String[] details = getLatestNotification();
        return (details != null && details.length > 0) ? details[0] : "N/A";
    }

    // ✅ Get Nav ID of Latest Notification
    public String getLatestNavId() {
        String[] details = getLatestNotification();
        return (details != null && details.length > 1) ? details[1] : "N/A";
    }

    // ✅ Get Title of Latest Notification
    public String getLatestTitle() {
        String[] details = getLatestNotification();
        return (details != null && details.length > 2) ? details[2] : "Untitled";
    }

    // ✅ Increment Notification Count
    public void incrementNotificationCount() {
        int count = getNotificationCount();
        editor.putInt(KEY_NOTIFICATION_COUNT, count + 1);
        editor.apply();
    }

    // ✅ Get Notification Count
    public int getNotificationCount() {
        return sharedPreferences.getInt(KEY_NOTIFICATION_COUNT, 0);
    }

    // ✅ Clear Notification Count
    public void clearNotificationCount() {
        editor.putInt(KEY_NOTIFICATION_COUNT, 0);
        editor.apply();
    }

    // ✅ Clear All Notification Data (Fixes persistence issues)
    public void clearNotificationData() {
        editor.remove(KEY_NOTIFICATIONS);
        editor.remove(KEY_NOTIFICATION_COUNT);
        editor.apply();
        Log.d("NotificationDebug", "All notification data cleared.");
    }
}
