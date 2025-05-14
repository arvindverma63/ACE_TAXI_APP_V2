package com.app.ace_taxi_v2.Components;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import com.app.ace_taxi_v2.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import androidx.appcompat.app.AlertDialog;

public class CustomDialog {

    private AlertDialog progressDialog;

    public void showProgressDialog(Context context) {
        // Ensure context is an Activity and is valid
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            if (activity.isFinishing() || activity.isDestroyed()) {
                return;
            }
        }

        // Prevent multiple dialogs
        if (progressDialog != null && progressDialog.isShowing()) {
            return;
        }

        // Inflate the custom layout
        View dialogView = LayoutInflater.from(context).inflate(R.layout.custom_progress_dialog, null);

        // Build the dialog
        progressDialog = new MaterialAlertDialogBuilder(context)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        // Set transparent background
        if (progressDialog.getWindow() != null) {
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        // Show the dialog
        progressDialog.show();
    }

    public void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }
}
