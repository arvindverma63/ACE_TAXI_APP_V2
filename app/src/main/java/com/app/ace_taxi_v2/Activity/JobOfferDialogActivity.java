package com.app.ace_taxi_v2.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.app.ace_taxi_v2.Logic.JobResponseApi;
import com.app.ace_taxi_v2.R;

public class JobOfferDialogActivity extends Activity {
    private static final String TAG = "JobOfferDialogActivity";
    private AlertDialog alertDialog;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.job_offer, null);

        TextView pickup_address = dialogView.findViewById(R.id.pickup_address);
        TextView destination_address = dialogView.findViewById(R.id.destination_address);
        TextView fairy_price = dialogView.findViewById(R.id.price);
        TextView pickupDateAndTime = dialogView.findViewById(R.id.pickup_date);
        TextView passenger_name = dialogView.findViewById(R.id.passenger_name);
        TextView timer = dialogView.findViewById(R.id.timer); // Added timer reference
        Button acceptButton = dialogView.findViewById(R.id.accept_button);
        Button rejectButton = dialogView.findViewById(R.id.reject_button);

        // Get data from intent
        Intent intent = getIntent();
        String pickupAddress = intent.getStringExtra("pickupAddress");
        String destinationAddress = intent.getStringExtra("destinationAddress");
        double price = intent.getDoubleExtra("price", 0.0);
        String pickupDate = intent.getStringExtra("pickupDate");
        String passengerName = intent.getStringExtra("passengerName");
        int bookingId = intent.getIntExtra("bookingId", -1);

        if (bookingId == -1) {
            Log.e(TAG, "Invalid bookingId received");
            finish();
            return;
        }

        // Set values
        pickup_address.setText(pickupAddress != null ? pickupAddress : "Unknown Pickup");
        destination_address.setText(destinationAddress != null ? destinationAddress : "Unknown Destination");
        fairy_price.setText("£" + price);
        pickupDateAndTime.setText(pickupDate != null ? pickupDate : "Unknown Date");
        passenger_name.setText(passengerName != null ? passengerName : "Unknown Passenger");

        JobResponseApi jobResponseApi = new JobResponseApi(this);

        acceptButton.setOnClickListener(view -> {
            jobResponseApi.acceptResponse(bookingId);
            Log.d(TAG, "Accept Job Button clicked: " + bookingId);
            startActivity(new Intent(this, HomeActivity.class));
            if (handler != null) {
                handler.removeCallbacksAndMessages(null); // Stop timer
            }
            if (alertDialog != null && alertDialog.isShowing()) {
                alertDialog.dismiss();
            }
            finish();
        });

        rejectButton.setOnClickListener(view -> {
            jobResponseApi.rejectBooking(bookingId);
            Log.d(TAG, "Reject Job Button clicked: " + bookingId);
            startActivity(new Intent(this, HomeActivity.class));
            if (handler != null) {
                handler.removeCallbacksAndMessages(null); // Stop timer
            }
            if (alertDialog != null && alertDialog.isShowing()) {
                alertDialog.dismiss();
            }
            finish();
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        alertDialog = builder.create();

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        dialogView.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_dialog));
        alertDialog.setCancelable(false);
        alertDialog.show();

        // Real-time countdown timer
        final int[] timeLeft = {15}; // Starting at 15 seconds
        handler = new Handler(Looper.getMainLooper());
        Runnable timerRunnable = new Runnable() {
            @Override
            public void run() {
                if (timeLeft[0] >= 0) {
                    timer.setText(timeLeft[0] + "s"); // Update timer text
                    timeLeft[0]--;
                    handler.postDelayed(this, 1000); // Run every second
                } else {
                    if (alertDialog.isShowing()) {
                        JobResponseApi jobResponseApiTimeout = new JobResponseApi(JobOfferDialogActivity.this);
                        jobResponseApiTimeout.timeOut(bookingId);
                        Log.d(TAG, "Job offer timed out: " + bookingId);
                        alertDialog.dismiss();
                        finish(); // Close activity
                    }
                    handler.removeCallbacks(this); // Stop the timer
                }
            }
        };
        handler.post(timerRunnable); // Start the timer
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
        if (handler != null) {
            handler.removeCallbacksAndMessages(null); // Clean up timer
        }
    }
}