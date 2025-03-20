package com.app.ace_taxi_v2.Activity.HomeActivityHelper;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.app.ace_taxi_v2.Activity.HomeActivity;
import com.app.ace_taxi_v2.Components.NotificationDialog;
import com.app.ace_taxi_v2.Logic.Service.NotificationModalSession;
import com.app.ace_taxi_v2.R;

public class NotificationHandler {
    private final HomeActivity activity;
    private final ImageView notificationIcon;
    private final TextView notificationCount;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private static final int INTERVAL = 5000;

    public NotificationHandler(HomeActivity activity, ImageView notificationIcon, TextView notificationCount) {
        this.activity = activity;
        this.notificationIcon = notificationIcon;
        this.notificationCount = notificationCount;
        setupNotificationListener();
    }

    private void setupNotificationListener() {
        notificationIcon.setOnClickListener(v -> {
            NotificationModalSession session = new NotificationModalSession(activity);
            String navId = session.getLatestNavId();
            String jobId = session.getLatestJobId();

            if (navId != null && jobId != null) {
                new NotificationDialog(activity).openModal();
            }
        });
    }

    private final Runnable notificationRunnable = new Runnable() {
        @Override
        public void run() {
            updateNotificationUI();
            handler.postDelayed(this, INTERVAL);
        }
    };

    public void startNotificationUpdates() {
        handler.post(notificationRunnable);
    }

    public void stopNotificationUpdates() {
        handler.removeCallbacks(notificationRunnable);
    }

    private void updateNotificationUI() {
        NotificationModalSession session = new NotificationModalSession(activity);
        int count = session.getNotificationCount();

        activity.runOnUiThread(() -> {
            if (count > 0) {
                notificationCount.setText(String.valueOf(count));
                notificationCount.setVisibility(View.VISIBLE);
            } else {
                notificationCount.setVisibility(View.GONE);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void handleNotificationData() {
        // Move getNotificationData logic here
        // Extract notification handling logic from original HomeActivity
    }
}