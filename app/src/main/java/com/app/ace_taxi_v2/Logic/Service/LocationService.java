package com.app.ace_taxi_v2.Logic.Service;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.app.ace_taxi_v2.Logic.SendLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class LocationService extends Service {

    private static final String TAG = "LocationService";
    private static final String CHANNEL_ID = "location_service_channel";

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service created");

        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null && locationResult.getLocations() != null) {
                    Log.d(TAG, "LocationCallback triggered with " + locationResult.getLocations().size() + " location(s).");
                    for (android.location.Location location : locationResult.getLocations()) {
                        float speed = location.hasSpeed() ? location.getSpeed() : 0.0f; // Speed in meters/second
                        float heading = location.hasBearing() ? location.getBearing() : 0.0f; // Heading in degrees
                        Log.d(TAG, "Location received: " + location.getLatitude() + ", " + location.getLongitude() + ", speed: " + speed + " Heading: " + heading);
                        // Process location data
                        SendLocation sendLocation = new SendLocation(
                                getApplicationContext(),
                                location.getLatitude(),
                                location.getLongitude(),
                                speed, heading  // speed and heading
                        );
                        sendLocation.sendLocation();
                    }
                } else {
                    Log.e(TAG, "LocationCallback triggered, but no location received.");
                }
            }
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service started");

        try {

            // Start foreground service with notification
            startForeground(1, createNotification());

        } catch (Exception e) {
            e.printStackTrace();
        }
        // Check permissions and start location updates
        if (checkPermissions()) {
            requestLocationUpdates();
        } else {
            Log.e(TAG, "Location permissions are missing. Stopping service.");
            stopSelf();
        }

        // Check and handle battery optimizations without redirecting
        checkBatteryOptimizations();

        return START_STICKY;
    }

    private Notification createNotification() {
        // Create notification channel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Location Service",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Location Service")
                .setContentText("Tracking your location...")
                .setSmallIcon(android.R.drawable.ic_menu_mylocation) // Use a valid icon
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();
    }

    private boolean checkPermissions() {
        boolean hasFineLocation = ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            boolean hasBackgroundLocation = ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED;
            if (!hasBackgroundLocation) {
                Log.e(TAG, "Background location permission is missing.");
                return false;
            }
        }

        if (!hasFineLocation) {
            Log.e(TAG, "Fine location permission is missing.");
            return false;
        }

        Log.d(TAG, "All required location permissions are granted.");
        return true;
    }

    private void requestLocationUpdates() {
        // Configure location request
        LocationRequest locationRequest = LocationRequest.create()
                .setInterval(10000) // 10 seconds
                .setFastestInterval(5000) // 5 seconds
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Requesting location updates...");
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Location updates successfully started."))
                    .addOnFailureListener(e -> Log.e(TAG, "Failed to start location updates: " + e.getMessage()));
        } else {
            Log.e(TAG, "Permissions not granted for location updates.");
        }
    }

    private void checkBatteryOptimizations() {
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        if (powerManager != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean isIgnoringOptimizations = powerManager.isIgnoringBatteryOptimizations(getPackageName());
            if (!isIgnoringOptimizations) {
                Log.w(TAG, "Battery optimization is enabled. Please disable it manually in Battery settings for optimal performance.");
                // Optionally, update the notification to inform the user
                updateNotificationForBatteryOptimization();
            } else {
                Log.d(TAG, "Battery optimization is already disabled for this app.");
            }
        }
    }

    private void updateNotificationForBatteryOptimization() {
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Location Service")
                .setContentText("Battery optimization is enabled. Disable it manually for best performance.")
                .setSmallIcon(android.R.drawable.ic_menu_mylocation)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();

        NotificationManager manager = getSystemService(NotificationManager.class);
        if (manager != null) {
            manager.notify(1, notification);
            Log.d(TAG, "Notification updated to warn about battery optimization.");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Service destroyed");

        // Remove location updates
        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
            Log.d(TAG, "Location updates removed.");
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // This service is not meant to be bound
        return null;
    }
}