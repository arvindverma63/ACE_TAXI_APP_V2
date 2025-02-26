package com.app.ace_taxi_v2.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.app.ace_taxi_v2.Logic.JobResponseApi;
import com.app.ace_taxi_v2.R;

public class JobOfferDialogActivity extends Activity {
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
        Button acceptButton = dialogView.findViewById(R.id.accept_button);
        Button rejectButton = dialogView.findViewById(R.id.reject_button);

        // Get data from intent
        Intent intent = getIntent();
        String pickupAddress = intent.getStringExtra("pickupAddress");
        String destinationAddress = intent.getStringExtra("destinationAddress");
        double price = intent.getDoubleExtra("price", 0.0);
        String pickupDate = intent.getStringExtra("pickupDate");
        String passengerName = intent.getStringExtra("passengerName");
        int bookingId = intent.getIntExtra("bookingId", 0);

        // Set values
        pickup_address.setText(pickupAddress);
        destination_address.setText(destinationAddress);
        fairy_price.setText("£" + price);
        pickupDateAndTime.setText(pickupDate);
        passenger_name.setText(passengerName);

        JobResponseApi jobResponseApi = new JobResponseApi(this);

        acceptButton.setOnClickListener(view -> {
            jobResponseApi.acceptResponse(bookingId);
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        });

        rejectButton.setOnClickListener(view -> {
            jobResponseApi.rejectBooking(bookingId);
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        dialogView.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_dialog));
        alertDialog.show();
    }
}
