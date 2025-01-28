package com.example.ace_taxi_v2.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.core.app.ActivityCompat;

import com.example.ace_taxi_v2.Activity.HomeActivity;
import com.example.ace_taxi_v2.Activity.LoginActivity;
import com.example.ace_taxi_v2.JobModals.JobModal;
import com.example.ace_taxi_v2.Logic.JobApi.GetBookingById;
import com.example.ace_taxi_v2.Logic.Service.LocationService;
import com.example.ace_taxi_v2.Logic.SessionManager;
import com.example.ace_taxi_v2.R;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private static final int BATTERY_OPTIMIZATION_REQUEST_CODE = 1002;

    private Switch locationSwitch;
    private TextView onlineStatusLabel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);



        SessionManager sessionManager = new SessionManager(getContext());
        if (!sessionManager.isLoggedIn()) {
            Intent intent = new Intent(getContext(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
        }

        // Initialize UI components
        locationSwitch = view.findViewById(R.id.online_toggle);
        onlineStatusLabel = view.findViewById(R.id.online_status_label);

        // Set up the Switch listener
        locationSwitch.setOnCheckedChangeListener((CompoundButton compoundButton, boolean isChecked) -> {
            if (isChecked) {
                if (isLocationEnabled()) {
                    if (checkLocationPermissions() && checkBatteryOptimizations()) {
                        startLocationService();
                        setSwitchState(true);
                        onlineStatusLabel.setText("You Are Online");
                    } else {
                        requestLocationPermissions();
                        setSwitchState(false);
                    }
                } else {
                    Log.e(TAG, "Location services (GPS) are disabled.");
                    promptEnableGPS();
                    setSwitchState(false);
                }
            } else {
                setSwitchState(false);
                stopLocationService();
                onlineStatusLabel.setText("You Are Offline");
            }
        });

        return view;
    }

    private boolean checkLocationPermissions() {
        boolean hasFineLocation = ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        boolean hasCoarseLocation = ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            boolean hasBackgroundLocation = ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED;
            if (!hasBackgroundLocation) {
                Log.e(TAG, "Background location permission is missing.");
                return false;
            }
        }

        if (!hasFineLocation && !hasCoarseLocation) {
            Log.e(TAG, "Location permissions are missing.");
            return false;
        }

        Log.d(TAG, "All required location permissions are granted.");
        return true;
    }

    private void requestLocationPermissions() {
        Log.d(TAG, "Requesting location permissions...");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { // Android 10+
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
            }, LOCATION_PERMISSION_REQUEST_CODE);
        } else { // Pre-Android 10
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            return locationManager != null && locationManager.isLocationEnabled();
        } else {
            int mode = Settings.Secure.getInt(
                    getContext().getContentResolver(),
                    Settings.Secure.LOCATION_MODE,
                    Settings.Secure.LOCATION_MODE_OFF
            );
            return mode != Settings.Secure.LOCATION_MODE_OFF;
        }
    }

    private void promptEnableGPS() {
        new AlertDialog.Builder(getContext())
                .setTitle("Enable Location Services")
                .setMessage("Location services are required for this feature. Please enable them.")
                .setPositiveButton("Settings", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    Log.d(TAG, "User declined to enable GPS.");
                    setSwitchState(false);
                })
                .create()
                .show();
    }

    private boolean checkBatteryOptimizations() {
        PowerManager powerManager = (PowerManager) getContext().getSystemService(Context.POWER_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && powerManager != null) {
            boolean isIgnoringOptimizations = powerManager.isIgnoringBatteryOptimizations(getContext().getPackageName());
            if (!isIgnoringOptimizations) {
                Log.d(TAG, "Battery optimization is enabled. Prompting user to disable it.");
                Intent intent = new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                startActivityForResult(intent, BATTERY_OPTIMIZATION_REQUEST_CODE);
                return false;
            }
        }
        return true;
    }

    private void startLocationService() {
        Log.d(TAG, "Starting LocationService...");
        Intent intent = new Intent(getActivity(), LocationService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getActivity().startForegroundService(intent);
        } else {
            getActivity().startService(intent);
        }
        Log.d(TAG, "LocationService started.");
    }

    private void stopLocationService() {
        Log.d(TAG, "Stopping LocationService...");
        Intent intent = new Intent(getActivity(), LocationService.class);
        getActivity().stopService(intent);
        Log.d(TAG, "LocationService stopped.");
    }


    private void setSwitchState(boolean isEnabled) {
        int color = isEnabled ? getResources().getColor(R.color.primaryColor) : getResources().getColor(R.color.gray);
        locationSwitch.setTrackTintList(ColorStateList.valueOf(color));
        locationSwitch.setThumbTintList(ColorStateList.valueOf(color));
        locationSwitch.setChecked(isEnabled);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            for (int i = 0; i < permissions.length; i++) {
                Log.d(TAG, "Permission: " + permissions[i] + " Result: " + grantResults[i]);
            }

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
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
            new AlertDialog.Builder(getContext())
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
        intent.setData(Uri.parse("package:" + getContext().getPackageName()));
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @NonNull Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
