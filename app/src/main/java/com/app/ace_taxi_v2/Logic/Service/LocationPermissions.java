package com.app.ace_taxi_v2.Logic.Service;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.app.ace_taxi_v2.Activity.HomeActivity;
import com.app.ace_taxi_v2.R;
import com.google.android.material.button.MaterialButton;

public class LocationPermissions {

    private static final String TAG = "LocationPermissions";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private static final int BATTERY_OPTIMIZATION_REQUEST_CODE = 1002;

    private final Context context;
    private final Activity activity;
    private final Switch locationSwitch;
    private final TextView onlineStatusLabel;

    public LocationPermissions(Activity activity, Switch locationSwitch, TextView onlineStatusLabel) {
        this.context = activity.getApplicationContext();
        this.activity = activity;
        this.locationSwitch = locationSwitch;
        this.onlineStatusLabel = onlineStatusLabel;
    }

    public boolean checkLocationPermissions() {
        boolean hasFineLocation = ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        boolean hasCoarseLocation = ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            boolean hasBackgroundLocation = ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED;
            if (!hasBackgroundLocation) {
                Log.e(TAG, "Background location permission is missing.");
                return false;
            }
        }

        if (!hasFineLocation || !hasCoarseLocation) {
            Log.e(TAG, "Location permissions are missing.");
            return false;
        }

        Log.d(TAG, "All required location permissions are granted.");
        return true;
    }

    public void requestLocationPermissions() {
        Log.d(TAG, "Requesting location permissions...");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityCompat.requestPermissions(activity, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
            }, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    public boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            return locationManager != null && locationManager.isLocationEnabled();
        } else {
            int mode = Settings.Secure.getInt(
                    context.getContentResolver(),
                    Settings.Secure.LOCATION_MODE,
                    Settings.Secure.LOCATION_MODE_OFF
            );
            return mode != Settings.Secure.LOCATION_MODE_OFF;
        }
    }

    public void promptEnableGPS() {
        try {
            // Use 'activity' instead of 'context' to avoid theme issues
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);

            // Inflate custom dialog layout
            LayoutInflater inflater = activity.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.location_permission_dialog, null);
            builder.setView(dialogView);

            // Prevent dialog from being dismissed outside clicks
            builder.setCancelable(false);
            AlertDialog alertDialog = builder.create();

            // Set transparent background for a clean look
            if (alertDialog.getWindow() != null) {
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            }

            // Apply rounded background programmatically
            dialogView.setBackground(ContextCompat.getDrawable(activity, R.drawable.rounded_dialog));

            // Initialize UI elements correctly
            MaterialButton btnPermission = dialogView.findViewById(R.id.btnPermission);
//            MaterialButton btnClose = dialogView.findViewById(R.id.btnClose);

            // Open Location Settings on "Enable" click
            btnPermission.setOnClickListener(v -> {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                activity.startActivity(intent);
                alertDialog.dismiss();
            });

            // Close the dialog on "Close" click
//            btnClose.setOnClickListener(v -> alertDialog.dismiss());

            // Show the dialog
            alertDialog.show();

        } catch (Exception e) {
            Log.e(TAG, "Error displaying GPS enable prompt: ", e);
        }
    }



    public boolean checkBatteryOptimizations() {
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && powerManager != null) {
            boolean isIgnoringOptimizations = powerManager.isIgnoringBatteryOptimizations(context.getPackageName());
            if (!isIgnoringOptimizations) {
                Log.d(TAG, "Battery optimization is enabled. Prompting user to disable it.");
                Intent intent = new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                activity.startActivityForResult(intent, BATTERY_OPTIMIZATION_REQUEST_CODE);
                return false;
            }
        }
        return true;
    }

    public void startLocationService() {
        Log.d(TAG, "Starting LocationService...");
        Intent intent = new Intent(context, LocationService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
        Log.d(TAG, "LocationService started.");
    }

    public void stopLocationService() {
        Log.d(TAG, "Stopping LocationService...");
        Intent intent = new Intent(context, LocationService.class);
        context.stopService(intent);
        Log.d(TAG, "LocationService stopped.");
    }

    public void setSwitchState(boolean isEnabled) {
        int color = isEnabled ? context.getResources().getColor(R.color.primaryColor) : context.getResources().getColor(R.color.gray);
        locationSwitch.setTrackTintList(ColorStateList.valueOf(color));
        locationSwitch.setThumbTintList(ColorStateList.valueOf(color));
        locationSwitch.setChecked(isEnabled);
    }

    public void handlePermissionsResult(int requestCode, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            boolean permissionsGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    permissionsGranted = false;
                    break;
                }
            }

            if (permissionsGranted) {
                Log.d(TAG, "Location permissions granted.");
                if (checkBatteryOptimizations()) {
                    startLocationService();
                    setSwitchState(true);
                }
            } else {
                Log.e(TAG, "Location permissions denied.");
                setSwitchState(false);
                showPermissionRationale();
            }
        }
    }

    private void showPermissionRationale() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)) {
            new AlertDialog.Builder(activity)
                    .setTitle("Location Permission Required")
                    .setMessage("This app needs location permissions to provide location-based services. Please grant the required permissions.")
                    .setPositiveButton("Grant", (dialog, which) -> requestLocationPermissions())
                    .setNegativeButton("Cancel", (dialog, which) -> {
                        Log.d(TAG, "User declined to grant permissions.");
                        setSwitchState(false);
                    })
                    .create()
                    .show();
        } else {
            redirectToAppSettings();
        }
    }

    private void redirectToAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        activity.startActivity(intent);
    }

    public void handleActivityResult(int requestCode) {
        if (requestCode == BATTERY_OPTIMIZATION_REQUEST_CODE) {
            if (checkBatteryOptimizations()) {
                Log.d(TAG, "Battery optimization disabled by user.");
                setSwitchState(true);
                startLocationService();
            } else {
                Log.e(TAG, "Battery optimization still enabled.");
                setSwitchState(false);
            }
        }
    }
}
