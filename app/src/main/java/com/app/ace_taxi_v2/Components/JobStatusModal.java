package com.app.ace_taxi_v2.Components;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.app.ace_taxi_v2.Fragments.Adapters.JobAdapters.TodayJobAdapter;
import com.app.ace_taxi_v2.JobModals.JobModal;
import com.app.ace_taxi_v2.Logic.JobStatusReply;
import com.app.ace_taxi_v2.Logic.Service.CurrentBookingSession;
import com.app.ace_taxi_v2.R;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class JobStatusModal {
    private final Context context;
    private AlertDialog alertDialog;
    private int bookingId;
    private JobStatusReply jobStatusReply;
    private CurrentBookingSession currentBookingSession;

    private MaterialCardView btnOnRoute;
    private MaterialCardView btnPickUp;
    private MaterialCardView btnPob;
    private MaterialCardView btnStc;
    private MaterialCardView btnClear;
    private MaterialCardView btnReset;

    public JobStatusModal(Context context) {
        this.context = context;
        this.currentBookingSession = new CurrentBookingSession(context);
    }

    public void openModal(int jobno) {
        // Inflate the layout for modal
        View dialogView = LayoutInflater.from(context).inflate(R.layout.today_job_status, null);

        // Initialize AlertDialog
        alertDialog = new MaterialAlertDialogBuilder(context, R.style.CustomMaterialAlertDialog)
                .setView(dialogView)
                .setBackground(new ColorDrawable(android.graphics.Color.TRANSPARENT))
                .create();
        alertDialog.show();

        bookingId = jobno;
        jobStatusReply = new JobStatusReply(context);

        // Find buttons safely
        btnOnRoute = dialogView.findViewById(R.id.btn_on_route);
        btnPickUp = dialogView.findViewById(R.id.btn_pick_up);
        btnPob = dialogView.findViewById(R.id.btn_pob);
        btnStc = dialogView.findViewById(R.id.btn_stc);
        btnClear = dialogView.findViewById(R.id.btn_clear);
        btnReset = dialogView.findViewById(R.id.btn_reset);

        // Null-check before setting listeners
        if (btnOnRoute != null) btnOnRoute.setOnClickListener(v -> updateJobStatus(3003));
        if (btnPickUp != null) btnPickUp.setOnClickListener(v -> updateJobStatus(3004));
        if (btnPob != null) btnPob.setOnClickListener(v -> updateJobStatus(3005));
        if (btnStc != null) btnStc.setOnClickListener(v -> updateJobStatus(3006));
        if (btnClear != null) btnClear.setOnClickListener(v -> jobClear());
        if (btnReset != null) btnReset.setOnClickListener(v -> updateJobStatus(3008));

        // Update UI based on current status
        updateStatus();
    }

    // Method to update job status
    private void updateJobStatus(int statusCode) {
        if (jobStatusReply != null) {
            jobStatusReply.updateStatus(bookingId, statusCode);
            alertDialog.dismiss();
        }
    }

    // Method to complete job
    private void jobClear() {
        JobModal jobModal = new JobModal(context);
        jobModal.jobCompleteBooking(bookingId);
    }

    // Dismiss modal safely
    public void dismissModal() {
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
            alertDialog = null; // Prevent memory leaks
        }
    }

    public void updateStatus() {
        String bookingId = currentBookingSession.getBookingId();
        String bookingShift = currentBookingSession.getBookingShift();

        if (bookingId == null || bookingShift == null) {
            return; // Exit if there's no valid booking info
        }

        switch (bookingShift) {
            case "3003":
                if (btnOnRoute != null)
                    btnOnRoute.setStrokeColor(ContextCompat.getColor(context, R.color.red));
                    btnOnRoute.setBackgroundTintList(ContextCompat.getColorStateList(context,R.color.light_red));
                break;
            case "3004":
                if (btnPickUp != null)
                    btnPickUp.setStrokeColor(ContextCompat.getColor(context, R.color.blue));
                    btnPickUp.setBackgroundTintList(ContextCompat.getColorStateList(context,R.color.light_blue));
                break;
            case "3005":
                if (btnPob != null)
                    btnPob.setStrokeColor(ContextCompat.getColor(context, R.color.green));
                    btnPob.setBackgroundTintList(ContextCompat.getColorStateList(context,R.color.green_light));
                break;
            case "3006":
                if (btnStc != null)
                    btnStc.setStrokeColor(ContextCompat.getColor(context, R.color.red));
                    btnStc.setBackgroundTintList(ContextCompat.getColorStateList(context,R.color.light_orange));
                break;
            case "3007":
                if (btnClear != null)
                    btnClear.setStrokeColor(ContextCompat.getColor(context, R.color.gray));
                    btnClear.setBackgroundTintList(ContextCompat.getColorStateList(context,R.color.green_light));
                break;
            case "3008":
                if (btnReset != null) {
                    btnReset.setStrokeColor(ContextCompat.getColor(context, R.color.black));
                    currentBookingSession.clearBookingData();
                } else {
                    currentBookingSession.clearBookingData();
                }
                break;
            default:
                // Optional: Log unknown status for debugging
                break;
        }
    }

}
