package com.example.ace_taxi_v2.Components;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;

import com.example.ace_taxi_v2.R;

public class CustomDialog {

    private Dialog progressDialog;

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

        progressDialog = new Dialog(context);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setCancelable(false); // Prevent dialog dismissal
        progressDialog.setContentView(R.layout.custom_progress_dialog);

        if (progressDialog.getWindow() != null) {
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        progressDialog.show();
    }

    public void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }
}
