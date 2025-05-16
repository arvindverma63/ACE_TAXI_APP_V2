package com.app.ace_taxi_v2.Helper;

import android.content.Context;
import android.content.SharedPreferences;

public class DeviceMode {
    private static final String PREF_NAME = "DeviceModePreferences";
    private static final String KEY_API_BASE_URL = "api_base_url";
    private static final String DEFAULT_URL = "https://dev.ace-api.1soft.co.uk/";
    private static final String LIVE_URL = "https://ace-server.1soft.co.uk/";
    private static DeviceMode instance;
    private static Context appContext;
    private SharedPreferences sharedPreferences;

    private DeviceMode(Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static void init(Context context) {
        if (instance == null) {
            appContext = context.getApplicationContext();
            instance = new DeviceMode(appContext);
        }
    }

    public static DeviceMode getInstance() {
        if (instance == null) {
            throw new IllegalStateException("DeviceMode is not initialized. Call DeviceMode.init(context) in your Application class.");
        }
        return instance;
    }

    public void setBaseURL(boolean isLive) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_API_BASE_URL, isLive ? LIVE_URL : DEFAULT_URL);
        editor.apply();
    }

    public String getBaseURL() {
        return sharedPreferences.getString(KEY_API_BASE_URL, DEFAULT_URL);
    }

    public void clearBaseURL() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_API_BASE_URL);
        editor.apply();
    }

    public boolean isLiveMode() {
        return LIVE_URL.equals(getBaseURL());
    }

    public static String getBaseURLStatic() {
        return getInstance().getBaseURL();
    }
}
