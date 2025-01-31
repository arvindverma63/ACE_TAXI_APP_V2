package com.example.ace_taxi_v2.JobModals;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.ace_taxi_v2.Activity.HomeActivity;
import com.example.ace_taxi_v2.Logic.JobResponseApi;
import com.example.ace_taxi_v2.R;

public class JobModal {
    public Context context;
    public JobModal(Context context){
        this.context = context;
    }

    public void jobDetails(){
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.job_offer,null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);

        builder.setCancelable(true);
        AlertDialog alertDialog = builder.create();
        // Set transparent background for the dialog window
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        // Apply rounded background programmatically
        dialogView.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_dialog));

        alertDialog.show();
        alertDialog.show();
    }

    public void viewJob(){
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.job_view,null);


        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);

        builder.setCancelable(true);
        AlertDialog alertDialog = builder.create();
        // Set transparent background for the dialog window
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        // Apply rounded background programmatically
        dialogView.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_dialog));

        alertDialog.show();
        alertDialog.show();
    }
    public void jobOfferModal(String pickupAddress, String destinationAddress, double price, String pickupDate, String passengerName,int bookingId){
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.job_offer,null);

        TextView pickup_address = dialogView.findViewById(R.id.pickup_address);
        TextView destination_address = dialogView.findViewById(R.id.destination_address);
        TextView fairy_price = dialogView.findViewById(R.id.price);
        TextView pickupDateAndTime = dialogView.findViewById(R.id.pickup_date);
        TextView passenger_name = dialogView.findViewById(R.id.passenger_name);
        Button acceptButton = dialogView.findViewById(R.id.accept_button);
        JobResponseApi jobResponseApi = new JobResponseApi(context);
        acceptButton.setOnClickListener(view -> {
            jobResponseApi.acceptResponse(bookingId);
            Log.d("Accept Job Button clicked",""+bookingId);
            Intent intent = new Intent(context, HomeActivity.class);
            context.startActivity(intent);

        });

        Button rejectBooking = dialogView.findViewById(R.id.reject_button);
        rejectBooking.setOnClickListener(view -> {
            jobResponseApi.rejectBooking(bookingId);
            Intent intent = new Intent(context, HomeActivity.class);
            context.startActivity(intent);
        });

        pickup_address.setText(""+pickupAddress);
        destination_address.setText(""+destinationAddress);
        fairy_price.setText("£"+price);
        pickupDateAndTime.setText(""+pickupDate);
        passenger_name.setText(""+passengerName);


        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);

        builder.setCancelable(false);
        AlertDialog alertDialog = builder.create();
        // Set transparent background for the dialog window
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        // Apply rounded background programmatically
        dialogView.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_dialog));

        alertDialog.show();
    }
    public void jobAmenedment(){
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.job_amenedment,null);
        Button closeBtn = dialogView.findViewById(R.id.btnClose);
        closeBtn.setOnClickListener(view -> {
            Intent intent = new Intent(context, HomeActivity.class);
            context.startActivity(intent);
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);

        builder.setCancelable(false);
        AlertDialog alertDialog = builder.create();
        // Set transparent background for the dialog window
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        // Apply rounded background programmatically
        dialogView.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_dialog));

        alertDialog.show();
    }
    public void jobCancel(){
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.job_cancel,null);
        Button closeBtn = dialogView.findViewById(R.id.btnClose);
        closeBtn.setOnClickListener(view -> {
            Intent intent = new Intent(context, HomeActivity.class);
            context.startActivity(intent);
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);

        builder.setCancelable(false);
        AlertDialog alertDialog = builder.create();
        // Set transparent background for the dialog window
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        // Apply rounded background programmatically
        dialogView.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_dialog));

        alertDialog.show();
    }
    public void JobRead(){
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.read_message,null);
        Button closeBtn = dialogView.findViewById(R.id.btnRead);
        closeBtn.setOnClickListener(view -> {
            Intent intent = new Intent(context, HomeActivity.class);
            context.startActivity(intent);
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);

        builder.setCancelable(false);
        AlertDialog alertDialog = builder.create();
        // Set transparent background for the dialog window
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        // Apply rounded background programmatically
        dialogView.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_dialog));

        alertDialog.show();
    }

    public void jobComplete(){
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.complete_job,null);
        Button closeBtn = dialogView.findViewById(R.id.btnSubmit);
        closeBtn.setOnClickListener(view -> {
            Intent intent = new Intent(context, HomeActivity.class);
            context.startActivity(intent);
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);

        builder.setCancelable(false);
        AlertDialog alertDialog = builder.create();
        // Set transparent background for the dialog window
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        // Apply rounded background programmatically
        dialogView.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_dialog));

        alertDialog.show();
    }
    public void jobUnallocated(){
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.unallocated_job,null);
        Button closeBtn = dialogView.findViewById(R.id.btnClose);
        closeBtn.setOnClickListener(view -> {
            Intent intent = new Intent(context, HomeActivity.class);
            context.startActivity(intent);
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);

        builder.setCancelable(false);
        AlertDialog alertDialog = builder.create();
        // Set transparent background for the dialog window
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        // Apply rounded background programmatically
        dialogView.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_dialog));

        alertDialog.show();
    }
}
