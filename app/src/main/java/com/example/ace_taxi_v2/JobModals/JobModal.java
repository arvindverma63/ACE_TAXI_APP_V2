package com.example.ace_taxi_v2.JobModals;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.ace_taxi_v2.R;

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

    public void futureJobView(String pickupAddress,String destinationAddress,double price,String pickupDate,String passengerName){
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.job_view,null);

        TextView pickup_address = dialogView.findViewById(R.id.pickup_address);
        TextView destination_address = dialogView.findViewById(R.id.destination_address);
        TextView fairy_price = dialogView.findViewById(R.id.price);
        TextView pickupDateAndTime = dialogView.findViewById(R.id.pickup_date);
        TextView passenger_name = dialogView.findViewById(R.id.passenger_name);

        pickup_address.setText(""+pickupAddress);
        destination_address.setText(""+destinationAddress);
        fairy_price.setText("£"+price);
        pickupDateAndTime.setText(""+pickupDate);
        passenger_name.setText(""+passengerName);



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
}
