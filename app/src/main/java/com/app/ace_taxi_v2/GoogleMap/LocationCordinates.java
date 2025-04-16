package com.app.ace_taxi_v2.GoogleMap;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationCordinates {

    public Context context;
    public LocationCordinates(Context context){
        this.context = context;
    }


    public LatLng getCoordinatesFromAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            Toast.makeText(context, "Invalid address", Toast.LENGTH_SHORT).show();
            return null;
        }

        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(address, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address location = addresses.get(0);
                return new LatLng(location.getLatitude(), location.getLongitude());
            } else {
                Toast.makeText(context, "Address not found", Toast.LENGTH_SHORT).show();
                return null;
            }
        } catch (IOException e) {
            Toast.makeText(context, "Geocoding error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return null;
        }
    }
}
