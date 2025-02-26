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

    public void saveNotification(NotificationModel notification) {
        List<NotificationModel> notifications = getAllNotifications();

        // Find the highest serial number in existing notifications
        int maxSerialNumber = 0;
        for (NotificationModel model : notifications) {
            if (model.getSerialNumber() > maxSerialNumber) {
                maxSerialNumber = model.getSerialNumber();
            }
        }

        // Assign the next serial number
        notification.setSerialNumber(maxSerialNumber + 1);

        notifications.add(notification);

        // Convert list to JSON and save it
        String jsonNotifications = gson.toJson(notifications);
        editor.putString(KEY_NOTIFICATIONS, jsonNotifications);
        incrementNotificationCount();
        editor.commit();

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
    public void deleteNotificationBySerial(int serialNumber) {
        List<NotificationModel> notifications = getAllNotifications();
        boolean deleted = false;

        for (int i = 0; i < notifications.size(); i++) {
            if (notifications.get(i).getSerialNumber() == serialNumber) {
                notifications.remove(i);
                deleted = true;
                break; // Stop after deleting the first match
            }
        }

        if (deleted) {
            // Convert the updated list to JSON and save it back
            String jsonNotifications = gson.toJson(notifications);
            editor.putString(KEY_NOTIFICATIONS, jsonNotifications);
            editor.apply();
            Log.d("NotificationDebug", "Deleted Notification with Serial Number: " + serialNumber);
        } else {
            Log.d("NotificationDebug", "No matching notification found for Serial Number: " + serialNumber);
        }
    }
    public int getSerialNumberByJobId(String jobId) {
        if (jobId == null) {
            Log.d("NotificationDebug", "Job ID is null. Cannot retrieve serial number.");
            return -1; // Return -1 to indicate not found
        }

        List<NotificationModel> notifications = getAllNotifications();

        for (NotificationModel model : notifications) {
            if (jobId.equals(model.getJobId())) { // Avoid null comparison error
                return model.getSerialNumber();
            }
        }

        Log.d("NotificationDebug", "No notification found with Job ID: " + jobId);
        return -1; // Return -1 if not found
    }


}
