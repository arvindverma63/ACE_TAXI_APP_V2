package com.example.ace_taxi_v2.Logic.Service;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.ace_taxi_v2.Activity.NotificationModalActivity;
import com.example.ace_taxi_v2.R;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class NotificationService extends FirebaseMessagingService {

    private static final String TAG = "FCM_Service";
    private static final String SHARED_PREF_NAME = "fcm_preferences";
    private static final String FCM_TOKEN_KEY = "fcm_token";
    public NotificationModalSession notificationModalSession;
    @Override
    public void onCreate() {
        super.onCreate();
        notificationModalSession = new NotificationModalSession(this);

    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.d(TAG, "New FCM Token: " + token);
        // Save token locally
        saveTokenToPreferences(token);

        // Optionally send token to your server
        sendTokenToServer(token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d(TAG, "Message received: " + (remoteMessage.getMessageId() != null ? remoteMessage.getMessageId() : "Unknown"));

        String title = null;
        String body = null;
        String jobId = null;
        String navId = null;

        if (remoteMessage.getNotification() != null) {
            title = remoteMessage.getNotification().getTitle();
            body = remoteMessage.getNotification().getBody();
        }

        if (remoteMessage.getData().size() > 0) {
            title = remoteMessage.getData().get("customTitle") != null ? remoteMessage.getData().get("customTitle") : title;
            body = remoteMessage.getData().get("customMessage") != null ? remoteMessage.getData().get("customMessage") : body;
            jobId = remoteMessage.getData().get("jobid");
            navId = remoteMessage.getData().get("NavId");

            Log.d(TAG, "Data Payload: " + remoteMessage.getData());
        }

        if (title != null && body != null) {

            // Show the notification
            NotificationModalSession notificationModalSession = new NotificationModalSession(this);
            notificationModalSession.saveNotificationData(jobId,navId,title);
            showNotification(title, body, jobId, navId);
        }
    }

    private void saveTokenToPreferences(String token) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(FCM_TOKEN_KEY, token);
        editor.apply();
        Log.d(TAG, "FCM Token saved to SharedPreferences");
    }

    private void sendTokenToServer(String token) {
        Log.d(TAG, "Fcm Token sent to server: " + token);
    }

    private void showNotification(String title, String message, String jobId, String navId) {
        String channelId = "default_channel_id";
        String channelName = "Default Channel";

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        // ✅ Create an Intent to open `NotificationModalActivity` when clicked
        Intent intent = new Intent(getApplicationContext(), NotificationModalActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("message", message);
        intent.putExtra("jobid", jobId);
        intent.putExtra("navId", navId);

        // ✅ Ensure proper flags for launching when app is killed
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // ✅ Create a PendingIntent that will open the activity with the data
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                jobId != null ? jobId.hashCode() : (int) System.currentTimeMillis(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_logo_ace)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true) // Removes the notification when clicked
                .setContentIntent(pendingIntent); // Open activity on click

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                Log.w(TAG, "Notification permission not granted.");
                return;
            }
        }

        int notificationId = jobId != null ? jobId.hashCode() : (int) System.currentTimeMillis();
        notificationManagerCompat.notify(notificationId, notificationBuilder.build());

        Log.d("NotificationDebug", "Notification sent with ID: " + notificationId);
    }

}