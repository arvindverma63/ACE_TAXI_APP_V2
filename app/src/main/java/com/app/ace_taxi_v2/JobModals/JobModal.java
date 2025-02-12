package com.app.ace_taxi_v2.JobModals;

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

import com.app.ace_taxi_v2.Activity.HomeActivity;
import com.app.ace_taxi_v2.Logic.ArrivedJobApi;
import com.app.ace_taxi_v2.Logic.BookingCompleteApi;
import com.app.ace_taxi_v2.Logic.JobResponseApi;
import com.app.ace_taxi_v2.Logic.Service.NotificationModalSession;
import com.app.ace_taxi_v2.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.w3c.dom.Text;

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

        AlertDialog alertDialog = builder.create();
        // Set transparent background for the dialog window
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        // Apply rounded background programmatically
        dialogView.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_dialog));

        alertDialog.show();
    }


    public void jobOfferModalForTodayJob(String pickupAddress, String destinationAddress, double price, String pickupDate, String passengerName, int bookingId) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.job_status_reply, null);


        Button acceptButton = dialogView.findViewById(R.id.accept_button);
        Button rejectBooking = dialogView.findViewById(R.id.reject_button);

        JobResponseApi jobResponseApi = new JobResponseApi(context);


        // Initialize BottomSheetDialog
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        bottomSheetDialog.setContentView(dialogView);

        // Set transparent background & apply rounded background
        dialogView.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_dialog));

        bottomSheetDialog.show();

        acceptButton.setOnClickListener(view -> {
            jobResponseApi.acceptResponse(bookingId);
            Log.d("Accept Job Button clicked", "" + bookingId);
            bottomSheetDialog.dismiss();
        });

        rejectBooking.setOnClickListener(view -> {
            jobResponseApi.rejectBooking(bookingId);
            bottomSheetDialog.dismiss();
        });
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


    public void JobRead() {

        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.read_message, null);

        Button closeBtn = dialogView.findViewById(R.id.btnRead);
        TextView message = dialogView.findViewById(R.id.tvMessage);

        NotificationModalSession notificationModalSession = new NotificationModalSession(context);
        String notificationMessage = notificationModalSession.getLatestMessage(); // No need for String.valueOf()

        // Ensure notificationMessage is not null or empty
        if (notificationMessage == null || notificationMessage.isEmpty()) {
            notificationMessage = "No new notifications.";
        }

        message.setText(notificationMessage);
        Log.d("NotificationDebug", "Read Message: " + notificationMessage);

        closeBtn.setOnClickListener(view -> {
            Intent intent = new Intent(context, HomeActivity.class);
            context.startActivity(intent);
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);
        builder.setCancelable(false);

        AlertDialog alertDialog = builder.create();

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

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


    public void JobViewForTodayJob(String pickup,String destination,String date,String passenger,int bookingId,String status,double price){
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.job_view,null);

        TextView pickupAddress,destinationAddress,pickupdate,bookingprice,customerName;
        pickupAddress = dialogView.findViewById(R.id.pickup_address);
        destinationAddress = dialogView.findViewById(R.id.destination_address);
        pickupdate = dialogView.findViewById(R.id.pickup_date);
        bookingprice = dialogView.findViewById(R.id.price);
        customerName = dialogView.findViewById(R.id.passenger_name);

        pickupAddress.setText(pickup);
        destinationAddress.setText(destination);
        pickupdate.setText(date);
        customerName.setText(passenger);
        bookingprice.setText("£"+price);

        Button completeBtn = dialogView.findViewById(R.id.complete_button);
        TextView closeBtn = dialogView.findViewById(R.id.close_dialog);

        completeBtn.setOnClickListener(v -> {
            ArrivedJobApi arrivedJobApi = new ArrivedJobApi(context);
            arrivedJobApi.updateStatus(bookingId);
        });

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

        closeBtn.setOnClickListener(v -> {
            alertDialog.dismiss();
        });
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


    public void notJobStartedYetModal(){
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.job_start_modal,null);
        Button closeBtn = dialogView.findViewById(R.id.complete_button);

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
        closeBtn.setOnClickListener(view -> {
            alertDialog.dismiss();
        });
    }

    public void jobCompleteBooking(int bookingId) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.complete_job, null);

        // Initialize views
        Button closeBtn = dialogView.findViewById(R.id.btnSubmit);
        TextView etWaitingTime = dialogView.findViewById(R.id.etWaitingTime);
        TextView etParking = dialogView.findViewById(R.id.etParking);
        TextView etPrice = dialogView.findViewById(R.id.etPrice);
        TextView tvJobId = dialogView.findViewById(R.id.tvJobId);

        tvJobId.setText(""+bookingId);

        // Show the dialog
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

        // Handle submit button click
        closeBtn.setOnClickListener(view -> {
            // Extract user inputs
            String waitingTimeStr = etWaitingTime.getText().toString().trim();
            String parkingStr = etParking.getText().toString().trim();
            String priceStr = etPrice.getText().toString().trim();

            // Convert to numeric values (handle empty input safely)
            double waitingTime = waitingTimeStr.isEmpty() ? 0 : Double.parseDouble(waitingTimeStr);
            double parking = parkingStr.isEmpty() ? 0 : Double.parseDouble(parkingStr);
            double price = priceStr.isEmpty() ? 0 : Double.parseDouble(priceStr);

            // Call API with correct data types
            BookingCompleteApi bookingCompleteApi = new BookingCompleteApi(context);
            bookingCompleteApi.complete(bookingId, (int) waitingTime, (int) parking, price, 0);

            // Dismiss dialog and navigate to HomeActivity
            alertDialog.dismiss();

        });
    }

}
