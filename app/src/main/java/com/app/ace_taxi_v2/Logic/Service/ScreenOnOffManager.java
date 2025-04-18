package com.app.ace_taxi_v2.Logic.Service;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class ScreenOnOffManager {
    private static final String PREF_NAME = "ScreenOnPrefs";
    private static final String KEY_SCREEN_ON = "isScreenOnEnabled";
    private final Context context;
    private final Window window;
    private final SharedPreferences prefs;

    public ScreenOnOffManager(Context context, Window window) {
        this.context = context;
        this.window = window;
        this.prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void enableScreenOn() {
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        saveScreenOnState(true);
    }

    public void disableScreenOn() {
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        saveScreenOnState(false);
    }

    public void toggleScreenOn() {
        if (isScreenOnEnabled()) {
            disableScreenOn();
        } else {
            enableScreenOn();
        }
    }

    public boolean isScreenOnEnabled() {
        return prefs.getBoolean(KEY_SCREEN_ON, false);
    }

    private void saveScreenOnState(boolean isEnabled) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_SCREEN_ON, isEnabled);
        editor.apply();
    }

    public void applyScreenOnBasedOnView(View view) {
        if (view == null) {
            // Global control: Apply based on SharedPreferences only
            if (isScreenOnEnabled()) {
                enableScreenOn();
            } else {
                disableScreenOn();
            }
        } else {
            // View-based control: Apply based on view visibility and SharedPreferences
            if (view.getVisibility() == View.VISIBLE && isScreenOnEnabled()) {
                enableScreenOn();
            } else {
                disableScreenOn();
            }
        }
    }
}