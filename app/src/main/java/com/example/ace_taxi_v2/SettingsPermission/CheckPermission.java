package com.example.ace_taxi_v2.SettingsPermission;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.os.Build;
import android.util.Log;
import android.widget.Switch;

import androidx.core.content.ContextCompat;

import com.example.ace_taxi_v2.R;

public class CheckPermission {
    private final Context context;

    public CheckPermission(Context context) {
        this.context = context;
    }

    // Check notification permission
    public void notificationPermission(Switch notificationSwitch) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                    == PackageManager.PERMISSION_GRANTED) {
                notificationSwitch.setChecked(true);
                notificationSwitch.setTrackTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.primaryColor)));
                notificationSwitch.setThumbTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.primaryColor)));
                Log.d("PermissionCheck", "Notification permission is enabled");
            } else {
                notificationSwitch.setChecked(false);
                Log.d("PermissionCheck", "Notification permission is not enabled");
            }
        } else {
            // Assume notifications are enabled for Android versions below 13
            notificationSwitch.setChecked(true);
            notificationSwitch.setTrackTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.primaryColor)));
            notificationSwitch.setThumbTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.primaryColor)));
            Log.d("PermissionCheck", "Notification permission is assumed to be enabled (Android < 13)");
        }
    }

    // Check GPS (location) permission
    public void gpsPermission(Switch gpsSwitch) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            gpsSwitch.setChecked(true);
            gpsSwitch.setTrackTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.primaryColor)));
            gpsSwitch.setThumbTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.primaryColor)));
            Log.d("PermissionCheck", "GPS permission is enabled");
        } else {
            gpsSwitch.setChecked(false);
            gpsSwitch.setTrackTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.gray)));
            gpsSwitch.setThumbTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.gray)));
            Log.d("PermissionCheck", "GPS permission is not enabled");
        }
    }
}
