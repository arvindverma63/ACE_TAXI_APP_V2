package com.app.ace_taxi_v2.JobModals;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.widget.Button;

import androidx.core.content.ContextCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.app.ace_taxi_v2.R; // Make sure this import matches your actual package

public class PriceZeroDialog {
    private final Context context;

    public PriceZeroDialog(Context context) {
        this.context = context;
    }

    public void openDialog() {
        androidx.appcompat.app.AlertDialog dialog = new MaterialAlertDialogBuilder(context)
                .setTitle("Booking Price Alert")
                .setMessage("The booking price is £0.00 .\nPlease call the dispatcher to update the price.")
                .setCancelable(false)
                .setPositiveButton("OK", (dialogInterface, which) -> dialogInterface.dismiss())
                .create();

        dialog.setOnShowListener(d -> {
            Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            if (positiveButton != null) {
                positiveButton.setTextColor(ContextCompat.getColor(context, R.color.red)); // Ensure R.color.red exists
            }
        });

        dialog.show();
    }
}
