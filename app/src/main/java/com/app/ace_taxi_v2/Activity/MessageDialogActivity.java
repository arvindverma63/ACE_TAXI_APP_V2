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
import com.app.ace_taxi_v2.JobModals.JobModal;
import com.app.ace_taxi_v2.R;

import kotlinx.coroutines.Job;

public class MessageDialogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_home);

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

        Intent messageIntent = getIntent();
        String messages = "";
        if (messageIntent.getExtras() != null) {
            messages = messageIntent.getStringExtra("message");
        }
        String datetime = messageIntent.getStringExtra("datetime");
        JobModal jobModal = new JobModal(this);
        jobModal.JobReadNotificationClick(messages,datetime);
    }
    public void unallocatedBooking(){

        Intent intent = getIntent();
        String jobId = getIntent().getStringExtra("jobId");
        String passenger = getIntent().getStringExtra("passenger");
        String date = getIntent().getStringExtra("datetime");

        BookingStartStatus bookingStartStatus = new BookingStartStatus(this);
        if(jobId.equals(bookingStartStatus.getBookingId())){
            bookingStartStatus.clearBookingId();
        }
        int bookingId = -1;
        try{
            if(jobId != null){
                bookingId = Integer.parseInt(jobId);
            }
        } catch (NumberFormatException e) {
            throw new RuntimeException(e);
        }
        JobModal jobModal = new JobModal(this);
        jobModal.jobUnallocated(bookingId,passenger,date);
    }

    public void cancelDialog(){

        String jobId = getIntent().getStringExtra("jobId");
        String passenger = getIntent().getStringExtra("passenger");
        String date = getIntent().getStringExtra("datetime");
        JobModal jobModal = new JobModal(this);
        jobModal.jobCancel(jobId,passenger,date);

    }
    public void jobAmend(){
        String jobId = getIntent().getStringExtra("jobId");
        String passenger = getIntent().getStringExtra("passenger");
        String date = getIntent().getStringExtra("datetime");
        JobModal jobModal = new JobModal(this);
        jobModal.jobAmenedment(jobId,passenger,date);
    }
}
