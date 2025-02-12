package com.app.ace_taxi_v2.Logic;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.app.ace_taxi_v2.Models.NotificationModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NotificationSessionManager {

    private static final String SHARED_PREF_NAME = "fcm_preferences";
    private static final String NOTIFICATIONS_KEY = "notifications_list";
    private static final String TAG = "NotificationSessionManager";

    private SharedPreferences sharedPreferences;

    public NotificationSessionManager(Context context) {
        this.sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Save a notification to SharedPreferences
     *
     * @param title   The notification title
     * @param body    The notification body
     * @param timestamp The time the notification was received
     */
    public void saveNotification(String title, String body, long timestamp) {
        String existingNotifications = sharedPreferences.getString(NOTIFICATIONS_KEY, "[]");

        try {
            JSONArray notificationsArray = new JSONArray(existingNotifications);
            JSONObject notificationObject = new JSONObject();
            notificationObject.put("title", title);
            notificationObject.put("body", body);
            notificationObject.put("timestamp", timestamp);

            notificationsArray.put(notificationObject);

            sharedPreferences.edit()
                    .putString(NOTIFICATIONS_KEY, notificationsArray.toString())
                    .apply();

            Log.d(TAG, "Notification saved: " + notificationObject.toString());
        } catch (JSONException e) {
            Log.e(TAG, "Error saving notification", e);
        }
    }

    /**
     * Retrieve a list of saved notifications from SharedPreferences
     *
     * @return A list of NotificationModel objects
     */
    public List<NotificationModel> getSavedNotifications() {
        String notificationsJson = sharedPreferences.getString(NOTIFICATIONS_KEY, "[]");
        List<NotificationModel> notificationsList = new ArrayList<>();

        try {
            JSONArray notificationsArray = new JSONArray(notificationsJson);
            for (int i = 0; i < notificationsArray.length(); i++) {
                JSONObject notificationObject = notificationsArray.getJSONObject(i);

                String title = notificationObject.getString("title");
                String body = notificationObject.getString("body");
                long timestamp = notificationObject.getLong("timestamp");

            }
        } catch (JSONException e) {
            Log.e(TAG, "Error retrieving notifications", e);
        }

        return notificationsList;
    }

    /**
     * Clear all saved notifications from SharedPreferences
     */
    public void clearNotifications() {
        sharedPreferences.edit()
                .remove(NOTIFICATIONS_KEY)
                .apply();
        Log.d(TAG, "All notifications cleared.");
    }
}
