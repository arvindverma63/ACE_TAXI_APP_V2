package com.app.ace_taxi_v2.JobModals;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.app.ace_taxi_v2.Activity.HomeActivity;
import com.app.ace_taxi_v2.Components.BookingStartStatus;
import com.app.ace_taxi_v2.Components.JobStatusModal;
import com.app.ace_taxi_v2.Logic.ArrivedJobApi;
import com.app.ace_taxi_v2.Logic.BookingCompleteApi;
import com.app.ace_taxi_v2.Logic.GetBookingInfoApi;
import com.app.ace_taxi_v2.Logic.JobResponseApi;
import com.app.ace_taxi_v2.Logic.Service.CurrentBookingSession;
import com.app.ace_taxi_v2.Logic.Service.NotificationModalSession;
import com.app.ace_taxi_v2.Models.Jobs.GetBookingInfo;
import com.app.ace_taxi_v2.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import org.w3c.dom.Text;

public class JobModal {
    public Context context;
    private static final int TIMEOUT_SECONDS = 15;
    private static final String TAG = "JobModal";

    public JobModal(Context context) {
        this.context = context;
    }

    // Reusable method to create and configure AlertDialog
    private AlertDialog createAlertDialog(View dialogView, boolean cancelable) {
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(dialogView)
                .setCancelable(cancelable)
                .create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }
        dialogView.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_dialog));
        return dialog;
    }

    // Reusable method to set up dialog dismissal with close button
    private void setupCloseButton(View dialogView, Dialog dialog, int buttonId) {
        Button closeBtn = dialogView.findViewById(buttonId);
        closeBtn.setOnClickListener(v -> dialog.dismiss());
    }

    public void jobOfferModal(String pickupAddress, String destinationAddress, double price,
                              String pickupDate, String passengerName, int bookingId) {
        if (!(context instanceof Activity)) {
            Log.e(TAG, "Context must be an Activity to show dialog");
            return;
        }

        Dialog fullScreenDialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        fullScreenDialog.setContentView(R.layout.job_offer);
        fullScreenDialog.setCancelable(false);

        MaterialTextView pickup_address = fullScreenDialog.findViewById(R.id.pickup_address);
        MaterialTextView destination_address = fullScreenDialog.findViewById(R.id.destination_address);
        MaterialTextView fairy_price = fullScreenDialog.findViewById(R.id.price);
        MaterialTextView pickupDateAndTime = fullScreenDialog.findViewById(R.id.pickup_date);
        MaterialTextView passenger_name = fullScreenDialog.findViewById(R.id.passenger_name);
        MaterialTextView timer = fullScreenDialog.findViewById(R.id.timer);
        MaterialButton acceptButton = fullScreenDialog.findViewById(R.id.accept_button);
        MaterialButton rejectBooking = fullScreenDialog.findViewById(R.id.reject_button);
        MaterialTextView tripfare = fullScreenDialog.findViewById(R.id.bookerName);
        MaterialTextView jobId = fullScreenDialog.findViewById(R.id.bookingId);
        MaterialTextView passengerCount = fullScreenDialog.findViewById(R.id.passenger_count);
        MaterialTextView pickupTime = fullScreenDialog.findViewById(R.id.pickup_time);
        MaterialTextView destinationTime = fullScreenDialog.findViewById(R.id.destination_time);
        MaterialTextView distance_duration = fullScreenDialog.findViewById(R.id.distance_duration);
        MaterialTextView scopeText = fullScreenDialog.findViewById(R.id.scope_text);
        MaterialCardView scopeCard = fullScreenDialog.findViewById(R.id.scope_card);
        MaterialTextView payment_status = fullScreenDialog.findViewById(R.id.payment_status);
        MaterialCardView paymentCard = fullScreenDialog.findViewById(R.id.payment_card);
        MaterialCardView asap_card = fullScreenDialog.findViewById(R.id.asap_card);



        GetBookingInfoApi getBookingInfoApi = new GetBookingInfoApi(context);
        getBookingInfoApi.getInfo(bookingId, new GetBookingInfoApi.BookingCallback() {
            @Override
            public void onSuccess(GetBookingInfo bookingInfo) {
                try{
                    passengerCount.setText(bookingInfo.getPassengers()+" Passengers");
                    jobId.setText("#"+bookingInfo.getBookingId());
                    distance_duration.setText(bookingInfo.getMileage()+" Miles");
                    String pickupTimeText = bookingInfo.getPickupDateTime();
                    String[] parts = pickupTimeText.split(",");
                    pickupTime.setText(parts[parts.length - 1]);
                    if(bookingInfo.isASAP()){
                        asap_card.setVisibility(View.VISIBLE);
                    }
                    if(bookingInfo.getArriveBy()== null){
                        destinationTime.setVisibility(View.GONE);
                    }else {
                        destinationTime.setText(bookingInfo.getArriveBy());
                    }
                    pickupDateAndTime.setText(bookingInfo.getFormattedDateTime());
                    if(bookingInfo.getScopeText().equals("Account")){
                        scopeCard.setBackgroundTintList(ContextCompat.getColorStateList(context,R.color.red));
                        scopeText.setText(bookingInfo.getAccountNumber()+"");
                        paymentCard.setBackgroundTintList(ContextCompat.getColorStateList(context,R.color.red));
                        payment_status.setText("ACCOUNT");
                    }else if(bookingInfo.getScopeText().equals("Card")){
                        if(bookingInfo.getPaymentStatusText().equals("Unpaid")){
                            paymentCard.setBackgroundTintList(ContextCompat.getColorStateList(context,R.color.red));
                            payment_status.setText("UNPAID");
                        }else if(bookingInfo.getPaymentStatusText().equals("Paid")){
                            paymentCard.setBackgroundTintList(ContextCompat.getColorStateList(context,R.color.green));
                            payment_status.setText("PAID");
                        }
                    }else if(bookingInfo.getScopeText().equals("Cash")){
                        paymentCard.setVisibility(View.GONE);
                        scopeCard.setBackgroundTintList(ContextCompat.getColorStateList(context,R.color.green));
                        scopeText.setText("CASH");
                    }else if(bookingInfo.getScopeText().equals("Rank")){
                        paymentCard.setVisibility(View.GONE);
                        scopeCard.setBackgroundTintList(ContextCompat.getColorStateList(context,R.color.purple));
                        scopeText.setText("RANK");
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onfailer(String error) {

            }
        });
        pickup_address.setText(pickupAddress);
        destination_address.setText(destinationAddress);
        tripfare.setText(passengerName);
        passenger_name.setText(passengerName);
        fairy_price.setText(String.format("£%.2f", price));
        JobResponseApi jobResponseApi = new JobResponseApi(context);
        Handler handler = new Handler(Looper.getMainLooper());
        final boolean[] isResponded = {false};



        acceptButton.setOnClickListener(v -> handleResponse(true, jobResponseApi, bookingId, fullScreenDialog, handler, isResponded, passengerName, pickupAddress));
        rejectBooking.setOnClickListener(v -> handleResponse(false, jobResponseApi, bookingId, fullScreenDialog, handler, isResponded));
        fullScreenDialog.show();
    }

    private void handleResponse(boolean accept, JobResponseApi jobResponseApi, int bookingId, Dialog dialog,
                                Handler handler, boolean[] isResponded,
                                String... extras) {

        if (accept) {
            jobResponseApi.acceptResponse(bookingId);
            Log.d("Accept Job", "Booking ID: " + bookingId);
            dialog.dismiss();
        } else {
            jobResponseApi.rejectBooking(bookingId);
            dialog.dismiss();
        }
        dialog.dismiss();
    }

    private void startHomeActivity() {
        context.startActivity(new Intent(context, HomeActivity.class));
    }

    public void jobOfferModalForTodayJob(int bookingId) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.job_status_reply, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        bottomSheetDialog.setContentView(dialogView);
        dialogView.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_dialog));

        JobResponseApi jobResponseApi = new JobResponseApi(context);
        Button acceptButton = dialogView.findViewById(R.id.accept_button);
        Button rejectBooking = dialogView.findViewById(R.id.reject_button);

        acceptButton.setOnClickListener(v -> {
            jobResponseApi.acceptResponse(bookingId);
            Log.d("Accept Job Button clicked", "" + bookingId);
            bottomSheetDialog.dismiss();
        });
        rejectBooking.setOnClickListener(v -> {
            jobResponseApi.rejectBooking(bookingId);
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.show();
    }

    public void jobAmenedment(String jobId, String passenger, String date) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.job_amenedment, null);
        AlertDialog alertDialog = createAlertDialog(dialogView, false);

        TextView datetime = dialogView.findViewById(R.id.tvDateTime);
        TextView passengerName = dialogView.findViewById(R.id.tvCustomerName);
        TextView bookingId = dialogView.findViewById(R.id.tvBookingId);

        datetime.setText(date);
        passengerName.setText(passenger);
        bookingId.setText(jobId);

        setupCloseButton(dialogView, alertDialog, R.id.btnClose);
        alertDialog.show();
    }

    public void jobCancel(String jobId, String passenger, String date) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.job_cancel, null);
        AlertDialog alertDialog = createAlertDialog(dialogView, false);

        TextView datetime = dialogView.findViewById(R.id.tvDateTime);
        TextView passengerName = dialogView.findViewById(R.id.tvCustomerName);
        TextView bookingId = dialogView.findViewById(R.id.tvJobId);

        datetime.setText(date);
        passengerName.setText(passenger);
        bookingId.setText(jobId);

        setupCloseButton(dialogView, alertDialog, R.id.btnClose);
        alertDialog.show();
    }


    public void JobReadNotificationClick(String messages, String date) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.read_message, null);
        AlertDialog alertDialog = createAlertDialog(dialogView, false);

        TextView message = dialogView.findViewById(R.id.tvMessage);
        TextView datetime = dialogView.findViewById(R.id.tvDateTime);
        message.setText(messages);
        datetime.setText(date);
        Log.d("NotificationDebug", "Read Message: " + messages);

        Button closeBtn = dialogView.findViewById(R.id.btnRead);
        closeBtn.setOnClickListener(v -> {
            alertDialog.dismiss();
            readMessage(messages, date);
        });
        alertDialog.show();
    }



    public void jobUnallocated(int jobId, String passenger, String date) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.unallocated_job, null);
        AlertDialog alertDialog = createAlertDialog(dialogView, false);

        TextView bookingId = dialogView.findViewById(R.id.tvJobId);
        TextView passengerName = dialogView.findViewById(R.id.tvPassengerName);
        TextView dateTime = dialogView.findViewById(R.id.tvDate);

        bookingId.setText(String.valueOf(jobId));
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

        Button closeBtn = dialogView.findViewById(R.id.btnClose);
        closeBtn.setOnClickListener(v -> {
            startHomeActivity();
            alertDialog.dismiss();
        });
        alertDialog.show();
    }

    public void notJobStartedYetModal() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.job_start_modal, null);
        AlertDialog alertDialog = createAlertDialog(dialogView, false);
        setupCloseButton(dialogView, alertDialog, R.id.complete_button);
        alertDialog.show();
    }

    public void jobCompleteBooking(int bookingId) {
        Dialog fullScreenDialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.job_complete_dialog, null);
        fullScreenDialog.setContentView(dialogView);
        fullScreenDialog.setCancelable(true);



        TextView etWaitingTime = dialogView.findViewById(R.id.etWaitingTime);
        TextView etParking = dialogView.findViewById(R.id.etParking);
        TextView etPrice = dialogView.findViewById(R.id.etPrice);
        TextView tip = dialogView.findViewById(R.id.etTip);
        Button btnSubmit = dialogView.findViewById(R.id.btnSubmit);
        setupCloseButton(dialogView, fullScreenDialog, R.id.btnClose);

        btnSubmit.setOnClickListener(v -> {
            double waitingTime = parseDouble(etWaitingTime.getText().toString());
            double parking = parseDouble(etParking.getText().toString());
            double price = parseDouble(etPrice.getText().toString());
            double etTip = parseDouble(tip.getText().toString());

            BookingCompleteApi bookingCompleteApi = new BookingCompleteApi(context);
            bookingCompleteApi.complete(bookingId, (int) waitingTime, (int) parking, price, 0, etTip);

            GetBookingInfoApi getBookingInfoApi = new GetBookingInfoApi(context);
            getBookingInfoApi.getInfo(bookingId, new GetBookingInfoApi.BookingCallback() {
                @Override
                public void onSuccess(GetBookingInfo bookingInfo) {
                    BottomSheetDialogs bottomSheetDialogs = new BottomSheetDialogs(context);
                    bottomSheetDialogs.openJobCompleted(bookingInfo.getPassengerName(), bookingInfo.getPickupAddress());
                    if("Account".equals(bookingInfo.getScopeText())){
                        etPrice.setEnabled(false);
                    }
                }
                @Override
                public void onfailer(String error) {
                    Log.e(TAG, "Failed to get booking info: " + error);
                }
            });

            fullScreenDialog.dismiss();
        });

        fullScreenDialog.show();
    }

    private double parseDouble(String value) {
        return value.trim().isEmpty() ? 0 : Double.parseDouble(value);
    }

    public void readMessage(String message, String date) {
        Dialog fullScreenDialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.new_message, null);
        fullScreenDialog.setContentView(dialogView);
        fullScreenDialog.setCancelable(true);

        TextView datetime = dialogView.findViewById(R.id.datetime);
        TextView tvMessage = dialogView.findViewById(R.id.tvMessage);
        ImageView backIcon = dialogView.findViewById(R.id.close_dialog);
        Button closeBtn = dialogView.findViewById(R.id.btnBack);

        datetime.setText("Effective Date: " + date);
        tvMessage.setText(message);

        closeBtn.setOnClickListener(v -> fullScreenDialog.dismiss());
        backIcon.setOnClickListener(v -> fullScreenDialog.dismiss());
        fullScreenDialog.show();
    }


}