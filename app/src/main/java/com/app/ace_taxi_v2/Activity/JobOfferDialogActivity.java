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

import com.app.ace_taxi_v2.JobModals.JobModal;
import com.app.ace_taxi_v2.Logic.JobResponseApi;
import com.app.ace_taxi_v2.R;

public class JobOfferDialogActivity extends Activity {
    private static final String TAG = "JobOfferDialogActivity";
    private AlertDialog alertDialog;
    private Handler handler;
    private boolean isResponded = false; // Flag to track response

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Intent intent = getIntent();
        String pickupAddress = intent.getStringExtra("pickupAddress");
        String destinationAddress = intent.getStringExtra("destinationAddress");
        double price = intent.getDoubleExtra("price", 0.0);
        String pickupDate = intent.getStringExtra("pickupDate");
        String passengerName = intent.getStringExtra("passengerName");
        int bookingId = intent.getIntExtra("bookingId", -1);

        JobModal jobModal = new JobModal(this);
        jobModal.jobOfferModal(pickupAddress,destinationAddress,price,pickupDate,passengerName,bookingId);


    }

}
