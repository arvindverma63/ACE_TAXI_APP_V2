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

        String title = "New Notification"; // Default title if not provided
        String body = "You have a new message"; // Default body if not provided
        String sentBy = null;
        String navId = null;
        String message = null;
        String datetime = null;

        if (remoteMessage.getNotification() != null) {
            title = remoteMessage.getNotification().getTitle() != null ? remoteMessage.getNotification().getTitle() : title;
            body = remoteMessage.getNotification().getBody() != null ? remoteMessage.getNotification().getBody() : body;
        }

        if (remoteMessage.getData().size() > 0) {
            sentBy = remoteMessage.getData().get("sentBy");
            navId = remoteMessage.getData().get("NavId");
            message = remoteMessage.getData().get("message");
            datetime = remoteMessage.getData().get("datetime");

            Log.d(TAG, "Data Payload: " + remoteMessage.getData());
        }

        if (title != null && message != null) {
            Log.d(TAG, "Notification Details: SentBy=" + sentBy + ", NavId=" + navId + ", Message=" + message + ", DateTime=" + datetime);

            // Save notification data to session storage
            notificationModalSession.saveNotificationData(sentBy, navId, title, message);

            // Show the notification
            showNotification(title, message, sentBy, navId);
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

    private void showNotification(String title, String message, String sentBy, String navId) {
        String channelId = "default_channel_id";

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Default Channel", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(getApplicationContext(), NotificationModalActivity.class);
        intent.putExtra("sentBy", sentBy);
        intent.putExtra("message", message);
        intent.putExtra("navId", navId);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        int pendingIntentFlags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            pendingIntentFlags |= PendingIntent.FLAG_IMMUTABLE;
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                sentBy != null ? sentBy.hashCode() : (int) System.currentTimeMillis(),
                intent,
                pendingIntentFlags
        );

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_logo_ace)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                Log.w(TAG, "Notification permission not granted.");
                return;
            }
        }

        int notificationId = sentBy != null ? sentBy.hashCode() : (int) System.currentTimeMillis();
        notificationManagerCompat.notify(notificationId, notificationBuilder.build());

        Log.d(TAG, "Notification sent with ID: " + notificationId);
    }


}