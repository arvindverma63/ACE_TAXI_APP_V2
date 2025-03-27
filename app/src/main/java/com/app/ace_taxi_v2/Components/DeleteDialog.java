package com.app.ace_taxi_v2.Components;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.app.ace_taxi_v2.Logic.JobResponseApi;
import com.app.ace_taxi_v2.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;


public class DeleteDialog {
    public Context context;

    public DeleteDialog(Context context){
        this.context = context;
    }

    public void rejectBooking(int bookingId){
        View view = LayoutInflater.from(context).inflate(R.layout.delete_confirm_dialog,null);
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(context);
        alertDialogBuilder.setView(view);
        TextView messageTitle,messageBody;

        AlertDialog alertDialog = alertDialogBuilder.create();

        messageTitle = view.findViewById(R.id.delete_title);
        messageBody = view.findViewById(R.id.delete_message);

        messageTitle.setText("Reject Booking ?");
        messageBody.setText("Are you sure you want to reject this booking? ");

        MaterialButton cancelButton = view.findViewById(R.id.cancel_button);
        MaterialButton deleteButton = view.findViewById(R.id.delete_button);
        deleteButton.setText("Reject");

        alertDialog.show();
        cancelButton.setOnClickListener(v -> {
            alertDialog.dismiss();
            // Item remains in place
        });

        deleteButton.setOnClickListener(v -> {
            JobResponseApi jobResponseApi = new JobResponseApi(context);
            jobResponseApi.rejectBooking(bookingId);
            alertDialog.dismiss();
        });
    }
}
