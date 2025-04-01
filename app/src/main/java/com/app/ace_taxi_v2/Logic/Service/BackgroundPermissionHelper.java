package com.app.ace_taxi_v2.Logic.Service;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

public class BackgroundPermissionHelper {

    private static final int LOCATION_REQUEST_CODE = 100;
    private static final int BACKGROUND_LOCATION_REQUEST_CODE = 101;

    private final Activity activity;

    public BackgroundPermissionHelper(Activity activity) {
        this.activity = activity;
    }

    // Request Foreground Location Permissions
    public void requestLocationPermissions() {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_REQUEST_CODE);
        } else {
            // Foreground permission granted, now request background location
            requestBackgroundLocationPermission();
        }
    }

    // Request Background Location Permission (Android 10+)
    public void requestBackgroundLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                showBackgroundPermissionDialog();
            }
        }
    }

    // Show dialog and handle repeated asking
    private void showBackgroundPermissionDialog() {
        new AlertDialog.Builder(activity)
                .setTitle("Background Location Permission")
                .setMessage("This app requires background location access to provide location updates even when the app is closed.")
                .setPositiveButton("Grant", (dialog, which) -> ActivityCompat.requestPermissions(
                        activity,
                        new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                        BACKGROUND_LOCATION_REQUEST_CODE))
                .setNegativeButton("Deny", (dialog, which) -> {
                    // If denied, show the dialog again
                    showBackgroundPermissionDialog();
                })
                .setCancelable(false) // Prevents dismissing the dialog without a choice
                .show();
    }

    // Handle Permission Results
    public void handlePermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Foreground location granted, now request background location
                requestBackgroundLocationPermission();
            } else {
                // If foreground permission denied, ask again
                requestLocationPermissions();
            }
        } else if (requestCode == BACKGROUND_LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Background location permission granted
            } else {
                // If background permission denied, ask again
                requestBackgroundLocationPermission();
            }
        }
    }
}