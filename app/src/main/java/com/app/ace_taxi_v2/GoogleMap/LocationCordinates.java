package com.app.ace_taxi_v2.GoogleMap;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationCordinates {

    private final Context context;

    public LocationCordinates(Context context) {
        this.context = context;
    }

    public LatLng getCoordinatesFromAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            Toast.makeText(context, "Invalid address", Toast.LENGTH_SHORT).show();
            return null;
        }

        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(address, 5);
            if (addresses != null && !addresses.isEmpty()) {
                Address location = addresses.get(0);
                Log.d("Geocoder", "Resolved Address: " + location.getAddressLine(0));
                return new LatLng(location.getLatitude(), location.getLongitude());
            } else {
                Log.e("Geocoder", "No address found for: " + address);
                return null;
            }
        } catch (IOException e) {
            Log.e("Geocoder", "Geocoding error", e);
            return null;
        }
    }
}
