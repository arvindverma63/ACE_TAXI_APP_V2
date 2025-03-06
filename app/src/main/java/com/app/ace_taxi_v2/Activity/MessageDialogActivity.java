package com.app.ace_taxi_v2.Activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.app.ace_taxi_v2.Components.BookingStartStatus;
import com.app.ace_taxi_v2.R;

public class MessageDialogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String navId = intent.getStringExtra("navId");

        if ("5".equals(navId) || "6".equals(navId)) {
            Log.d("NotificationDebug", "Opening messageDialog()");
            messageDialog();
        } else if ("2".equals(navId)) {
            Log.d("NotificationDebug", "Opening unallocatedBooking()");
            unallocatedBooking();
        } else if("4".equals(navId)){
            cancelDialog();
        }else if("3".equals(navId)){
            jobAmend();
        }else {
            Log.d("NotificationDebug", "Invalid navId: " + navId);
            finish(); // Exit if no valid ID
        }
    }


    public void messageDialog(){
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.read_message, null);

        Button closeBtn = dialogView.findViewById(R.id.btnRead);
        TextView message = dialogView.findViewById(R.id.tvMessage);

        Intent messageIntent = getIntent();
        String messages = "";
        if (messageIntent.getExtras() != null) {
            messages = messageIntent.getStringExtra("message");
        }

        message.setText(messages);
        Log.d("NotificationDebug", "Read Message: " + messages);

        closeBtn.setOnClickListener(view -> {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            finish(); // Ensure this activity is removed from the stack
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        builder.setCancelable(false);

        AlertDialog alertDialog = builder.create();

        // Set background color properly
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        dialogView.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_dialog));

        alertDialog.show(); // Show the dialog last to avoid UI glitches
        closeBtn.setOnClickListener(view -> {
           alertDialog.dismiss();
        });
    }
    public void unallocatedBooking(){
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.unallocated_job,null);
        Button closeBtn = dialogView.findViewById(R.id.btnClose);
        closeBtn.setOnClickListener(view -> {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        TextView bookingId,passengerName,dateTime;
        bookingId = dialogView.findViewById(R.id.tvJobId);
        passengerName = dialogView.findViewById(R.id.tvPassengerName);
        dateTime = dialogView.findViewById(R.id.tvDate);

        Intent intent = getIntent();
        String jobId = getIntent().getStringExtra("jobId");
        String passenger = getIntent().getStringExtra("passenger");
        String date = getIntent().getStringExtra("datetime");

        BookingStartStatus bookingStartStatus = new BookingStartStatus(this);
        if(jobId.equals(bookingStartStatus.getBookingId())){
            bookingStartStatus.clearBookingId();
        }

        bookingId.setText(jobId);
        passengerName.setText(passenger);
        dateTime.setText(date);

        builder.setCancelable(false);
        AlertDialog alertDialog = builder.create();
        // Set transparent background for the dialog window
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        // Apply rounded background programmatically
        dialogView.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_dialog));

        alertDialog.show();
    }

    public void cancelDialog(){
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.job_cancel,null);
        Button closeBtn = dialogView.findViewById(R.id.btnClose);
        TextView datetime,passengerName,bookingId;
        datetime = dialogView.findViewById(R.id.tvDateTime);
        passengerName = dialogView.findViewById(R.id.tvCustomerName);
        bookingId = dialogView.findViewById(R.id.tvJobId);

        String jobId = getIntent().getStringExtra("jobId");
        String passenger = getIntent().getStringExtra("passenger");
        String date = getIntent().getStringExtra("datetime");

        datetime.setText(date);
        passengerName.setText(passenger);
        bookingId.setText(jobId+"");
        closeBtn.setOnClickListener(view -> {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        builder.setCancelable(false);
        AlertDialog alertDialog = builder.create();
        // Set transparent background for the dialog window
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        // Apply rounded background programmatically
        dialogView.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_dialog));

        alertDialog.show();
    }
    public void jobAmend(){
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.job_amenedment,null);
        Button closeBtn = dialogView.findViewById(R.id.btnClose);

        TextView datetime,passengerName,bookingId;
        datetime = dialogView.findViewById(R.id.tvDateTime);
        passengerName = dialogView.findViewById(R.id.tvCustomerName);
        bookingId = dialogView.findViewById(R.id.tvBookingId);

        String jobId = getIntent().getStringExtra("jobId");
        String passenger = getIntent().getStringExtra("passenger");
        String date = getIntent().getStringExtra("datetime");

        datetime.setText(date);
        passengerName.setText(passenger);
        bookingId.setText(jobId+"");

        closeBtn.setOnClickListener(view -> {
            Intent intent = new Intent(this, HomeActivity.class);
            this.startActivity(intent);
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        builder.setCancelable(false);
        AlertDialog alertDialog = builder.create();
        // Set transparent background for the dialog window
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        // Apply rounded background programmatically
        dialogView.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_dialog));

        alertDialog.show();
    }
}
