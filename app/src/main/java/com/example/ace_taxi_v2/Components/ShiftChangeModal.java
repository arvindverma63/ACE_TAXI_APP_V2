package com.example.ace_taxi_v2.Components;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.example.ace_taxi_v2.Fragments.BookingFragment;
import com.example.ace_taxi_v2.Logic.UpdateDriverShiftApi;
import com.example.ace_taxi_v2.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;

public class ShiftChangeModal {
    private final Context context;
    private final FragmentManager fragmentManager; // Pass FragmentManager to avoid errors
    private BottomSheetDialog bottomSheetDialog;

    public ShiftChangeModal(Context context, FragmentManager fragmentManager) {
        this.context = context;
        this.fragmentManager = fragmentManager;
    }

    public void openModal() {
        // Initialize BottomSheetDialog
        bottomSheetDialog = new BottomSheetDialog(context);
        View bottomSheetView = LayoutInflater.from(context).inflate(R.layout.update_shift_status, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        UpdateDriverShiftApi updateDriverShiftApi = new UpdateDriverShiftApi(context);

        // Get button references
        MaterialButton startShift = bottomSheetView.findViewById(R.id.btn_start_shift);
        MaterialButton finishShift = bottomSheetView.findViewById(R.id.btn_finish);
        MaterialButton startBreak = bottomSheetView.findViewById(R.id.btn_on_break);
        MaterialButton finishBreak = bottomSheetView.findViewById(R.id.btn_finish_break);
        MaterialButton rankMap = bottomSheetView.findViewById(R.id.btn_rank_up);

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

        bottomSheetDialog.show();
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
        if (bottomSheetDialog != null && bottomSheetDialog.isShowing()) {
            bottomSheetDialog.dismiss();
        }
    }
}
