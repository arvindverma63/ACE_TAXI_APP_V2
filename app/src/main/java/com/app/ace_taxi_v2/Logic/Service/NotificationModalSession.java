package com.app.ace_taxi_v2.Logic.Service;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.app.ace_taxi_v2.Models.NotificationModel;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NotificationModalSession {
    private static final String PREF_NAME = "NotificationData";
    private static final String KEY_NOTIFICATIONS = "notifications_json";
    private static final String KEY_NOTIFICATION_COUNT = "notification_count";
    private static final String TAG = "NotificationDebug";

    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;
    private final Gson gson;
    private final Object lock = new Object();

    public NotificationModalSession(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        gson = new Gson();
    }

    public void saveNotification(NotificationModel notification) {
        if (notification == null) {
            Log.w(TAG, "Attempted to save null notification.");
            return;
        }

        synchronized (lock) {
            List<NotificationModel> notifications = getAllNotifications();

            // Assign next serial number
            int maxSerialNumber = notifications.stream()
                    .mapToInt(NotificationModel::getSerialNumber)
                    .max()
                    .orElse(0);
            notification.setSerialNumber(maxSerialNumber + 1);

            // Generate and set unique notification number
            String notificationNumber = generateUniqueNotificationNumber();
            notification.setNotificationNumber(notificationNumber);

            notifications.add(notification);

            try {
                String jsonNotifications = gson.toJson(notifications);
                editor.putString(KEY_NOTIFICATIONS, jsonNotifications);
                incrementNotificationCount();
                editor.apply();
                Log.d(TAG, "Saved Notification: " + notification.toString());
            } catch (JsonSyntaxException e) {
                Log.e(TAG, "Failed to serialize notifications: " + e.getMessage());
            }
        }
    }

    private String generateUniqueNotificationNumber() {
        // Using timestamp + UUID suffix for uniqueness
        String timestamp = String.valueOf(System.currentTimeMillis());
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        return "NOTIF-" + timestamp + "-" + uniqueId;
    }

    public List<NotificationModel> getAllNotifications() {
        synchronized (lock) {
            String jsonNotifications = sharedPreferences.getString(KEY_NOTIFICATIONS, "[]");
            Type type = new TypeToken<ArrayList<NotificationModel>>() {}.getType();
            try {
                List<NotificationModel> notifications = gson.fromJson(jsonNotifications, type);
                return notifications != null ? notifications : new ArrayList<>();
            } catch (JsonSyntaxException e) {
                Log.e(TAG, "Failed to deserialize notifications: " + e.getMessage());
                return new ArrayList<>();
            }
        }
    }

    public void deleteNotificationByNumber(String notificationNumber) {
        if (notificationNumber == null) {
            Log.w(TAG, "Notification number is null. Cannot delete.");
            return;
        }

        synchronized (lock) {
            List<NotificationModel> notifications = getAllNotifications();
            boolean deleted = notifications.removeIf(model -> notificationNumber.equals(model.getNotificationNumber()));

            if (deleted) {
                try {
                    String jsonNotifications = gson.toJson(notifications);
                    editor.putString(KEY_NOTIFICATIONS, jsonNotifications);
                    decrementNotificationCount(); // Decrease the count
                    editor.apply();
                    Log.d(TAG, "Deleted Notification with Notification Number: " + notificationNumber);
                } catch (JsonSyntaxException e) {
                    Log.e(TAG, "Failed to serialize notifications after deletion: " + e.getMessage());
                }
            } else {
                Log.d(TAG, "No matching notification found for Notification Number: " + notificationNumber);
            }
        }
    }

    public void incrementNotificationCount() {
        synchronized (lock) {
            int count = getNotificationCount();
            editor.putInt(KEY_NOTIFICATION_COUNT, count + 1);
            editor.apply();
        }
    }

    public void decrementNotificationCount() {
        synchronized (lock) {
            int count = getNotificationCount();
            if (count > 0) { // Ensure count doesn't go below 0
                editor.putInt(KEY_NOTIFICATION_COUNT, count - 1);
                editor.apply();
            }
        }
    }

    public int getNotificationCount() {
        return sharedPreferences.getInt(KEY_NOTIFICATION_COUNT, 0);
    }

    // Other methods remain unchanged
    public NotificationModel getLatestNotification() {
        List<NotificationModel> notifications = getAllNotifications();
        if (notifications.isEmpty()) return null;
        return notifications.get(notifications.size() - 1);
    }

    public String getLatestMessage() {
        NotificationModel latest = getLatestNotification();
        return latest != null ? latest.getMessage() : "No messages available.";
    }

    public String getLatestJobId() {
        NotificationModel latest = getLatestNotification();
        return latest != null ? latest.getJobId() : "N/A";
    }

    public String getLatestNavId() {
        NotificationModel latest = getLatestNotification();
        return latest != null ? latest.getNavId() : "N/A";
    }

    public String getLatestTitle() {
        NotificationModel latest = getLatestNotification();
        return latest != null ? latest.getTitle() : "Untitled";
    }

    public String getPassenger() {
        NotificationModel latest = getLatestNotification();
        return latest != null ? latest.getPassenger() : "N/A";
    }
    public void clearAllNotifications() {
        synchronized (lock) {
            // Clear the notifications list
            editor.putString(KEY_NOTIFICATIONS, "[]"); // Store an empty list
            // Reset the notification count to 0
            editor.putInt(KEY_NOTIFICATION_COUNT, 0);
            editor.apply();
            Log.d(TAG, "Cleared all notifications and reset count to 0");
        }
    }
}