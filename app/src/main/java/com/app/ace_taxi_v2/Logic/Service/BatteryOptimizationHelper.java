package com.app.ace_taxi_v2.Logic.Service;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;

public class BatteryOptimizationHelper {

    private static final String TAG = "BatteryOptimizationHelper";
    private final Activity activity;

    public BatteryOptimizationHelper(Activity activity) {
        this.activity = activity;
    }

    // Check if battery optimization is already disabled
    public boolean isBatteryOptimizationIgnored() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PowerManager powerManager = (PowerManager) activity.getSystemService(Context.POWER_SERVICE);
            boolean isIgnored = powerManager.isIgnoringBatteryOptimizations(activity.getPackageName());
            Log.d(TAG, "Battery Optimization Ignored: " + isIgnored);
            return isIgnored;
        }
        return true;
    }

    // Show modal before opening settings
    public void showBatteryOptimizationDialog() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (isBatteryOptimizationIgnored()) {
                Log.d(TAG, "Battery optimization is already disabled. No need to request.");
                return;
            }

            new AlertDialog.Builder(activity)
                    .setTitle("Disable Battery Optimization")
                    .setMessage("To ensure background functionality, please disable battery optimization.")
                    .setPositiveButton("Allow", (dialog, which) -> requestBatteryOptimizationExemption())
                    .setNegativeButton("Cancel", null)
                    .show();
        }
    }

    // Open Battery Optimization Settings with Your Intent Flags
    private void requestBatteryOptimizationExemption() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                Intent intentSettings = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intentSettings.setData(Uri.fromParts("package", activity.getPackageName(), null));
                intentSettings.addCategory(Intent.CATEGORY_DEFAULT);
                intentSettings.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intentSettings.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intentSettings.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);

                Log.d(TAG, "Opening battery optimization settings for: " + activity.getPackageName());
                activity.startActivity(intentSettings);

            } catch (Exception e) {
                Log.e(TAG, "Failed to open battery optimization settings", e);
            }
        }
    }
}
