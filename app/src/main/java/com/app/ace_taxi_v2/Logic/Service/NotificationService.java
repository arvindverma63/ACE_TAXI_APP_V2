package com.app.ace_taxi_v2.Logic.Service;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Switch;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.app.ace_taxi_v2.Activity.HomeActivity;
import com.app.ace_taxi_v2.Logic.JobApi.NotificationJobDialogResponse;
import com.app.ace_taxi_v2.Models.Guid;
import com.app.ace_taxi_v2.Models.NotificationModel;
import com.app.ace_taxi_v2.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class NotificationService extends FirebaseMessagingService {

    private static final String TAG = "FCM_Service";
    private static final String SHARED_PREF_NAME = "fcm_preferences";
    private static final String FCM_TOKEN_KEY = "fcm_token";
    private NotificationModalSession notificationModalSession;
    public MediaPlayer mediaPlayer;
    private static NotificationService instance;

    public static NotificationService getInstance() {
        return instance;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
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
        Log.d(TAG, "Message received: " + remoteMessage.getMessageId());

        SharedPreferences sharedPreferences = getSharedPreferences("check_notification", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Default values
        String title = remoteMessage.getNotification() != null && remoteMessage.getNotification().getTitle() != null
                ? remoteMessage.getNotification().getTitle() : "Notification";
        String body = remoteMessage.getNotification() != null && remoteMessage.getNotification().getBody() != null
                ? remoteMessage.getNotification().getBody() : "New message received.";

        // Extract data payload
        String jobId = remoteMessage.getData().getOrDefault("bookingId", "N/A");
        String navId = remoteMessage.getData().getOrDefault("NavId", "N/A");
        String message = remoteMessage.getData().getOrDefault("message", "No additional information.");
        String passenger = remoteMessage.getData().getOrDefault("passenger", "");
        String datetime = remoteMessage.getData().getOrDefault("datetime", "");
        String guid = remoteMessage.getData().getOrDefault("guid","");
        Log.d(TAG, "Data Payload: Title=" + title + ", Body=" + body + ", JobId=" + jobId + ", NavId=" + navId+" datetime: "+datetime+" message : "+message);

        Guid guid1 = new Guid(this);
        guid1.setGuid(guid);
        // Save notification details in SharedPreferences
        editor.putString("notification_payload", body);
        editor.putString("notification_title", title);
        editor.putString("notification_jobId", jobId);
        editor.putString("notification_navId", navId);
        editor.apply();

        Log.e("Notification Service datetime",datetime);

        if("5".equals(navId) || "6".equals(navId)){
            NotificationModel notificationModel = new NotificationModel(jobId, navId, title, message, passenger, datetime);
            notificationModalSession.saveNotification(notificationModel);
        }


//        showNotification(title, body, jobId, navId, passenger, datetime);
        handleNavigation(navId, jobId, message, passenger, datetime,guid,body);
    }

    private void handleNavigation(String navId, String jobId, String message, String passenger, String datetime,String guid,String body) {
        if ("N/A".equals(navId)) return;
        notificationSound(navId);

        new Handler(Looper.getMainLooper()).post(() -> {
            Intent intent = new Intent(this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("datetime", datetime);
            intent.putExtra("passenger", passenger);
            intent.putExtra("body",body);
            intent.putExtra("jobId", jobId);
            intent.putExtra("navId", navId);
            intent.putExtra("guid",guid);


            switch (navId) {
                case "5":
                    intent.putExtra("message", message);
                    break;
                case "6":
                    intent.putExtra("message", message);
                    break;
            }
            startActivity(intent);
            Log.d(TAG,"Start Activity launch with "+navId+" "+jobId+" "+datetime+" "+passenger);
        });
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

    private void showNotification(String title, String message, String jobId, String navId, String passenger, String datetime) {
        Log.d(TAG, "showNotification called with title: " + title + ", message: " + message);

        String channelId = getString(R.string.default_notification_channel_id);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (notificationManager == null) {
            Log.e(TAG, "NotificationManager is null");
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Default Channel", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("title", title);
        intent.putExtra("message", message);
        intent.putExtra("jobId", jobId);
        intent.putExtra("navId", navId);
        intent.putExtra("passenger", passenger);
        intent.putExtra("datetime", datetime);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

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

        int notificationId = (int) System.currentTimeMillis();
        notificationManagerCompat.notify(notificationId, notificationBuilder.build());
        Log.d(TAG, "Notification posted with ID: " + notificationId);
    }

    public void notificationSound(String navId) {
        Uri soundUri;

        switch (navId) {
            case "1":
                soundUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.new_job_offer);
                break;
            case "6":
            case "5":
                soundUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.new_message);
                break;
            case "4":
                soundUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.job_cancelled);
                break;
            case "3":
                soundUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.job_amednded);
                break;
            default:
                soundUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.new_message);
                break;
        }

        playSoundThreeTimes(soundUri);
    }

    private void playSoundThreeTimes(Uri soundUri) {
         mediaPlayer = new MediaPlayer();
        final int[] playCount = {0};

        try {
            mediaPlayer.setDataSource(this, soundUri);
            mediaPlayer.setOnPreparedListener(mp -> mp.start());

            mediaPlayer.setOnCompletionListener(mp -> {
                playCount[0]++;
                if (playCount[0] < 3) {
                    mp.seekTo(0);
                    mp.start();
                } else {
                    mp.reset();
                    mp.release();
                }
            });

            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void stopSound() {
        try{
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                mediaPlayer.reset();
                mediaPlayer.release();
                mediaPlayer = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }



}
