package com.app.ace_taxi_v2.Helper;

import android.accessibilityservice.AccessibilityServiceInfo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.accessibility.AccessibilityManager;

import com.app.ace_taxi_v2.MainActivity;

import java.util.List;

public class BatteryOptimizationHelper {
    private static final String TAG = "BatteryOptHelper";
    private static final String SERVICE_NAME = "com.app.ace_taxi_v2.Logic.Service.BatteryOptimizationService";
    private final Context context;
    private final MainActivity activity; // Assuming this is running from MainActivity

    public BatteryOptimizationHelper(Context context, MainActivity activity) {
        this.context = context;
        this.activity = activity;
    }

    public boolean checkBatteryOptimizations() {
        SharedPreferences prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE);
        boolean alreadyExempt = prefs.getBoolean("battery_optimization_exempt", false);
        if (alreadyExempt) return true;

        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && powerManager != null) {
            boolean isIgnoringOptimizations = powerManager.isIgnoringBatteryOptimizations(context.getPackageName());
            if (!isIgnoringOptimizations) {
                Log.d(TAG, "Attempting to disable battery optimization via accessibility service.");

                // Check if accessibility service is enabled
                if (!isAccessibilityServiceEnabled()) {
                    promptEnableAccessibilityService();
                    return false;
                }

                // Open battery settings and let the service handle it
                openBatterySettings();
                return false; // Return false until confirmed in a later check
            } else {
                prefs.edit().putBoolean("battery_optimization_exempt", true).apply();
            }
        }
        return true;
    }

    private boolean isAccessibilityServiceEnabled() {
        AccessibilityManager am = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
        List<AccessibilityServiceInfo> enabledServices = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK);

        for (AccessibilityServiceInfo service : enabledServices) {
            if (SERVICE_NAME.equals(service.getId())) {
                return true;
            }
        }
        return false;
    }

    private void promptEnableAccessibilityService() {
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        activity.startActivity(intent);
        Log.i(TAG, "Prompting user to enable accessibility service");
        // You might want to show a dialog explaining why this is needed
    }

    private void openBatterySettings() {
        Intent intent = new Intent(Settings.ACTION_BATTERY_SAVER_SETTINGS);
        try {
            activity.startActivity(intent);
            Log.i(TAG, "Opened battery settings for accessibility service to handle");
        } catch (Exception e) {
            Log.w(TAG, "Failed to open battery saver settings, falling back to general settings");
            intent = new Intent(Settings.ACTION_SETTINGS);
            activity.startActivity(intent);
        }
    }
}