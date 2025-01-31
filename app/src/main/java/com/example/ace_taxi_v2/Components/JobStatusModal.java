package com.example.ace_taxi_v2.Components;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.fragment.app.FragmentManager;
import com.example.ace_taxi_v2.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class JobStatusModal {
    private final Context context;
    private final FragmentManager fragmentManager; // Pass FragmentManager to avoid errors
    private BottomSheetDialog bottomSheetDialog;

    public JobStatusModal(Context context, FragmentManager fragmentManager) {
        this.context = context;
        this.fragmentManager = fragmentManager;
    }

    public void openModal() {
        // Initialize BottomSheetDialog
        bottomSheetDialog = new BottomSheetDialog(context);
        View bottomSheetView = LayoutInflater.from(context).inflate(R.layout.today_job_status, null);
        bottomSheetDialog.setContentView(bottomSheetView);


        bottomSheetDialog.show();
    }
    // Dismiss modal safely to avoid memory leaks
    private void dismissModal() {
        if (bottomSheetDialog != null && bottomSheetDialog.isShowing()) {
            bottomSheetDialog.dismiss();
        }
    }
}
