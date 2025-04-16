package com.app.ace_taxi_v2.GoogleMap;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapManager implements OnMapReadyCallback {

    private final Context context;
    private final Fragment fragment;
    private final MapView mapView;
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private OnPermissionResultListener permissionResultListener;

    // Interface for permission result callback
    public interface OnPermissionResultListener {
        void onPermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults);
    }

    public MapManager(Context context, Fragment fragment, MapView mapView) {
        this.context = context;
        this.fragment = fragment;
        this.mapView = mapView;
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
    }

    // Initialize the map
    public void initializeMap(Bundle savedInstanceState) {
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        // Check location permission
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Request permission via Fragment
            fragment.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            enableMyLocation();
        }
    }

    // Enable current location on map
    private void enableMyLocation() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);

            // Get last known location
            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    addMarker(currentLocation, "My Location");
                    updateMap(currentLocation, 15f);
                } else {
                    Toast.makeText(context, "Unable to get location", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // Add a marker to the map
    public void addMarker(LatLng latLng, String title) {
        if (googleMap != null) {
            googleMap.addMarker(new MarkerOptions().position(latLng).title(title));
        }
    }

    // Update map camera to a specific location and zoom level
    public void updateMap(LatLng latLng, float zoom) {
        if (googleMap != null) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        }
    }

    // Handle permission result
    public void handlePermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocation();
            } else {
                Toast.makeText(context, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Set permission result listener
    public void setOnPermissionResultListener(OnPermissionResultListener listener) {
        this.permissionResultListener = listener;
    }

    // MapView lifecycle methods
    public void onResume() {
        mapView.onResume();
    }

    public void onPause() {
        mapView.onPause();
    }

    public void onDestroy() {
        mapView.onDestroy();
    }

    public void onLowMemory() {
        mapView.onLowMemory();
    }
}