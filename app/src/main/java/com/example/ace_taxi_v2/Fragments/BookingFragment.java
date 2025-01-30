package com.example.ace_taxi_v2.Fragments;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.ace_taxi_v2.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class BookingFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker pickupMarker, destinationMarker;
    private Polyline routePolyline;
    private boolean isInputVisible = false;
    private TextInputEditText pickupLocationInput, destinationLocationInput;
    private LinearLayout inputSection;
    private MaterialButton toggleButton, updateMapButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_booking, container, false);

        // Initialize Google Maps
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Initialize UI Elements
        inputSection = view.findViewById(R.id.inputSection);
        toggleButton = view.findViewById(R.id.toggleInputSection);
        updateMapButton = view.findViewById(R.id.updateMapButton);
        pickupLocationInput = view.findViewById(R.id.pickupLocation);
        destinationLocationInput = view.findViewById(R.id.destinationLocation);

        // Toggle Input Section Visibility
        toggleButton.setOnClickListener(v -> {
            if (isInputVisible) {
                inputSection.setVisibility(View.GONE);
                toggleButton.setText("Show Pickup & Destination");
            } else {
                inputSection.setVisibility(View.VISIBLE);
                toggleButton.setText("Hide Pickup & Destination");
            }
            isInputVisible = !isInputVisible;
        });

        // Update Map with New Locations and Draw Route
        updateMapButton.setOnClickListener(v -> updateMap());

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Default location (London)
        LatLng defaultLocation = new LatLng(51.5074, -0.1278);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12));

        // Default Pickup & Destination
        LatLng pickup = new LatLng(51.5074, -0.1278);
        LatLng destination = new LatLng(51.5155, -0.1410);

        pickupMarker = mMap.addMarker(new MarkerOptions().position(pickup).title("Pickup Location"));
        destinationMarker = mMap.addMarker(new MarkerOptions().position(destination).title("Destination"));

        // Draw route for default locations
        drawRoute(pickup, destination);
    }

    private void updateMap() {
        String pickupText = pickupLocationInput.getText().toString().trim();
        String destinationText = destinationLocationInput.getText().toString().trim();

        if (pickupText.isEmpty() || destinationText.isEmpty()) {
            Toast.makeText(getContext(), "Please enter both locations", Toast.LENGTH_SHORT).show();
            return;
        }

        // Dummy LatLng for demonstration (Replace with actual Geocoding)
        LatLng newPickup = new LatLng(51.5081, -0.1281);
        LatLng newDestination = new LatLng(51.5205, -0.1420);

        if (pickupMarker != null) pickupMarker.setPosition(newPickup);
        if (destinationMarker != null) destinationMarker.setPosition(newDestination);

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(newPickup, 13));

        // Draw route between new points
        drawRoute(newPickup, newDestination);

        Toast.makeText(getContext(), "Map updated with new locations!", Toast.LENGTH_SHORT).show();
    }

    private void drawRoute(LatLng origin, LatLng destination) {
        String apiKey = getGoogleMapsApiKey();
        String url = "https://maps.googleapis.com/maps/api/directions/json?" +
                "origin=" + origin.latitude + "," + origin.longitude +
                "&destination=" + destination.latitude + "," + destination.longitude +
                "&key=" + apiKey;

        new FetchRoute().execute(url);
    }

    private class FetchRoute extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(url[0]).openConnection();
                connection.setRequestMethod("GET");
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                return response.toString();
            } catch (Exception e) {
                Log.e("GoogleMaps", "Error fetching route", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(String response) {
            if (response == null) {
                Toast.makeText(getContext(), "Failed to fetch route", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray routes = jsonObject.getJSONArray("routes");
                if (routes.length() > 0) {
                    JSONObject route = routes.getJSONObject(0);
                    JSONArray legs = route.getJSONArray("legs");
                    JSONArray steps = legs.getJSONObject(0).getJSONArray("steps");

                    List<LatLng> points = new ArrayList<>();
                    for (int i = 0; i < steps.length(); i++) {
                        JSONObject step = steps.getJSONObject(i);
                        JSONObject start = step.getJSONObject("start_location");
                        LatLng startLatLng = new LatLng(start.getDouble("lat"), start.getDouble("lng"));
                        points.add(startLatLng);
                    }

                    if (routePolyline != null) {
                        routePolyline.remove();
                    }

                    routePolyline = mMap.addPolyline(new PolylineOptions()
                            .addAll(points)
                            .width(8)
                            .color(Color.BLUE)
                            .geodesic(true));
                }
            } catch (JSONException e) {
                Log.e("GoogleMaps", "Error parsing JSON", e);
            }
        }
    }

    private String getGoogleMapsApiKey() {
        try {
            Context context = getContext();
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            return appInfo.metaData.getString("com.google.android.geo.API_KEY");
        } catch (Exception e) {
            Log.e("GoogleMaps", "Error retrieving API key", e);
            return "";
        }
    }
}
