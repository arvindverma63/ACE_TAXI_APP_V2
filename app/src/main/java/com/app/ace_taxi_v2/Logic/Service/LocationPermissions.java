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
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.app.ace_taxi_v2.R;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class LocationPermissions {

    private static final String TAG = "LocationPermissions";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private static final int BATTERY_OPTIMIZATION_REQUEST_CODE = 1002;
    private static final int REQUEST_CHECK_SETTINGS = 1003;
    private static final int BACKGROUND_LOCATION_REQUEST_CODE = 101;

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
        boolean hasBackgroundLocation = true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            hasBackgroundLocation = ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED;
            if (!hasBackgroundLocation) {
                Log.e(TAG, "Background location permission is missing.");
                return false;
            }
        }

        if (!hasFineLocation || !hasCoarseLocation) {
            Log.e(TAG, "Foreground location permissions are missing.");
            return false;
        }

        Log.d(TAG, "All required location permissions are granted.");
        return true;
    }

    public boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null) {
            Log.e(TAG, "LocationManager is null");
            return false;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            return locationManager.isLocationEnabled();
        } else {
            try {
                int mode = Settings.Secure.getInt(
                        context.getContentResolver(),
                        Settings.Secure.LOCATION_MODE,
                        Settings.Secure.LOCATION_MODE_OFF
                );
                return mode != Settings.Secure.LOCATION_MODE_OFF;
            } catch (Exception e) {
                Log.e(TAG, "Error checking location mode", e);
                return false;
            }
        }
    }

    public boolean checkBatteryOptimizations() {
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && powerManager != null) {
            return powerManager.isIgnoringBatteryOptimizations(context.getPackageName());
        }
        return true;
    }

    public void ensureAllPermissions() {
        if (!checkLocationPermissions()) {
            requestLocationPermissionsRepeatedly();
        } else if (!isLocationEnabled()) {
            promptEnableGPSRepeatedly();
        } else if (!checkBatteryOptimizations()) {
            requestBatteryOptimizationRepeatedly();
        } else {
            setSwitchState(true);
            startLocationService();
            updateStatusLabel(true);
        }
    }

    public void requestForegroundPermissions() {
        Log.d(TAG, "Requesting foreground location permissions...");
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE);
    }

    public void requestBackgroundLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Requesting background location permission...");
                    ActivityCompat.requestPermissions(activity,
                            new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                            BACKGROUND_LOCATION_REQUEST_CODE);
                } else {
                    Log.d(TAG, "Background location permission already granted");
                    ensureAllPermissions();
                }
            } else {
                Log.d(TAG, "Foreground permissions not granted yet, requesting them first");
                requestForegroundPermissions();
            }
        } else {
            ensureAllPermissions();
        }
    }

    public void promptEnableGPS() {
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10000)
                .setFastestInterval(5000);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        Task<LocationSettingsResponse> task = LocationServices.getSettingsClient(activity)
                .checkLocationSettings(builder.build());

        task.addOnSuccessListener(locationSettingsResponse -> {
            Log.d(TAG, "Location settings are already enabled");
            ensureAllPermissions();
        });

        task.addOnFailureListener(exception -> {
            if (exception instanceof ResolvableApiException) {
                try {
                    ResolvableApiException resolvable = (ResolvableApiException) exception;
                    resolvable.startResolutionForResult(activity, REQUEST_CHECK_SETTINGS);
                } catch (Exception e) {
                    Log.e(TAG, "Error showing location settings dialog", e);
                    showCustomGPSPrompt();
                }
            } else {
                Log.e(TAG, "Location settings cannot be resolved automatically", exception);
                showCustomGPSPrompt();
            }
        });
    }

    private void showCustomGPSPrompt() {
        try {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(activity);
            View dialogView = LayoutInflater.from(activity).inflate(R.layout.location_permission_dialog, null);
            builder.setView(dialogView);
            builder.setCancelable(false);

            AlertDialog alertDialog = builder.create();
            if (alertDialog.getWindow() != null) {
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            }
            dialogView.setBackground(ContextCompat.getDrawable(activity, R.drawable.rounded_dialog));

            MaterialButton btnPermission = dialogView.findViewById(R.id.btnPermission);
            btnPermission.setOnClickListener(v -> {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                activity.startActivityForResult(intent, REQUEST_CHECK_SETTINGS);
                alertDialog.dismiss();
            });

            alertDialog.show();
        } catch (Exception e) {
            Log.e(TAG, "Error displaying custom GPS prompt", e);
        }
    }

    private void requestLocationPermissionsRepeatedly() {
        try {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(activity)
                    .setTitle("Location Permissions Required")
                    .setMessage("This app needs location access (including background location on Android 10+) to function properly. Please grant all requested permissions.")
                    .setCancelable(false)
                    .setPositiveButton("Grant", (dialog, which) -> {
                        dialog.dismiss();
                        requestForegroundPermissions();
                    })
                    .setNegativeButton("Exit", (dialog, which) -> activity.finish());

            AlertDialog dialog = builder.create();
            dialog.show();
        } catch (Exception e) {
            Log.e(TAG, "Error showing permission dialog", e);
            requestForegroundPermissions();
        }
    }

    private void requestBackgroundPermissionsRepeatedly() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(activity)
                        .setTitle("Background Location Permission")
                        .setMessage("This app requires background location access to provide location updates even when the app is closed.")
                        .setCancelable(false)
                        .setPositiveButton("Grant", (dialog, which) -> {
                            dialog.dismiss();
                            requestBackgroundLocationPermission();
                        })
                        .setNegativeButton("Exit", (dialog, which) -> activity.finish());

                AlertDialog dialog = builder.create();
                dialog.show();
            } catch (Exception e) {
                Log.e(TAG, "Error showing background permission dialog", e);
                requestBackgroundLocationPermission();
            }
        } else {
            ensureAllPermissions();
        }
    }

    private void promptEnableGPSRepeatedly() {
        try {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(activity)
                    .setTitle("GPS Required")
                    .setMessage("Please enable GPS to use location services.")
                    .setCancelable(false)
                    .setPositiveButton("Enable", (dialog, which) -> {
                        dialog.dismiss();
                        promptEnableGPS();
                    })
                    .setNegativeButton("Exit", (dialog, which) -> activity.finish());

            AlertDialog dialog = builder.create();
            dialog.show();
        } catch (Exception e) {
            Log.e(TAG, "Error showing GPS dialog", e);
        }
    }

    private void requestBatteryOptimizationRepeatedly() {
        try {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(activity)
                    .setTitle("Battery Optimization")
                    .setMessage("Please disable battery optimization for this app to work properly in the background.")
                    .setCancelable(false)
                    .setPositiveButton("Disable", (dialog, which) -> {
                        Intent intent = new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                        activity.startActivityForResult(intent, BATTERY_OPTIMIZATION_REQUEST_CODE);
                        dialog.dismiss();
                    })
                    .setNegativeButton("Exit", (dialog, which) -> activity.finish());

            AlertDialog dialog = builder.create();
            dialog.show();
        } catch (Exception e) {
            Log.e(TAG, "Error showing battery optimization dialog", e);
        }
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
                Log.d(TAG, "Foreground location permissions granted.");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    requestBackgroundPermissionsRepeatedly();
                } else {
                    ensureAllPermissions();
                }
            } else {
                Log.e(TAG, "Foreground location permissions denied.");
                setSwitchState(false);
                requestLocationPermissionsRepeatedly();
            }
        } else if (requestCode == BACKGROUND_LOCATION_REQUEST_CODE) {
            boolean permissionsGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    permissionsGranted = false;
                    break;
                }
            }

            if (permissionsGranted) {
                Log.d(TAG, "Background location permission granted.");
                ensureAllPermissions();
            } else {
                Log.e(TAG, "Background location permission denied.");
                setSwitchState(false);
                requestBackgroundPermissionsRepeatedly();
            }
        }
    }

    public void handleActivityResult(int requestCode, int resultCode) {
        if (requestCode == BATTERY_OPTIMIZATION_REQUEST_CODE) {
            if (checkBatteryOptimizations()) {
                Log.d(TAG, "Battery optimization disabled.");
                ensureAllPermissions();
            } else {
                Log.e(TAG, "Battery optimization still enabled.");
                setSwitchState(false);
                requestBatteryOptimizationRepeatedly();
            }
        } else if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == Activity.RESULT_OK) {
                Log.d(TAG, "User enabled location settings");
                ensureAllPermissions();
            } else {
                Log.d(TAG, "User declined to enable location settings");
                setSwitchState(false);
                promptEnableGPSRepeatedly();
            }
        }
    }

    private void updateStatusLabel(boolean isOnline) {
        if (onlineStatusLabel != null) {
            activity.runOnUiThread(() -> {
                onlineStatusLabel.setText(isOnline ? "Send Location ON" : "Send Location OFF");
                onlineStatusLabel.setTextColor(ContextCompat.getColor(context,
                        isOnline ? R.color.green : R.color.red));
            });
        }
    }

    private boolean shouldShowRequestPermissionRationale() {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION) ||
                ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_COARSE_LOCATION) ||
                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
                        ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_BACKGROUND_LOCATION));
    }

    private void showPermissionRationale() {
        try {
            new MaterialAlertDialogBuilder(activity)
                    .setTitle("Location Permission Required")
                    .setMessage("This app needs location permissions (including background on Android 10+) to provide location-based services. Please grant all permissions.")
                    .setPositiveButton("Grant", (dialog, which) -> {
                        dialog.dismiss();
                        requestForegroundPermissions();
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> {
                        Log.d(TAG, "User declined to grant permissions in rationale.");
                        setSwitchState(false);
                    })
                    .setCancelable(false)
                    .create()
                    .show();
        } catch (Exception e) {
            Log.e(TAG, "Error showing permission rationale", e);
            requestForegroundPermissions();
        }
    }

    private void redirectToAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        activity.startActivity(intent);
    }
}