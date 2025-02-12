package com.app.ace_taxi_v2.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.app.ace_taxi_v2.Activity.LoginActivity;
import com.app.ace_taxi_v2.Logic.Service.LocationPermissions;
import com.app.ace_taxi_v2.Logic.SessionManager;
import com.app.ace_taxi_v2.R;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    private Switch locationSwitch;
    private TextView onlineStatusLabel;
    private LocationPermissions locationPermissions;  // LocationPermissions instance

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Check user session
        if (getActivity() == null) return view;
        SessionManager sessionManager = new SessionManager(getActivity());
        if (!sessionManager.isLoggedIn()) {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
            return view;
        }

        // Initialize UI components
        locationSwitch = view.findViewById(R.id.online_toggle);
        onlineStatusLabel = view.findViewById(R.id.online_status_label);

        // Initialize LocationPermissions instance
        locationPermissions = new LocationPermissions(getActivity(), locationSwitch, onlineStatusLabel);

        // Set up the Switch listener
        locationSwitch.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (isChecked) {
                if (locationPermissions.isLocationEnabled()) {
                    if (locationPermissions.checkLocationPermissions() && locationPermissions.checkBatteryOptimizations()) {
                        locationPermissions.startLocationService();
                        locationPermissions.setSwitchState(true);
                        onlineStatusLabel.setText("You Are Online");
                    } else {
                        locationPermissions.requestLocationPermissions();
                        locationPermissions.setSwitchState(false);
                    }
                } else {
                    Log.e(TAG, "Location services (GPS) are disabled.");
                    locationPermissions.promptEnableGPS();
                    locationPermissions.setSwitchState(false);
                }
            } else {
                locationPermissions.setSwitchState(false);
                locationPermissions.stopLocationService();
                onlineStatusLabel.setText("You Are Offline");
            }
        });

        return view;
    }
}
