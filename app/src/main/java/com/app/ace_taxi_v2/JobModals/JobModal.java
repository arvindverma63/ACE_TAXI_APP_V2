package com.app.ace_taxi_v2.JobModals;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.app.ace_taxi_v2.Activity.HomeActivity;
import com.app.ace_taxi_v2.Components.BookingStartStatus;
import com.app.ace_taxi_v2.Logic.ArrivedJobApi;
import com.app.ace_taxi_v2.Logic.BookingCompleteApi;
import com.app.ace_taxi_v2.Logic.GetBookingInfoApi;
import com.app.ace_taxi_v2.Logic.JobResponseApi;
import com.app.ace_taxi_v2.Logic.Service.NotificationModalSession;
import com.app.ace_taxi_v2.Models.Jobs.GetBookingInfo;
import com.app.ace_taxi_v2.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.w3c.dom.Text;

public class JobModal {
    public Context context;
    public JobModal(Context context){
        this.context = context;
    }



    public void jobOfferModal(String pickupAddress, String destinationAddress, double price, String pickupDate, String passengerName, int bookingId) {
        // Ensure context is an Activity to avoid issues with dialog attachment
        if (!(context instanceof Activity)) {
            Log.e("JobModal", "Context must be an Activity to show dialog");
            return;
        }

        Dialog fullScreenDialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        fullScreenDialog.setContentView(R.layout.job_offer);
        fullScreenDialog.setCancelable(false);

        TextView pickup_address = fullScreenDialog.findViewById(R.id.pickup_address);
        TextView destination_address = fullScreenDialog.findViewById(R.id.destination_address);
        TextView fairy_price = fullScreenDialog.findViewById(R.id.price);
        TextView pickupDateAndTime = fullScreenDialog.findViewById(R.id.pickup_date);
        TextView passenger_name = fullScreenDialog.findViewById(R.id.passenger_name);
        TextView timer = fullScreenDialog.findViewById(R.id.timer);
        Button acceptButton = fullScreenDialog.findViewById(R.id.accept_button);
        Button rejectBooking = fullScreenDialog.findViewById(R.id.reject_button);

        JobResponseApi jobResponseApi = new JobResponseApi(context);

        // Set values
        pickup_address.setText(pickupAddress);
        destination_address.setText(destinationAddress);
        fairy_price.setText("£" + price);
        pickupDateAndTime.setText(pickupDate);
        passenger_name.setText(passengerName);

        // Handler and timer setup
        Handler handler = new Handler(Looper.getMainLooper());
        final boolean[] isResponded = {false};
        final int[] timeLeft = {15};

        // Countdown Timer
        Runnable timerRunnable = new Runnable() {
            @Override
            public void run() {
                if (isResponded[0] || !fullScreenDialog.isShowing()) {
                    stopAllThreads(handler, this);
                    return;
                }

                if (timeLeft[0] >= 0) {
                    timer.setText(timeLeft[0] + "s");
                    timeLeft[0]--;
                    handler.postDelayed(this, 1000);
                } else {
                    // Timeout case
                    if (!isResponded[0]) {
                        isResponded[0] = true;
                        jobResponseApi.timeOut(bookingId);
                        Log.d("Job Offer", "Timed out for booking: " + bookingId);
                        Intent intent = new Intent(context, HomeActivity.class);
                        context.startActivity(intent);
                        fullScreenDialog.dismiss();
                        stopAllThreads(handler, this);
                    }
                }
            }
        };

        // Start the timer
        handler.post(timerRunnable);

        // Accept button
        acceptButton.setOnClickListener(view -> {
            if (!isResponded[0]) {
                isResponded[0] = true;
                stopAllThreads(handler, timerRunnable); // Stop all background processes
                jobResponseApi.acceptResponse(bookingId);
                Log.d("Accept Job", "Booking ID: " + bookingId);
                fullScreenDialog.dismiss(); // Dismiss dialog before starting activity
                Intent intent = new Intent(context, HomeActivity.class);
                intent.putExtra("accepted", true);
                intent.putExtra("passenger", passengerName);
                intent.putExtra("pickupAddress", pickupAddress);
                context.startActivity(intent);
            }
        });

        // Reject button
        rejectBooking.setOnClickListener(view -> {
            if (!isResponded[0]) {
                isResponded[0] = true;
                stopAllThreads(handler, timerRunnable); // Stop all background processes
                jobResponseApi.rejectBooking(bookingId);
                fullScreenDialog.dismiss(); // Dismiss dialog before starting activity
                Intent intent = new Intent(context, HomeActivity.class);
                intent.putExtra("rejected", true);
                context.startActivity(intent);
            }
        });

        // Ensure dialog is dismissed if activity is destroyed
        fullScreenDialog.setOnDismissListener(dialog -> stopAllThreads(handler, timerRunnable));

        // Show dialog
        fullScreenDialog.show();
    }

    // Helper method to stop the timeout and clear all background threads
    private void stopAllThreads(Handler handler, Runnable timerRunnable) {
        if (handler != null && timerRunnable != null) {
            handler.removeCallbacks(timerRunnable); // Remove only the specific runnable
        }
        // Stop all other running tasks in background if needed
        Thread.interrupted(); // Interrupt all background threads
    }






    public void jobOfferModalForTodayJob(int bookingId) {
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


    public void jobAmenedment(String jobId,String passenger,String date){
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.job_amenedment,null);
        Button closeBtn = dialogView.findViewById(R.id.btnClose);
        TextView datetime,passengerName,bookingId;
        datetime = dialogView.findViewById(R.id.tvDateTime);
        passengerName = dialogView.findViewById(R.id.tvCustomerName);
        bookingId = dialogView.findViewById(R.id.tvBookingId);
        datetime.setText(date);
        passengerName.setText(passenger);
        bookingId.setText(jobId+"");

        closeBtn.setOnClickListener(view -> {
            Intent intent = new Intent(context, HomeActivity.class);
            context.startActivity(intent);
        });
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setView(dialogView);

        builder.setCancelable(false);
        android.app.AlertDialog alertDialog = builder.create();
        // Set transparent background for the dialog window
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        // Apply rounded background programmatically
        dialogView.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_dialog));

        alertDialog.show();
    }


    public void jobCancel(String jobId,String passenger,String date){
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.job_cancel,null);
        Button closeBtn = dialogView.findViewById(R.id.btnClose);
        TextView datetime,passengerName,bookingId;
        datetime = dialogView.findViewById(R.id.tvDateTime);
        passengerName = dialogView.findViewById(R.id.tvCustomerName);
        bookingId = dialogView.findViewById(R.id.tvJobId);
        datetime.setText(date);
        passengerName.setText(passenger);
        bookingId.setText(jobId+"");
        closeBtn.setOnClickListener(view -> {
            Intent intent = new Intent(context, HomeActivity.class);
            context.startActivity(intent);
        });
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setView(dialogView);

        builder.setCancelable(false);
        android.app.AlertDialog alertDialog = builder.create();
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

    public void JobReadNotificationClick(String messages,String date) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.read_message, null);

        Button closeBtn = dialogView.findViewById(R.id.btnRead);
        TextView message = dialogView.findViewById(R.id.tvMessage);
        TextView datetime = dialogView.findViewById(R.id.tvDateTime);
        datetime.setText(date);


        message.setText(messages);
        Log.d("NotificationDebug", "Read Message: " + messages);



        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);
        builder.setCancelable(false);

        AlertDialog alertDialog = builder.create();

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        dialogView.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_dialog));

        alertDialog.show();
        closeBtn.setOnClickListener(view -> {
            alertDialog.dismiss();
            readMessage(messages,date);
        });
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

        TextView closeBtn = dialogView.findViewById(R.id.close_dialog);



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


    public void jobUnallocated(int jobId,String passenger,String date){
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.unallocated_job,null);
        Button closeBtn = dialogView.findViewById(R.id.btnClose);
        closeBtn.setOnClickListener(view -> {
            Intent intent = new Intent(context, HomeActivity.class);
            context.startActivity(intent);
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);

        TextView bookingId,passengerName,dateTime;
        bookingId = dialogView.findViewById(R.id.tvJobId);
        passengerName = dialogView.findViewById(R.id.tvPassengerName);
        dateTime = dialogView.findViewById(R.id.tvDate);

        bookingId.setText(jobId+"");
        passengerName.setText(passenger);
        dateTime.setText(date);


        try {
            BookingStartStatus bookingStartStatus = new BookingStartStatus(context);
            String bookingId1 = bookingStartStatus.getBookingId();
            if (jobId == Integer.parseInt(bookingId1)) {
                bookingStartStatus.clearBookingId();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

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
        // Inflate the dialog layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.job_complete_dialog, null);

        // Initialize views
        Button closeBtn = dialogView.findViewById(R.id.btnClose);
        TextView etWaitingTime = dialogView.findViewById(R.id.etWaitingTime);
        TextView etParking = dialogView.findViewById(R.id.etParking);
        TextView etPrice = dialogView.findViewById(R.id.etPrice);
        TextView tip = dialogView.findViewById(R.id.etTip);
        Button btnSubmit = dialogView.findViewById(R.id.btnSubmit);


        // Create full-screen dialog
        Dialog fullScreenDialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        fullScreenDialog.setContentView(dialogView);
        fullScreenDialog.setCancelable(true);

        // Show the dialog
        fullScreenDialog.show();
        closeBtn.setOnClickListener(v -> {
            fullScreenDialog.dismiss();
        });

        // Handle submit button click
        btnSubmit.setOnClickListener(view -> {
            // Extract user inputs
            // Dismiss dialog and navigate to HomeActivity
            fullScreenDialog.dismiss();
            String waitingTimeStr = etWaitingTime.getText().toString().trim();
            String parkingStr = etParking.getText().toString().trim();
            String priceStr = etPrice.getText().toString().trim();
            String etTipStr = tip.getText().toString().trim();

            // Convert to numeric values (handle empty input safely)
            double waitingTime = waitingTimeStr.isEmpty() ? 0 : Double.parseDouble(waitingTimeStr);
            double parking = parkingStr.isEmpty() ? 0 : Double.parseDouble(parkingStr);
            double price = priceStr.isEmpty() ? 0 : Double.parseDouble(priceStr);
            double etTip = etTipStr.isEmpty() ? 0 : Double.parseDouble(etTipStr);

            // Call API with correct data types
            BookingCompleteApi bookingCompleteApi = new BookingCompleteApi(context);
            bookingCompleteApi.complete(bookingId, (int) waitingTime, (int) parking, price, 0, etTip);



            GetBookingInfoApi getBookingInfoApi = new GetBookingInfoApi(context);
            getBookingInfoApi.getInfo(bookingId, new GetBookingInfoApi.BookingCallback() {
                @Override
                public void onSuccess(GetBookingInfo bookingInfo) {
                    BottomSheetDialogs bottomSheetDialogs = new BottomSheetDialogs(context);
                    bottomSheetDialogs.openJobCompleted(bookingInfo.getPassengerName(),bookingInfo.getPickupAddress());
                }

                @Override
                public void onfailer(String error) {

                }
            });

        });
    }

    public void readMessage(String message,String date){
        // Inflate the dialog layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.new_message, null);

        Button closeBtn;
        ImageView backIcon = dialogView.findViewById(R.id.close_dialog);
        closeBtn = dialogView.findViewById(R.id.btnBack);
        TextView datetime = dialogView.findViewById(R.id.datetime);
        TextView tvMessage = dialogView.findViewById(R.id.tvMessage);
        datetime.setText("Effective Date: "+date);
        tvMessage.setText(message);


        // Create full-screen dialog
        Dialog fullScreenDialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        fullScreenDialog.setContentView(dialogView);
        fullScreenDialog.setCancelable(true);


        fullScreenDialog.show();
        closeBtn.setOnClickListener(v -> {
            fullScreenDialog.dismiss();
        });
        backIcon.setOnClickListener(v -> {
            fullScreenDialog.dismiss();
        });

        // Handle submit button click

    }


}
