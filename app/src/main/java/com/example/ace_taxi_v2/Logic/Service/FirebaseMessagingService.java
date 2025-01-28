package com.example.ace_taxi_v2.Logic.Service;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.ace_taxi_v2.Activity.NotificationModalActivity;
import com.example.ace_taxi_v2.Activity.SplashScreenActivity;
import com.example.ace_taxi_v2.Logic.NotificationSessionManager;
import com.example.ace_taxi_v2.R;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    private static final String TAG = "FCM_Service";
    private static final String SHARED_PREF_NAME = "fcm_preferences";
    private static final String FCM_TOKEN_KEY = "fcm_token";
    private NotificationSessionManager notificationSessionManager;

    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize NotificationSessionManager
        notificationSessionManager = new NotificationSessionManager(this);
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

        Log.d(TAG, "Message received: " + remoteMessage.getMessageId());
        Log.d(TAG,"Notification Payload: "+remoteMessage.getNotification());

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

            Log.d(TAG,"DATA json: "+remoteMessage.getData());

        }

        if (title != null && body != null) {
            notificationSessionManager.saveNotification(title, body, System.currentTimeMillis());
            showNotification(title, body, jobId,navId);
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

    private void showNotification(String title, String message, String jobId,String navId) {
        String channelId = "default_channel_id";
        String channelName = "Default Channel";

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(this, NotificationModalActivity.class);
        intent.putExtra("title", title);
        if (jobId != null) {
            intent.putExtra("jobid", jobId);
        }
        intent.putExtra("navId",navId);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        if(navId!=null){
            switch (navId){

            }
        }
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

        int notificationId = jobId != null ? jobId.hashCode() : (int) System.currentTimeMillis();
        notificationManagerCompat.notify(notificationId, notificationBuilder.build());
    }
}