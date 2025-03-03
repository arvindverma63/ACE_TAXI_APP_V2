package com.app.ace_taxi_v2.Logic.Service;

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

import com.app.ace_taxi_v2.Activity.HomeActivity;
import com.app.ace_taxi_v2.Logic.JobApi.GetBookingById;
import com.app.ace_taxi_v2.Logic.JobApi.NotificationJobDialogResponse;
import com.app.ace_taxi_v2.Models.NotificationModel;
import com.app.ace_taxi_v2.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class NotificationService extends FirebaseMessagingService {

    private static final String TAG = "FCM_Service";
    private static final String SHARED_PREF_NAME = "fcm_preferences";
    private static final String FCM_TOKEN_KEY = "fcm_token";
    private NotificationModalSession notificationModalSession;

    @Override
    public void onCreate() {
        super.onCreate();
        notificationModalSession = new NotificationModalSession(this);
    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.d(TAG, "New FCM Token: " + token);
        saveTokenToPreferences(token);
        sendTokenToServer(token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "Message received: " + (remoteMessage.getMessageId() != null ? remoteMessage.getMessageId() : "Unknown"));

        SharedPreferences sharedPreferences = getSharedPreferences("check_notification", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String title = "Notification";
        String body = "New message received.";
        String jobId = "N/A";
        String navId = "N/A";
        String message = "No additional information.";

        if (remoteMessage.getNotification() != null) {
            title = remoteMessage.getNotification().getTitle() != null ? remoteMessage.getNotification().getTitle() : title;
            body = remoteMessage.getNotification().getBody() != null ? remoteMessage.getNotification().getBody() : body;
        }

        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Data Payload: " + remoteMessage.getData());
            title = remoteMessage.getData().getOrDefault("customTitle", title);
            body = remoteMessage.getData().getOrDefault("customMessage", body);
            jobId = remoteMessage.getData().getOrDefault("bookingId", jobId);
            navId = remoteMessage.getData().getOrDefault("NavId", navId);
            message = remoteMessage.getData().getOrDefault("message", message);
        }

        editor.putString("notification_payload", body);
        editor.putString("notification_title", title);
        editor.putString("notification_jobId", jobId);
        editor.putString("notification_navId", navId);
        editor.apply();

        NotificationModel notificationModel = new NotificationModel(jobId, navId, title, message);
        notificationModalSession.saveNotification(notificationModel);

        Log.d(TAG, "Calling showNotification with title: " + title + ", body: " + body);
        showNotification(title, body, jobId, navId);

        if (!jobId.equals("N/A")) {
            try {
                int bookingId = Integer.parseInt(jobId);
                NotificationJobDialogResponse notificationJobDialogResponse = new NotificationJobDialogResponse(this);
                notificationJobDialogResponse.getBookingDetails(bookingId);
            } catch (NumberFormatException e) {
                Log.e(TAG, "Invalid jobId: " + jobId, e);
            }
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
        Log.d(TAG, "FCM Token sent to server: " + token);
    }

    private void showNotification(String title, String message, String jobId, String navId) {
        Log.d(TAG, "showNotification called with title: " + title + ", message: " + message);

        String channelId = getString(R.string.default_notification_channel_id);
        String channelName = "Default Channel";
        Log.d(TAG, "Channel ID: " + channelId);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (notificationManager == null) {
            Log.e(TAG, "NotificationManager is null");
            return;
        }
        Log.d(TAG, "NotificationManager retrieved");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
            Log.d(TAG, "Channel created: " + channelId);
        }

        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("message", message);
        intent.putExtra("jobid", jobId);
        intent.putExtra("navId", navId);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Log.d(TAG, "Intent prepared");

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        Log.d(TAG, "PendingIntent created");

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_logo_ace)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);
        Log.d(TAG, "NotificationBuilder configured");

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        if (notificationManagerCompat == null) {
            Log.e(TAG, "NotificationManagerCompat is null");
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                Log.w(TAG, "Notification permission not granted in kill mode.");
                return;
            }
        }

        int notificationId = (int) System.currentTimeMillis();
        notificationManagerCompat.notify(notificationId, notificationBuilder.build());
        Log.d(TAG, "Notification posted with ID: " + notificationId);
    }
}