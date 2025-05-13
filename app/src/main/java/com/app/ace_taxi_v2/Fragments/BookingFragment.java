package com.app.ace_taxi_v2.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.app.ace_taxi_v2.Logic.AutoCompleteApi;
import com.app.ace_taxi_v2.Models.POI.LocalPOIResponse;
import com.app.ace_taxi_v2.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import org.json.JSONArray;
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
    private AutoCompleteTextView destinationLocationInput;
    private MaterialButton bookButton;
    private TextInputEditText passengerNameInput;
    private TextView priceTextView;
    private ArrayAdapter<String> destinationAdapter;
    private List<String> destinationSuggestions;
    private double routeDistance = 0.0;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private LatLng currentPickupLocation;
    private String currentPickupAddress;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());
    }

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
        destinationLocationInput = view.findViewById(R.id.autoCompleteDestination);
        passengerNameInput = view.findViewById(R.id.passengerName);
        priceTextView = view.findViewById(R.id.price);
        bookButton = view.findViewById(R.id.bookButton);

        // Setup AutoComplete for Destination
        setupAutoCompleteFields();

        // Test Button
        view.findViewById(R.id.testButton).setOnClickListener(v -> {
            destinationLocationInput.showDropDown();
            Log.d("AutoComplete", "Test button: suggestions=" + destinationSuggestions);
        });

        // Check permissions and get current location
        checkLocationPermissions();

        // Handle Booking
        bookButton.setOnClickListener(v -> bookRide());

        // Handle Back Button
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm.isActive(destinationLocationInput)) {
                    imm.hideSoftInputFromWindow(destinationLocationInput.getWindowToken(), 0);
                    if (!destinationSuggestions.isEmpty()) {
                        destinationLocationInput.showDropDown();
                    }
                    return true;
                }
            }
            return false;
        });

        return view;
    }

    private void checkLocationPermissions() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getCurrentLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(getContext(), "Location permission denied", Toast.LENGTH_SHORT).show();
                // Fallback to default location
                LatLng defaultLocation = new LatLng(51.5074, -0.1278);
                currentPickupLocation = defaultLocation;
                currentPickupAddress = "London, UK";
                if (mMap != null) {
                    if (pickupMarker != null) pickupMarker.remove();
                    pickupMarker = mMap.addMarker(new MarkerOptions().position(defaultLocation).title("Pickup Location"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12));
                }
            }
        }
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        currentPickupLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        // Reverse geocode to get address
                        new ReverseGeocodeTask(currentPickupLocation).execute(currentPickupLocation);
                        // Update map with current location as pickup
                        if (mMap != null) {
                            if (pickupMarker != null) pickupMarker.remove();
                            pickupMarker = mMap.addMarker(new MarkerOptions().position(currentPickupLocation).title("Pickup Location"));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPickupLocation, 15));
                        }
                    } else {
                        Toast.makeText(getContext(), "Unable to get current location", Toast.LENGTH_SHORT).show();
                        // Fallback to default location
                        LatLng defaultLocation = new LatLng(51.5074, -0.1278);
                        currentPickupLocation = defaultLocation;
                        currentPickupAddress = "London, UK";
                        if (mMap != null) {
                            if (pickupMarker != null) pickupMarker.remove();
                            pickupMarker = mMap.addMarker(new MarkerOptions().position(defaultLocation).title("Pickup Location"));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12));
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error getting location: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    // Fallback to default location
                    LatLng defaultLocation = new LatLng(51.5074, -0.1278);
                    currentPickupLocation = defaultLocation;
                    currentPickupAddress = "London, UK";
                    if (mMap != null) {
                        if (pickupMarker != null) pickupMarker.remove();
                        pickupMarker = mMap.addMarker(new MarkerOptions().position(defaultLocation).title("Pickup Location"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12));
                    }
                });
    }

    private class ReverseGeocodeTask extends AsyncTask<LatLng, Void, String> {
        private LatLng location;

        public ReverseGeocodeTask(LatLng location) {
            this.location = location;
        }

        @Override
        protected String doInBackground(LatLng... params) {
            String apiKey = getGoogleMapsApiKey();
            String url = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" +
                    location.latitude + "," + location.longitude + "&key=" + apiKey;
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
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
                    return results.getJSONObject(0).getString("formatted_address");
                }
            } catch (Exception e) {
                Log.e("ReverseGeocoding", "Error fetching address", e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String address) {
            if (address != null) {
                currentPickupAddress = address;
            } else {
                currentPickupAddress = "Current Location";
                Toast.makeText(getContext(), "Could not fetch address", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setupAutoCompleteFields() {
        destinationSuggestions = new ArrayList<>();
        destinationAdapter = new ArrayAdapter<String>(requireContext(), android.R.layout.simple_dropdown_item_1line, destinationSuggestions) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view;
                textView.setTextColor(Color.BLACK);
                textView.setPadding(16, 16, 16, 16);
                return view;
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) view;
                textView.setTextColor(Color.BLACK);
                textView.setPadding(16, 16, 16, 16);
                return view;
            }
        };
        destinationLocationInput.setAdapter(destinationAdapter);
        destinationLocationInput.setThreshold(1);
        destinationLocationInput.setDropDownHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        destinationLocationInput.setDropDownWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        destinationLocationInput.setDropDownBackgroundResource(android.R.color.white);
        destinationLocationInput.setDropDownAnchor(R.id.passengerCard);
        destinationLocationInput.setDropDownVerticalOffset(-destinationLocationInput.getHeight());
        destinationLocationInput.addTextChangedListener(new AutoCompleteTextWatcher(false));
        destinationLocationInput.setOnClickListener(v -> {
            if (!destinationSuggestions.isEmpty()) {
                destinationLocationInput.showDropDown();
                Log.d("AutoComplete", "Dropdown triggered on click, suggestions: " + destinationSuggestions.size());
            }
        });
        destinationLocationInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && !destinationSuggestions.isEmpty()) {
                destinationLocationInput.showDropDown();
                Log.d("AutoComplete", "Dropdown triggered on focus, suggestions: " + destinationSuggestions.size());
            }
        });
        destinationLocationInput.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN && !destinationSuggestions.isEmpty()) {
                destinationLocationInput.showDropDown();
            }
            return false;
        });
        destinationLocationInput.setOnItemClickListener((parent, view, position, id) -> {
            String selected = destinationAdapter.getItem(position);
            destinationLocationInput.setText(selected);
            destinationLocationInput.setSelection(selected.length());
            Log.d("AutoComplete", "Selected: " + selected);
        });
        // Test with static data
        destinationSuggestions.add("Test - 123 Main St");
        destinationAdapter.notifyDataSetChanged();
        Log.d("AutoComplete", "Static suggestions added, count: " + destinationAdapter.getCount());
    }

    private class AutoCompleteTextWatcher implements TextWatcher {
        private final boolean isPickup;
        private final Handler handler = new Handler(Looper.getMainLooper());
        private Runnable searchRunnable;

        public AutoCompleteTextWatcher(boolean isPickup) {
            this.isPickup = isPickup;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (searchRunnable != null) {
                handler.removeCallbacks(searchRunnable);
            }
            if (s.length() >= 1) {
                searchRunnable = () -> fetchAutoCompleteSuggestions(s.toString(), isPickup);
                handler.postDelayed(searchRunnable, 300);
            } else {
                requireActivity().runOnUiThread(() -> {
                    destinationSuggestions.clear();
                    destinationAdapter.notifyDataSetChanged();
                    Log.d("AutoComplete", "Cleared suggestions, count: " + destinationAdapter.getCount());
                });
            }
        }

        @Override
        public void afterTextChanged(Editable s) {}
    }

    private void fetchAutoCompleteSuggestions(String query, boolean isPickup) {
        Log.d("AutoComplete", "Fetching suggestions for query: " + query);
        AutoCompleteApi autoCompleteApi = new AutoCompleteApi(requireContext());
        autoCompleteApi.autoCompleteSearch(query, new AutoCompleteApi.AutoCompleteCallback() {
            @Override
            public void onSuccess(List<LocalPOIResponse> response) {
                Log.d("AutoCompleteRaw", "Raw response size: " + response.size());
                for (LocalPOIResponse poi : response) {
                    Log.d("AutoCompleteRaw", "POI: name=" + poi.getName() + ", address=" + poi.getAddress());
                }
                List<String> newSuggestions = new ArrayList<>();
                for (LocalPOIResponse poi : response) {
                    String address = poi.getAddress() != null && !poi.getAddress().isEmpty() ? poi.getAddress() : "Unknown";
                    String postcode = poi.getPostcode() != null && !poi.getPostcode().isEmpty() ? poi.getPostcode() : "No address";
                    if (!address.equals("No address")) {
                        newSuggestions.add(address + " - " + postcode);
                    }
                }
                Log.d("AutoComplete", "Fetched " + newSuggestions.size() + " suggestions: " + newSuggestions);
                if (!isPickup) {
                    requireActivity().runOnUiThread(() -> {
                        destinationSuggestions.clear();
                        destinationSuggestions.addAll(newSuggestions);
                        destinationAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, destinationSuggestions);
                        destinationLocationInput.setAdapter(destinationAdapter);
                        destinationAdapter.notifyDataSetChanged();
                        Log.d("AutoComplete", "Adapter updated, count: " + destinationAdapter.getCount());
                        if (!newSuggestions.isEmpty()) {
                            destinationLocationInput.showDropDown();
                        }
                    });
                }
            }

            @Override
            public void onFail(String error) {
                Log.e("AutoComplete", "Failed to fetch suggestions: " + error);
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Failed to load suggestions: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Default location (London) as fallback
        LatLng defaultLocation = new LatLng(51.5074, -0.1278);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12));
        // Set pickup marker if current location is already fetched
        if (currentPickupLocation != null) {
            if (pickupMarker != null) pickupMarker.remove();
            pickupMarker = mMap.addMarker(new MarkerOptions().position(currentPickupLocation).title("Pickup Location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPickupLocation, 15));
        }
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
                    currentPickupLocation = latLng;
                } else {
                    if (destinationMarker != null) destinationMarker.remove();
                    destinationMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("Destination"));
                }

                if (pickupMarker != null && destinationMarker != null) {
                    drawRoute(pickupMarker.getPosition(), destinationMarker.getPosition());

                    // Adjust camera to show both markers
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    builder.include(pickupMarker.getPosition());
                    builder.include(destinationMarker.getPosition());
                    LatLngBounds bounds = builder.build();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
                }
            } else {
                Toast.makeText(getContext(), "Could not fetch location", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateMap() {
        if (currentPickupLocation == null) {
            Toast.makeText(getContext(), "Pickup location not set", Toast.LENGTH_SHORT).show();
            return;
        }
        String destinationText = destinationLocationInput.getText().toString().trim();

        if (destinationText.isEmpty()) {
            Toast.makeText(getContext(), "Please enter destination", Toast.LENGTH_SHORT).show();
            return;
        }

        getLocationFromAddress(destinationText, false);
    }

    private void bookRide() {
        String destinationText = destinationLocationInput.getText().toString().trim();
        String passengerName = passengerNameInput.getText().toString().trim();

        if (currentPickupLocation == null) {
            Toast.makeText(getContext(), "Pickup location not set", Toast.LENGTH_SHORT).show();
            return;
        }

        if (destinationText.isEmpty()) {
            Toast.makeText(getContext(), "Please enter destination location", Toast.LENGTH_SHORT).show();
            return;
        }

        if (passengerName.isEmpty()) {
            Toast.makeText(getContext(), "Please enter passenger name", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update map with route before booking
        updateMap();

        // Simulate booking confirmation
        Toast.makeText(getContext(), "Ride booked successfully!\nFrom: " + currentPickupAddress + "\nTo: " + destinationText, Toast.LENGTH_LONG).show();

        // Clear inputs after booking
        destinationLocationInput.setText("");
        passengerNameInput.setText("");
        priceTextView.setText("£0.00");
        routeDistance = 0.0;

        // Reset map
        if (destinationMarker != null) destinationMarker.remove();
        if (routePolyline != null) routePolyline.remove();
        if (pickupMarker != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPickupLocation, 15));
        } else {
            LatLng defaultLocation = new LatLng(51.5074, -0.1278);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12));
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
        private double distanceInMeters = 0.0;

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

                    // Calculate distance
                    JSONArray legs = route.getJSONArray("legs");
                    if (legs.length() > 0) {
                        JSONObject leg = legs.getJSONObject(0);
                        JSONObject distance = leg.getJSONObject("distance");
                        distanceInMeters = distance.getDouble("value"); // Distance in meters
                    }

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

            // Calculate and display price based on distance
            routeDistance = distanceInMeters / 1000; // Convert to kilometers
            double price = calculatePrice(routeDistance);
            priceTextView.setText(String.format("£%.2f", price));
        }
    }

    private double calculatePrice(double distanceInKm) {
        // Simple pricing model: £2 base fare + £1.5 per kilometer
        double baseFare = 2.0;
        double perKmRate = 1.5;
        return baseFare + (distanceInKm * perKmRate);
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