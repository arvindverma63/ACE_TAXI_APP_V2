package com.app.ace_taxi_v2.GoogleMap;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

public class StringtoMap {
    public Context context;
    public StringtoMap(Context context){
        this.context = context;
    }

    public void openGoogleMaps(String address) {
        if (context == null) return;
        if (address == null || address.trim().isEmpty()) {
            Toast.makeText(context, "Invalid address", Toast.LENGTH_SHORT).show();
            return;
        }

        // Encode the address to handle spaces and special characters
        String encodedAddress = Uri.encode(address);
        // Create a geo URI for the address
        String geoUri = "geo:0,0?q=" + encodedAddress;

        // Create an Intent to open Google Maps
        Uri gmmIntentUri = Uri.parse(geoUri);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps"); // Restrict to Google Maps

        // Check if Google Maps is installed
        if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(mapIntent);
        } else {
            // Fallback: Open the address in a browser
            String mapsUrl = "https://www.google.com/maps/search/?api=1&query=" + encodedAddress;
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mapsUrl));
            context.startActivity(browserIntent);
        }
    }
}
