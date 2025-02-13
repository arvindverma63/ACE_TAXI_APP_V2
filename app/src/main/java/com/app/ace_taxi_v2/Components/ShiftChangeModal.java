package com.app.ace_taxi_v2.Components;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.app.ace_taxi_v2.Fragments.BookingFragment;
import com.app.ace_taxi_v2.Logic.Service.CurrentShiftStatus;
import com.app.ace_taxi_v2.Logic.UpdateDriverShiftApi;
import com.app.ace_taxi_v2.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

public class ShiftChangeModal {
    private final Context context;
    private final FragmentManager fragmentManager;
    private AlertDialog alertDialog;
    public MaterialButton startShift, finishShift, startBreak, finishBreak, rankMap;
    public MaterialCardView start_shift_card, finish_shift_card, on_break_card, finish_break_card, rank_pickup_card;

    public ShiftChangeModal(Context context, FragmentManager fragmentManager) {
        this.context = context;
        this.fragmentManager = fragmentManager;
    }

    public void openModal() {
        // Initialize AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.RightSlideDialog);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.update_shift_status, null);
        builder.setView(dialogView);
        alertDialog = builder.create();

        // Set window properties
        if (alertDialog.getWindow() != null) {
            Window window = alertDialog.getWindow();
            window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.MATCH_PARENT);
            window.setGravity(Gravity.END); // Opens from the right
            window.setBackgroundDrawableResource(android.R.color.transparent);
            window.setDimAmount(0.5f);
            window.setWindowAnimations(R.style.RightSlideDialogAnimation); // Apply flip animation
        }

        UpdateDriverShiftApi updateDriverShiftApi = new UpdateDriverShiftApi(context);

        // Get button references
        startShift = dialogView.findViewById(R.id.btn_start_shift);
        finishShift = dialogView.findViewById(R.id.btn_finish);
        startBreak = dialogView.findViewById(R.id.btn_on_break);
        finishBreak = dialogView.findViewById(R.id.btn_finish_break);
        rankMap = dialogView.findViewById(R.id.btn_rank_up);
        start_shift_card = dialogView.findViewById(R.id.start_shift_card);
        finish_shift_card = dialogView.findViewById(R.id.finish_shift_card);
        on_break_card = dialogView.findViewById(R.id.on_break_card);
        finish_break_card = dialogView.findViewById(R.id.finish_break_card);
        rank_pickup_card = dialogView.findViewById(R.id.rank_pickup_card);
        TextView closeBtn = dialogView.findViewById(R.id.close_shift_dialog);

        // Attach click listeners
        startShift.setOnClickListener(v -> {
            updateDriverShiftApi.updateStatus(1000);
            updateStatus();
            dismissModal();
        });

        finishShift.setOnClickListener(v -> {
            updateDriverShiftApi.updateStatus(1001);
            updateStatus();
            dismissModal();
        });

        startBreak.setOnClickListener(v -> {
            updateDriverShiftApi.updateStatus(1002);
            updateStatus();
            dismissModal();
        });

        finishBreak.setOnClickListener(v -> {
            updateDriverShiftApi.updateStatus(1003);
            updateStatus();
            dismissModal();
        });

        rankMap.setOnClickListener(v -> {
            openBookingFragment();
            dismissModal();
        });

        // Ensure UI reflects current status
        updateStatus();

        alertDialog.show();

        closeBtn.setOnClickListener(v -> {
            dismissModal();
        });
    }

    // Open BookingFragment safely
    private void openBookingFragment() {
        if (fragmentManager != null) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragment_container, new BookingFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        } else {
            Log.e("ShiftChangeModal", "FragmentManager is null. Cannot open BookingFragment.");
        }
    }

    // Dismiss modal safely to avoid memory leaks
    private void dismissModal() {
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
    }

    // Update button colors based on current shift status
    public void updateStatus() {
        CurrentShiftStatus currentShiftStatus = new CurrentShiftStatus(context);
        String current_status = currentShiftStatus.getStatus();

        if (current_status == null) {
            Log.w("ShiftChangeModal", "No shift status found.");
            return;
        }

        switch (current_status) {
            case "onShift":
                start_shift_card.setStrokeColor(ContextCompat.getColorStateList(context, R.color.red));
                break;
            case "onBreak":
                on_break_card.setStrokeColor(ContextCompat.getColorStateList(context, R.color.green));
                break;
            case "onFinish":
                finish_shift_card.setStrokeColor(ContextCompat.getColorStateList(context, R.color.blue));
                break;
            case "onBreakFinish":
                finish_break_card.setStrokeColor(ContextCompat.getColorStateList(context, R.color.orange));
                break;
            default:
                rank_pickup_card.setStrokeColor(ContextCompat.getColorStateList(context, R.color.darkCard));
                break;
        }
    }
}
