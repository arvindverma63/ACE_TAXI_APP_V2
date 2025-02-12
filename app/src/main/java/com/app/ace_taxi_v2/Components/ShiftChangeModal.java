package com.app.ace_taxi_v2.Components;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.app.ace_taxi_v2.Fragments.BookingFragment;
import com.app.ace_taxi_v2.Logic.UpdateDriverShiftApi;
import com.app.ace_taxi_v2.R;
import com.google.android.material.button.MaterialButton;

public class ShiftChangeModal {
    private final Context context;
    private final FragmentManager fragmentManager; // Pass FragmentManager to avoid errors
    private Dialog topSheetDialog;

    public ShiftChangeModal(Context context, FragmentManager fragmentManager) {
        this.context = context;
        this.fragmentManager = fragmentManager;
    }

    public void openModal() {
        // Initialize Dialog (Instead of BottomSheetDialog)
        topSheetDialog = new Dialog(context, R.style.TopSheetDialogTheme);
        View topSheetView = LayoutInflater.from(context).inflate(R.layout.update_shift_status, null);
        topSheetDialog.setContentView(topSheetView);

        // Set window properties
        Window window = topSheetDialog.getWindow();
        if (window != null) {
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.TOP);
            window.setBackgroundDrawableResource(android.R.color.transparent);
            window.setDimAmount(0.5f); // Slight dim effect behind the dialog
            window.setWindowAnimations(R.style.TopSheetDialogAnimation); // Apply custom animation

            // Ensure it appears at the very top
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.y = 0; // Set Y position to ensure it's at the top
            window.setAttributes(layoutParams);
        }

        UpdateDriverShiftApi updateDriverShiftApi = new UpdateDriverShiftApi(context);

        // Get button references
        MaterialButton startShift = topSheetView.findViewById(R.id.btn_start_shift);
        MaterialButton finishShift = topSheetView.findViewById(R.id.btn_finish);
        MaterialButton startBreak = topSheetView.findViewById(R.id.btn_on_break);
        MaterialButton finishBreak = topSheetView.findViewById(R.id.btn_finish_break);
        MaterialButton rankMap = topSheetView.findViewById(R.id.btn_rank_up);

        // Button Click Listeners
        startShift.setOnClickListener(v -> {
            updateDriverShiftApi.updateStatus(1000);
            dismissModal();
        });

        finishShift.setOnClickListener(v -> {
            updateDriverShiftApi.updateStatus(1001);
            dismissModal();
        });

        startBreak.setOnClickListener(v -> {
            updateDriverShiftApi.updateStatus(1002);
            dismissModal();
        });

        finishBreak.setOnClickListener(v -> {
            updateDriverShiftApi.updateStatus(1003);
            dismissModal();
        });

        rankMap.setOnClickListener(v -> {
            openBookingFragment();
            dismissModal();
        });

        topSheetDialog.show();
    }

    // Open BookingFragment safely
    private void openBookingFragment() {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, new BookingFragment()); // Ensure fragment_container is defined in XML
        transaction.addToBackStack(null);
        transaction.commit();
    }

    // Dismiss modal safely to avoid memory leaks
    private void dismissModal() {
        if (topSheetDialog != null && topSheetDialog.isShowing()) {
            topSheetDialog.dismiss();
        }
    }
}
