package com.app.ace_taxi_v2.Fragments.RankPickup;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.app.ace_taxi_v2.Components.CustomToast;
import com.app.ace_taxi_v2.Fragments.BookingFragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MapAndLocationManager implements OnMapReadyCallback {
    private final BookingFragment fragment;
    private GoogleMap mMap;
    private Marker pickupMarker;
    private Marker destinationMarker;
    private Polyline routePolyline;
    private FusedLocationProviderClient fusedLocationClient;
    private LatLng currentPickupLocation;
    private String currentPickupAddress;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    public CustomToast customToast;

    public MapAndLocationManager(BookingFragment fragment) {
        this.fragment = fragment;
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(fragment.requireContext());
        this.customToast = new CustomToast(fragment.getContext());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng defaultLocation = new LatLng(51.5074, -0.1278);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12));

        if (currentPickupLocation != null) {
            updatePickupMarker(currentPickupLocation);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPickupLocation, 15));
        }
    }

    public void checkLocationPermissions() {
        if (ContextCompat.checkSelfPermission(fragment.requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(fragment.requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getCurrentLocation();
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                handleLocationPermissionDenied();
            }
        }
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(fragment.requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        currentPickupLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        new ReverseGeocodeTask().execute(currentPickupLocation);
                        updatePickupMarker(currentPickupLocation);
                    } else {
                        handleLocationFetchFailure("Unable to get current location");
                    }
                })
                .addOnFailureListener(e ->
                        handleLocationFetchFailure("Error getting location: " + e.getMessage()));
    }

    private void handleLocationPermissionDenied() {
        customToast.showCustomErrorToast("Location permission denied");
        setDefaultLocation();
    }

    private void handleLocationFetchFailure(String message) {
        customToast.showCustomToast(message);
        setDefaultLocation();
    }

    private void setDefaultLocation() {
        LatLng defaultLocation = new LatLng(51.5074, -0.1278);
        currentPickupLocation = defaultLocation;
        currentPickupAddress = "London, UK";
        String defaultPostcode = "SW1A 1AA";
        AutoCompleteTextView pickupInput = fragment.getPickupLocationInput();
        if (pickupInput != null) {
            pickupInput.setText("SP8 4PZ");

        } else {
            Log.e("MapAndLocationManager", "pickupLocationInput is null in setDefaultLocation");
        }
        updatePickupMarker(defaultLocation);
        if (mMap != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12));
        }
    }

    private void updatePickupMarker(LatLng location) {
        if (mMap != null) {
            if (pickupMarker != null) pickupMarker.remove();
            pickupMarker = mMap.addMarker(new MarkerOptions().position(location).title("Pickup Location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
        }
    }

    public void getLocationFromAddress(String address, boolean isPickup, OnLocationFetchedListener listener) {
        String apiKey = getGoogleMapsApiKey();
        String url = "https://maps.googleapis.com/maps/api/geocode/json?address=" +
                address.replace(" ", "%20") + "&key=" + apiKey;
        new FetchLocationTask(isPickup, listener).execute(url);
    }

    private class ReverseGeocodeTask extends AsyncTask<LatLng, Void, String[]> {
        @Override
        protected String[] doInBackground(LatLng... params) {
            LatLng location = params[0];
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
                String status = jsonObject.getString("status");
                if (!status.equals("OK")) {
                    Log.e("ReverseGeocoding", "API Error: " + status);
                    return null;
                }

                JSONArray results = jsonObject.getJSONArray("results");
                if (results.length() > 0) {
                    JSONObject result = results.getJSONObject(0);
                    String formattedAddress = result.getString("formatted_address");
                    String postcode = "";
                    JSONArray addressComponents = result.getJSONArray("address_components");
                    for (int i = 0; i < addressComponents.length(); i++) {
                        JSONObject component = addressComponents.getJSONObject(i);
                        JSONArray types = component.getJSONArray("types");
                        for (int j = 0; j < types.length(); j++) {
                            if (types.getString(j).equals("postal_code")) {
                                postcode = component.getString("long_name");
                                break;
                            }
                        }
                    }
                    return new String[]{formattedAddress, postcode.isEmpty() ? "No postcode" : postcode};
                }
            } catch (Exception e) {
                Log.e("ReverseGeocoding", "Error fetching address", e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {
            if (result != null) {
                currentPickupAddress = result[0];
                String postcode = result[1];
                // Update the pickupLocationInput in BookingFragment
                AutoCompleteTextView pickupInput = fragment.getPickupLocationInput();
                if (pickupInput != null) {
                    pickupInput.setText(currentPickupAddress + " - " + postcode);
                } else {
                    Log.e("ReverseGeocoding", "pickupLocationInput is null");
                }
            } else {
                currentPickupAddress = "Current Location";
                AutoCompleteTextView pickupInput = fragment.getPickupLocationInput();
                if (pickupInput != null) {
                    pickupInput.setText(currentPickupAddress + " - No postcode");
                }
                Log.w("ReverseGeocoding", "Failed to fetch address, using fallback");
            }
        }
    }

    private class FetchLocationTask extends AsyncTask<String, Void, LatLng> {
        private final boolean isPickup;
        private final OnLocationFetchedListener listener;

        public FetchLocationTask(boolean isPickup, OnLocationFetchedListener listener) {
            this.isPickup = isPickup;
            this.listener = listener;
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
                    updatePickupMarker(latLng);
                    currentPickupLocation = latLng;
                } else {
                    if (destinationMarker != null) destinationMarker.remove();
                    destinationMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("Destination"));
                }
                listener.onLocationFetched(latLng, isPickup);
            } else {
//                customToast.showCustomErrorToast("Could not fetch location");
                listener.onLocationFetchFailed();
            }
        }
    }

    public void drawRoute(LatLng origin, LatLng destination, OnRouteDrawnListener listener) {
        String apiKey = getGoogleMapsApiKey();
        String url = "https://maps.googleapis.com/maps/api/directions/json?" +
                "origin=" + origin.latitude + "," + origin.longitude +
                "&destination=" + destination.latitude + "," + destination.longitude +
                "&key=" + apiKey;
        new FetchRoute(listener).execute(url);
    }

    private class FetchRoute extends AsyncTask<String, Void, List<LatLng>> {
        private double distanceInMeters = 0.0;
        private final OnRouteDrawnListener listener;

        public FetchRoute(OnRouteDrawnListener listener) {
            this.listener = listener;
        }

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

                    JSONArray legs = route.getJSONArray("legs");
                    if (legs.length() > 0) {
                        JSONObject leg = legs.getJSONObject(0);
                        JSONObject distance = leg.getJSONObject("distance");
                        distanceInMeters = distance.getDouble("value");
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
                customToast.showCustomErrorToast("Could not fetch route");
                listener.onRouteDrawFailed();
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

            double routeDistance = distanceInMeters / 1000;
            listener.onRouteDrawn(routeDistance);
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
            return fragment.requireContext().getPackageManager()
                    .getApplicationInfo(fragment.requireContext().getPackageName(),
                            PackageManager.GET_META_DATA)
                    .metaData.getString("com.google.android.geo.API_KEY");
        } catch (Exception e) {
            Log.e("GoogleMaps", "Error retrieving API key", e);
            return "";
        }
    }

    public void resetMap() {
        if (destinationMarker != null) {
            destinationMarker.remove();
            destinationMarker = null;
        }
        if (routePolyline != null) {
            routePolyline.remove();
            routePolyline = null;
        }
        if (pickupMarker != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPickupLocation, 15));
        } else {
            LatLng defaultLocation = new LatLng(51.5074, -0.1278);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12));
        }
    }

    public LatLng getCurrentPickupLocation() {
        return currentPickupLocation;
    }

    public String getCurrentPickupAddress() {
        return currentPickupAddress;
    }

    public Marker getPickupMarker() {
        return pickupMarker;
    }

    public Marker getDestinationMarker() {
        return destinationMarker;
    }

    public interface OnLocationFetchedListener {
        void onLocationFetched(LatLng latLng, boolean isPickup);
        void onLocationFetchFailed();
    }

    public interface OnRouteDrawnListener {
        void onRouteDrawn(double routeDistance);
        void onRouteDrawFailed();
    }
}