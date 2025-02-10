package com.example.ace_taxi_v2.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ace_taxi_v2.Logic.AutoCompleteApi;
import com.example.ace_taxi_v2.Models.POI.LocalPOIResponse;
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
    private AutoCompleteTextView pickupLocationInput, destinationLocationInput;
    private LinearLayout inputSection;
    private MaterialButton toggleButton, updateMapButton;
    private ArrayAdapter<String> pickupAdapter, destinationAdapter;
    private List<String> pickupSuggestions, destinationSuggestions;

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
        pickupLocationInput = view.findViewById(R.id.autoCompletePickup);
        destinationLocationInput = view.findViewById(R.id.autoCompleteDestination);

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


        setupAutoCompleteFields();
        // Update Map with New Locations and Draw Route
        updateMapButton.setOnClickListener(v -> updateMap());

        return view;
    }

    private void setupAutoCompleteFields() {
        pickupSuggestions = new ArrayList<>();
        destinationSuggestions = new ArrayList<>();

        pickupAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, pickupSuggestions);
        destinationAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, destinationSuggestions);

        pickupLocationInput.setAdapter(pickupAdapter);
        destinationLocationInput.setAdapter(destinationAdapter);

        pickupLocationInput.setThreshold(1);
        destinationLocationInput.setThreshold(1);

        // Add text listeners
        pickupLocationInput.addTextChangedListener(new AutoCompleteTextWatcher(true));
        destinationLocationInput.addTextChangedListener(new AutoCompleteTextWatcher(false));
    }

    private class AutoCompleteTextWatcher implements TextWatcher {
        private boolean isPickup;

        public AutoCompleteTextWatcher(boolean isPickup) {
            this.isPickup = isPickup;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() >= 2) {  // Start searching after 2 characters
                fetchAutoCompleteSuggestions(s.toString(), isPickup);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {}
    }

    private void fetchAutoCompleteSuggestions(String query, boolean isPickup) {
        AutoCompleteApi autoCompleteApi = new AutoCompleteApi(requireContext());
        autoCompleteApi.autoCompleteSearch(query, new AutoCompleteApi.AutoCompleteCallback() {
            @Override
            public void onSuccess(List<LocalPOIResponse> response) {
                List<String> newSuggestions = new ArrayList<>();
                for (LocalPOIResponse poi : response) {
                    newSuggestions.add(poi.getName() + " - " + poi.getAddress());
                }

                if (isPickup) {
                    pickupSuggestions.clear();
                    pickupSuggestions.addAll(newSuggestions);
                    pickupAdapter.notifyDataSetChanged();
                } else {
                    destinationSuggestions.clear();
                    destinationSuggestions.addAll(newSuggestions);
                    destinationAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFail(String error) {
                Log.e("AutoComplete", "Failed to fetch suggestions: " + error);
            }
        });
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



    private void getLocationFromAddress(String address, boolean isPickup) {
        String apiKey = getGoogleMapsApiKey();
        String url = "https://maps.googleapis.com/maps/api/geocode/json?address=" +
                address.replace(" ", "%20") + "&key=" + apiKey;

        new FetchLocationTask(isPickup).execute(url);
    }

    private class FetchLocationTask extends AsyncTask<String, Void, LatLng> {
        private boolean isPickup;

        public FetchLocationTask(boolean isPickup) {
            this.isPickup = isPickup;
        }

        @Override
        protected LatLng doInBackground(String... urls) {
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(urls[0]).openConnection();
                connection.setRequestMethod("GET");
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JSONObject jsonObject = new JSONObject(response.toString());
                JSONArray results = jsonObject.getJSONArray("results");

                if (results.length() > 0) {
                    JSONObject location = results.getJSONObject(0)
                            .getJSONObject("geometry")
                            .getJSONObject("location");
                    return new LatLng(location.getDouble("lat"), location.getDouble("lng"));
                }
            } catch (Exception e) {
                Log.e("Geocoding", "Error fetching location", e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(LatLng latLng) {
            if (latLng != null) {
                if (isPickup) {
                    if (pickupMarker != null) pickupMarker.remove();
                    pickupMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("Pickup Location"));
                } else {
                    if (destinationMarker != null) destinationMarker.remove();
                    destinationMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("Destination"));
                }

                if (pickupMarker != null && destinationMarker != null) {
                    drawRoute(pickupMarker.getPosition(), destinationMarker.getPosition());
                }
            } else {
                Toast.makeText(getContext(), "Could not fetch location", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void updateMap() {
        String pickupText = pickupLocationInput.getText().toString().trim();
        String destinationText = destinationLocationInput.getText().toString().trim();

        if (pickupText.isEmpty() || destinationText.isEmpty()) {
            Toast.makeText(getContext(), "Please enter both locations", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convert location names to a Google Maps URL format
        String uri = "https://www.google.com/maps/dir/?api=1&origin="
                + Uri.encode(pickupText)
                + "&destination="
                + Uri.encode(destinationText)
                + "&travelmode=driving";

        // Open Google Maps App
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setPackage("com.google.android.apps.maps");  // Ensure it opens in Google Maps
        if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivity(intent);
        } else {
            // If Google Maps app is not available, open in browser
            intent.setPackage(null);
            startActivity(intent);
        }
    }


    private void drawRoute(LatLng origin, LatLng destination) {
        String apiKey = getGoogleMapsApiKey();
        String url = "https://maps.googleapis.com/maps/api/directions/json?" +
                "origin=" + origin.latitude + "," + origin.longitude +
                "&destination=" + destination.latitude + "," + destination.longitude +
                "&key=" + apiKey;

        new FetchRoute().execute(url);
    }

    private class FetchRoute extends AsyncTask<String, Void, List<LatLng>> {
        @Override
        protected List<LatLng> doInBackground(String... urls) {
            List<LatLng> routePoints = new ArrayList<>();
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(urls[0]).openConnection();
                connection.setRequestMethod("GET");
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                Log.d("DirectionsResponse", response.toString());  // Log API Response

                JSONObject jsonObject = new JSONObject(response.toString());
                String status = jsonObject.getString("status");

                if (!status.equals("OK")) {
                    Log.e("DirectionsAPI", "Error: " + status);
                    return null;
                }

                JSONArray routes = jsonObject.getJSONArray("routes");

                if (routes.length() > 0) {
                    JSONObject route = routes.getJSONObject(0);
                    JSONObject overviewPolyline = route.getJSONObject("overview_polyline");
                    String encodedPolyline = overviewPolyline.getString("points");

                    routePoints = decodePolyline(encodedPolyline);
                }
            } catch (Exception e) {
                Log.e("DirectionsAPI", "Error fetching route", e);
            }
            return routePoints;
        }

        @Override
        protected void onPostExecute(List<LatLng> points) {
            if (points == null || points.isEmpty()) {
                Toast.makeText(getContext(), "Could not fetch route", Toast.LENGTH_SHORT).show();
                return;
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
    }


    private List<LatLng> decodePolyline(String encoded) {
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng(lat / 1E5, lng / 1E5);
            poly.add(p);
        }
        return poly;
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
