package com.app.ace_taxi_v2.SettingsPermission;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

public class Permission {
    private static final String TAG = "Permission";
    private final Context context;

    // Constructor
    public Permission(Context context) {
        this.context = context;
    }

    /**
     * Open location settings.
     * @param status If true, opens location source settings.
     */
    public void locationPermission(boolean status) {
        try {
            Intent intent = new Intent(
                    status ? Settings.ACTION_LOCATION_SOURCE_SETTINGS
                            : Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            );
            if (!status) {
                intent.setData(android.net.Uri.fromParts("package", context.getPackageName(), null));
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Failed to open location settings: " + e.getMessage());
            Toast.makeText(context, "Unable to open location settings.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Open SMS permissions/settings.
     * @param status If true, opens manage default apps settings.
     */
    public void smsPermission(boolean status) {
        try {
            Intent intent = new Intent(
                    status ? Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS
                            : Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            );
            if (!status) {
                intent.setData(android.net.Uri.fromParts("package", context.getPackageName(), null));
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Failed to open SMS settings: " + e.getMessage());
            Toast.makeText(context, "Unable to open SMS settings.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Open notification permissions/settings.
     * @param status If true, opens app notification settings.
     */
    public void notificationPermission(boolean status) {
        try {
            Intent intent;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                        .putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
            } else {
                intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        .setData(android.net.Uri.fromParts("package", context.getPackageName(), null));
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Failed to open notification settings: " + e.getMessage());
            Toast.makeText(context, "Unable to open notification settings.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Open phone permissions/settings.
     * @param status If true, opens manage all applications settings.
     */
    public void phonePermission(boolean status) {
        try {
            Intent intent = new Intent(
                    status ? Settings.ACTION_MANAGE_ALL_APPLICATIONS_SETTINGS
                            : Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            );
            if (!status) {
                intent.setData(android.net.Uri.fromParts("package", context.getPackageName(), null));
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Failed to open phone settings: " + e.getMessage());
            Toast.makeText(context, "Unable to open phone settings.", Toast.LENGTH_SHORT).show();
        }
    }
}
