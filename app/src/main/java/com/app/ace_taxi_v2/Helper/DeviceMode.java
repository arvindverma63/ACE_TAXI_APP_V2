package com.app.ace_taxi_v2.Helper;

import android.content.Context;
import android.content.SharedPreferences;

public class DeviceMode {
    private static final String PREF_NAME = "DeviceModePreferences";
    private static final String KEY_API_BASE_URL = "api_base_url";

    // URLs
    private static final String DEV_URL = "https://dev.ace-api.1soft.co.uk/";
    private static final String LIVE_URL = "https://ace-server.1soft.co.uk/";

    private static DeviceMode instance;
    private static Context appContext;
    private SharedPreferences sharedPreferences;

    private DeviceMode(Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        // Set LIVE as default only if not already set
        if (!sharedPreferences.contains(KEY_API_BASE_URL)) {
            setBaseURL(true); // true = LIVE
        }
    }

    public static void init(Context context) {
        if (instance == null) {
            appContext = context.getApplicationContext();
            instance = new DeviceMode(appContext);
        }
    }

    public static DeviceMode getInstance() {
        if (instance == null) {
            throw new IllegalStateException("DeviceMode not initialized. Call init(context) first.");
        }
        return instance;
    }

    public void setBaseURL(boolean isLive) {
        String url = isLive ? LIVE_URL : DEV_URL;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_API_BASE_URL, url);
        editor.apply();
    }

    public String getBaseURL() {
        // Default to LIVE_URL if no value is stored
        return sharedPreferences.getString(KEY_API_BASE_URL, LIVE_URL);
    }

    public void clearBaseURL() {
        sharedPreferences.edit().remove(KEY_API_BASE_URL).apply();
    }

    public boolean isLiveMode() {
        return LIVE_URL.equals(getBaseURL());
    }

    public boolean isDevMode() {
        return DEV_URL.equals(getBaseURL());
    }

    public static String getBaseURLStatic() {
        return getInstance().getBaseURL();
    }
}
