package com.app.ace_taxi_v2.Logic.Service;

import android.content.Context;
import android.content.SharedPreferences;

public class LocationSessionManager {

    // SharedPreferences file name and keys
    private static final String PREF_NAME = "LocationSession";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";
    private static final String KEY_SPEED = "speed";
    private static final String KEY_HEADING = "heading";

    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;

    public LocationSessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    /**
     * Save location data.
     */
    public void saveLocation(double latitude, double longitude, float speed, float heading) {
        editor.putLong(KEY_LATITUDE, Double.doubleToRawLongBits(latitude));
        editor.putLong(KEY_LONGITUDE, Double.doubleToRawLongBits(longitude));
        editor.putFloat(KEY_SPEED, speed);
        editor.putFloat(KEY_HEADING, heading);
        editor.apply();
    }

    /**
     * Get saved latitude.
     */
    public double getLatitude() {
        return Double.longBitsToDouble(sharedPreferences.getLong(KEY_LATITUDE, Double.doubleToRawLongBits(0.0)));
    }

    /**
     * Get saved longitude.
     */
    public double getLongitude() {
        return Double.longBitsToDouble(sharedPreferences.getLong(KEY_LONGITUDE, Double.doubleToRawLongBits(0.0)));
    }

    /**
     * Get saved speed.
     */
    public float getSpeed() {
        return sharedPreferences.getFloat(KEY_SPEED, 0.0f);
    }

    /**
     * Get saved heading.
     */
    public float getHeading() {
        return sharedPreferences.getFloat(KEY_HEADING, 0.0f);
    }

    /**
     * Clear saved location data.
     */
    public void clearLocationData() {
        editor.clear();
        editor.apply();
    }
}
