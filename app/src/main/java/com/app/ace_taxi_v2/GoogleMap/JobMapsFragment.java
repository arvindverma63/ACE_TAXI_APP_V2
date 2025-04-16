package com.app.ace_taxi_v2.GoogleMap;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.app.ace_taxi_v2.R;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;

public class JobMapsFragment extends DialogFragment {

    private static final String ARG_LATITUDE = "latitude";
    private static String ARG_LONGITUDE = "longitude";
    private static final String ARG_TITLE = "title";

    private MapManager mapManager;
    private Double latitude;
    private Double longitude;
    private String title;

    // Factory method to create a new instance with optional location
    public static JobMapsFragment newInstance(Double latitude, Double longitude, String title) {
        JobMapsFragment fragment = new JobMapsFragment();
        Bundle args = new Bundle();
        if (latitude != null && longitude != null) {
            args.putDouble(ARG_LATITUDE, latitude);
            args.putDouble(ARG_LONGITUDE, longitude);
            args.putString(ARG_TITLE, title != null ? title : "Location");
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set full-screen style

        // Parse arguments
        if (getArguments() != null) {
            latitude = getArguments().getDouble(ARG_LATITUDE, 0);
            longitude = getArguments().getDouble(ARG_LONGITUDE, 0);
            title = getArguments().getString(ARG_TITLE, "Location");
            // Check if valid location was passed
            if (latitude == 0 && longitude == 0) {
                latitude = null;
                longitude = null;
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_job_maps, container, false);

        // Initialize MapView
        MapView mapView = view.findViewById(R.id.mapView);

        // Initialize MapManager
        mapManager = new MapManager(requireContext(), this, mapView);
        mapManager.initializeMap(savedInstanceState);

        // Set permission result listener
        mapManager.setOnPermissionResultListener(this::onRequestPermissionsResult);

        // If a specific location was passed, show it
        if (latitude != null && longitude != null) {
            LatLng location = new LatLng(latitude, longitude);
            mapManager.addMarker(location, title);
            mapManager.updateMap(location, 15f);
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Make dialog full-screen
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT
            );
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mapManager.handlePermissionResult(requestCode, permissions, grantResults);
    }

    // Add a new location marker and update map
    public void addNewLocation(double latitude, double longitude, String title) {
        LatLng newLocation = new LatLng(latitude, longitude);
        mapManager.addMarker(newLocation, title);
        mapManager.updateMap(newLocation, 15f);
    }

    // Lifecycle methods
    @Override
    public void onResume() {
        super.onResume();
        mapManager.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapManager.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mapManager.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapManager.onLowMemory();
    }
}