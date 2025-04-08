package com.app.ace_taxi_v2.Components;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class ShiftChangeModal {
    private final Context context;
    private final FragmentManager fragmentManager;
    private androidx.appcompat.app.AlertDialog alertDialog;
    public MaterialButton startShift, finishShift, startBreak, finishBreak, rankMap;
    public MaterialCardView start_shift_card, finish_shift_card, on_break_card, finish_break_card, rank_pickup_card;

    public ShiftChangeModal(Context context, FragmentManager fragmentManager) {
        this.context = context;
        this.fragmentManager = fragmentManager;
    }

    public void openModal() {
        // Inflate custom layout
        View dialogView = LayoutInflater.from(context).inflate(R.layout.update_shift_status, null);

        // Initialize Material AlertDialog
        alertDialog = new MaterialAlertDialogBuilder(context, R.style.CustomMaterialAlertDialog)
                .setView(dialogView)
                .setBackground(new ColorDrawable(android.graphics.Color.TRANSPARENT))
                .create();

        alertDialog.show();

        // Set dialog position at top-left corner
        Window window = alertDialog.getWindow();
        if (window != null) {
            window.setGravity(Gravity.TOP | Gravity.START);
            window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setWindowAnimations(R.style.DialogAnimation); // Apply custom animation
        }

        // Initialize UI elements
        UpdateDriverShiftApi updateDriverShiftApi = new UpdateDriverShiftApi(context);

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

        alertDialog.setCancelable(true);

        // Attach click listeners
        start_shift_card.setOnClickListener(v -> {
            updateDriverShiftApi.updateStatus(1000);
            updateStatus();
            dismissModal();
        });

        finish_shift_card.setOnClickListener(v -> {
            updateDriverShiftApi.updateStatus(1001);
            updateStatus();
            dismissModal();
        });

        on_break_card.setOnClickListener(v -> {
            updateDriverShiftApi.updateStatus(1002);
            updateStatus();
            dismissModal();
        });

        finish_break_card.setOnClickListener(v -> {
            updateDriverShiftApi.updateStatus(1003);
            updateStatus();
            dismissModal();
        });

        rank_pickup_card.setOnClickListener(v -> {
            openBookingFragment();
            dismissModal();
        });

        // Ensure UI reflects current status
        updateStatus();
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
                start_shift_card.setStrokeColor(ContextCompat.getColorStateList(context, R.color.start_shift));
                start_shift_card.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.light_blue));
                break;
            case "onBreak":
                on_break_card.setStrokeColor(ContextCompat.getColorStateList(context, R.color.on_break));
                on_break_card.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.light_blue));
                break;
            case "onFinish":
                finish_shift_card.setStrokeColor(ContextCompat.getColorStateList(context, R.color.finish_shift));
                finish_shift_card.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.light_gray));
                break;
            case "onBreakFinish":
                finish_break_card.setStrokeColor(ContextCompat.getColorStateList(context, R.color.finish_break));
                finish_break_card.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.green_light));
                break;
            default:
                rank_pickup_card.setStrokeColor(ContextCompat.getColorStateList(context, R.color.darkCard));
                break;
        }

    }
}
