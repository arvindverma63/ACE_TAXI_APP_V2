package com.app.ace_taxi_v2.Logic.Service;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.app.ace_taxi_v2.Models.NotificationModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class NotificationModalSession {
    private static final String PREF_NAME = "NotificationData";
    private static final String KEY_NOTIFICATIONS = "notifications_json";
    private static final String KEY_NOTIFICATION_COUNT = "notification_count";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Gson gson;

    public NotificationModalSession(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        gson = new Gson();
    }

    // ✅ Save Notification Data using JSON storage
    public void saveNotification(NotificationModel notification) {
        List<NotificationModel> notifications = getAllNotifications();
        notifications.add(notification);

        // Convert list to JSON string
        String jsonNotifications = gson.toJson(notifications);
        editor.putString(KEY_NOTIFICATIONS, jsonNotifications);
        incrementNotificationCount();
        editor.commit(); // Ensures data is saved immediately

        Log.d("NotificationDebug", "Saved Notification: " + notification.toString());
    }

    // ✅ Retrieve All Notifications as List<NotificationModel>
    public List<NotificationModel> getAllNotifications() {
        String jsonNotifications = sharedPreferences.getString(KEY_NOTIFICATIONS, "[]");
        Type type = new TypeToken<ArrayList<NotificationModel>>() {}.getType();
        List<NotificationModel> notifications = gson.fromJson(jsonNotifications, type);

        if (notifications == null) {
            Log.d("NotificationDebug", "Returning empty list due to parsing failure.");
            return new ArrayList<>();
        }
        return notifications;
    }

    // ✅ Retrieve the Latest Notification (Object)
    public NotificationModel getLatestNotification() {
        List<NotificationModel> notifications = getAllNotifications();

        if (notifications.isEmpty()) {
            Log.d("NotificationDebug", "No stored notifications found!");
            return null;
        }

        return notifications.get(notifications.size() - 1);
    }

    // ✅ Retrieve the Latest Notification Message
    public String getLatestMessage() {
        NotificationModel latest = getLatestNotification();
        return (latest != null) ? latest.getMessage() : "No messages available.";
    }

    // ✅ Retrieve the Latest Job ID
    public String getLatestJobId() {
        NotificationModel latest = getLatestNotification();
        return (latest != null) ? latest.getJobId() : "N/A";
    }

    // ✅ Retrieve the Latest Nav ID
    public String getLatestNavId() {
        NotificationModel latest = getLatestNotification();
        return (latest != null) ? latest.getNavId() : "N/A";
    }

    // ✅ Retrieve the Latest Notification Title
    public String getLatestTitle() {
        NotificationModel latest = getLatestNotification();
        return (latest != null) ? latest.getTitle() : "Untitled";
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

    // ✅ Clear All Notifications
    public void clearAllNotifications() {
        editor.remove(KEY_NOTIFICATIONS);
        editor.remove(KEY_NOTIFICATION_COUNT);
        editor.apply();
        Log.d("NotificationDebug", "All notifications cleared.");
    }
}
