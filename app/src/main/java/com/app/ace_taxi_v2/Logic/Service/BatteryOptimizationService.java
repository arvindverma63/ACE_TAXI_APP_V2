package com.app.ace_taxi_v2.Logic.Service;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

public class BatteryOptimizationService extends AccessibilityService {
    private static final String TAG = "BatteryOptService";
    private static final long CLICK_DELAY = 500; // milliseconds
    private static final String APP_NAME = "Ace Taxi"; // Replace with your app's visible name

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            disableBatteryOptimization();
        }
    }

    @Override
    public void onInterrupt() {
        Log.i(TAG, "Service interrupted");
    }

    private void disableBatteryOptimization() {
        AccessibilityNodeInfo root = getRootInActiveWindow();
        if (root == null) {
            Log.w(TAG, "Root node is null");
            return;
        }

        try {
            // Step 1: Click "Battery optimization"
            AccessibilityNodeInfo batteryOptNode = findNodeWithText("Battery optimization");
            if (batteryOptNode != null && performClick(batteryOptNode)) {
                Thread.sleep(CLICK_DELAY);

                // Step 2: Click "All apps" if present
                AccessibilityNodeInfo allAppsNode = findNodeWithText("All apps");
                if (allAppsNode != null && performClick(allAppsNode)) {
                    Thread.sleep(CLICK_DELAY);

                    // Step 3: Find and select your app
                    AccessibilityNodeInfo appNode = findNodeWithText(APP_NAME);
                    if (appNode != null && performClick(appNode)) {
                        Thread.sleep(CLICK_DELAY);

                        // Step 4: Click "Don't optimize"
                        AccessibilityNodeInfo dontOptimizeNode = findNodeWithText("Don't optimize");
                        if (dontOptimizeNode != null && performClick(dontOptimizeNode)) {
                            Thread.sleep(CLICK_DELAY);

                            // Step 5: Click OK/Confirm if present
                            AccessibilityNodeInfo okNode = findNodeWithText("OK");
                            if (okNode != null) {
                                performClick(okNode);
                            }
                        }
                    }
                }
            }
        } catch (InterruptedException e) {
            Log.w(TAG, "Navigation interrupted: " + e.getMessage());
        } finally {
            root.recycle();
        }
    }

    private AccessibilityNodeInfo findNodeWithText(String text) {
        AccessibilityNodeInfo root = getRootInActiveWindow();
        if (root == null) return null;

        List<AccessibilityNodeInfo> nodes = root.findAccessibilityNodeInfosByText(text);
        if (!nodes.isEmpty()) {
            AccessibilityNodeInfo target = nodes.get(0);
            // Recycle all other nodes
            for (int i = 1; i < nodes.size(); i++) {
                nodes.get(i).recycle();
            }
            return target;
        }
        root.recycle();
        return null;
    }

    private boolean performClick(AccessibilityNodeInfo node) {
        if (node == null || !node.isClickable()) return false;
        boolean success = node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        Log.i(TAG, "Click on " + node.getText() + ": " + success);
        node.recycle();
        return success;
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.i(TAG, "Service connected");
    }
}