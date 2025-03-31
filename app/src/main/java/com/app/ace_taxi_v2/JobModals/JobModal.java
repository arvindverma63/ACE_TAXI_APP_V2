package com.app.ace_taxi_v2.JobModals;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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

        TextView pickup_address = fullScreenDialog.findViewById(R.id.pickup_address);
        TextView destination_address = fullScreenDialog.findViewById(R.id.destination_address);
        TextView fairy_price = fullScreenDialog.findViewById(R.id.price);
        TextView pickupDateAndTime = fullScreenDialog.findViewById(R.id.pickup_date);
        TextView passenger_name = fullScreenDialog.findViewById(R.id.passenger_name);
        TextView timer = fullScreenDialog.findViewById(R.id.timer);
        Button acceptButton = fullScreenDialog.findViewById(R.id.accept_button);
        Button rejectBooking = fullScreenDialog.findViewById(R.id.reject_button);

        pickup_address.setText(pickupAddress);
        destination_address.setText(destinationAddress);
        fairy_price.setText(String.format("£%.2f", price));
        pickupDateAndTime.setText(pickupDate);
        passenger_name.setText(passengerName);

        JobResponseApi jobResponseApi = new JobResponseApi(context);
        Handler handler = new Handler(Looper.getMainLooper());
        final boolean[] isResponded = {false};
        final int[] timeLeft = {TIMEOUT_SECONDS};

        Runnable timerRunnable = new Runnable() {
            @Override
            public void run() {
                if (isResponded[0] || !fullScreenDialog.isShowing()) return;
                if (timeLeft[0] >= 0) {
                    timer.setText(timeLeft[0]-- + "s");
                    handler.postDelayed(this, 1000);
                } else if (!isResponded[0]) {
                    isResponded[0] = true;
                    jobResponseApi.timeOut(bookingId);
                    Log.d("Job Offer", "Timed out for booking: " + bookingId);
                    startHomeActivity();
                    fullScreenDialog.dismiss();
                }
            }
        };

        handler.post(timerRunnable);

        acceptButton.setOnClickListener(v -> handleResponse(true, jobResponseApi, bookingId, fullScreenDialog, handler, timerRunnable, isResponded, passengerName, pickupAddress));
        rejectBooking.setOnClickListener(v -> handleResponse(false, jobResponseApi, bookingId, fullScreenDialog, handler, timerRunnable, isResponded));
        fullScreenDialog.setOnDismissListener(d -> handler.removeCallbacks(timerRunnable));
        fullScreenDialog.show();
    }

    private void handleResponse(boolean accept, JobResponseApi jobResponseApi, int bookingId, Dialog dialog,
                                Handler handler, Runnable timerRunnable, boolean[] isResponded,
                                String... extras) {
        if (isResponded[0]) return;
        isResponded[0] = true;
        handler.removeCallbacks(timerRunnable);

        if (accept) {
            jobResponseApi.acceptResponse(bookingId);
            Log.d("Accept Job", "Booking ID: " + bookingId);
            Intent intent = new Intent(context, HomeActivity.class)
                    .putExtra("accepted", true)
                    .putExtra("passenger", extras[0])
                    .putExtra("pickupAddress", extras[1]);
            context.startActivity(intent);
        } else {
            jobResponseApi.rejectBooking(bookingId);
            startHomeActivity();
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

    public void JobRead() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.read_message, null);
        AlertDialog alertDialog = createAlertDialog(dialogView, false);

        TextView message = dialogView.findViewById(R.id.tvMessage);
        NotificationModalSession notificationModalSession = new NotificationModalSession(context);
        String notificationMessage = notificationModalSession.getLatestMessage();
        message.setText(notificationMessage != null && !notificationMessage.isEmpty() ? notificationMessage : "No new notifications.");
        Log.d("NotificationDebug", "Read Message: " + notificationMessage);

        setupCloseButton(dialogView, alertDialog, R.id.btnRead);
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

    public void JobViewForTodayJob(int bookingId) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.job_view, null);
        AlertDialog alertDialog = createAlertDialog(dialogView, true);
        setupJobView(dialogView, bookingId, alertDialog, true);
        alertDialog.show();
    }

    public void JobViewForFutureAndHistory(int bookingId) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.job_view, null);
        AlertDialog alertDialog = createAlertDialog(dialogView, true);
        setupJobView(dialogView, bookingId, alertDialog, false);
        alertDialog.show();
    }

    private void setupJobView(View dialogView, int bookingId, AlertDialog dialog, boolean showCompleteButton) {
        TextView pickupAddress = dialogView.findViewById(R.id.pickup_address);
        TextView destinationAddress = dialogView.findViewById(R.id.destination_address);
        TextView pickupdate = dialogView.findViewById(R.id.pickup_date);
        TextView bookingprice = dialogView.findViewById(R.id.price);
        TextView customerName = dialogView.findViewById(R.id.passenger_name);
        TextView jobId = dialogView.findViewById(R.id.job_id);
        TextView payment_status = dialogView.findViewById(R.id.payment_status);
        TextView distance_duration = dialogView.findViewById(R.id.distance_duration);
        TextView notes = dialogView.findViewById(R.id.notes);
        TextView account_status = dialogView.findViewById(R.id.account_status);
        TextView passenger_email = dialogView.findViewById(R.id.passenger_email);
        TextView passengers_count = dialogView.findViewById(R.id.passengers_count);
        MaterialCardView payment_card = dialogView.findViewById(R.id.payment_status_card);
        MaterialCardView cardView = dialogView.findViewById(R.id.card_view);
        MaterialButton complete_button = dialogView.findViewById(R.id.complete_button);
        TextView job_status_change = dialogView.findViewById(R.id.change_status);
        TextView set_job_status = dialogView.findViewById(R.id.set_job_status);
        LinearLayout status_controller_layout = dialogView.findViewById(R.id.status_controller_layout);
        job_status_change.setOnClickListener(v -> {
            JobStatusModal jobStatusModal = new JobStatusModal(context);
            dialog.dismiss();
            jobStatusModal.openModal(bookingId);
        });
        CurrentBookingSession currentBookingSession = new CurrentBookingSession(context);
        String shift = currentBookingSession.getBookingShift();

        try {
            if(bookingId == Integer.parseInt(currentBookingSession.getBookingId())){
                status_controller_layout.setVisibility(View.VISIBLE);
            }else{
                status_controller_layout.setVisibility(View.GONE);
            }
        }catch (Exception e){
            e.printStackTrace();
            status_controller_layout.setVisibility(View.GONE);
        }

        switch (shift) {
            case "3003":
                set_job_status.setText("On Route");
                set_job_status.setTextColor(ContextCompat.getColor(context,R.color.red));
                break;
            case "3004":
                set_job_status.setText("At Pickup");
                set_job_status.setTextColor(ContextCompat.getColor(context,R.color.blue));
                break;
            case "3005":
                set_job_status.setText("POB");
                set_job_status.setTextColor(ContextCompat.getColor(context,R.color.green));
                break;
            case "3006":
                set_job_status.setText("STC");
                set_job_status.setTextColor(ContextCompat.getColor(context,R.color.orange));
                break;
//            case "3007":
//                set_job_status.setText("On Route");
//                set_job_status.setTextColor(ContextCompat.getColor(context,R.color.red));
//                break;
            case "3008":
                set_job_status.setText("No job");
                set_job_status.setTextColor(ContextCompat.getColor(context,R.color.black));
                break;
            default:
                // Optional: Log unknown status for debugging
                break;
        }

        if (!showCompleteButton) complete_button.setVisibility(View.GONE);

        GetBookingInfoApi getBookingInfoApi = new GetBookingInfoApi(context);
        getBookingInfoApi.getInfo(bookingId, new GetBookingInfoApi.BookingCallback() {
            @Override
            public void onSuccess(GetBookingInfo bookingInfo) {
                pickupAddress.setText(bookingInfo.getPickupAddress());
                destinationAddress.setText(bookingInfo.getDestinationAddress());
                pickupdate.setText(bookingInfo.getPickupDateTime());
                customerName.setText(String.valueOf(bookingInfo.getPassengerName()));
                bookingprice.setText(String.format("£%.2f", bookingInfo.getPrice()));
                jobId.setText(String.valueOf(bookingInfo.getBookingId()));
                payment_status.setText(bookingInfo.getPaymentStatusText());
                distance_duration.setText(bookingInfo.getDurationMinutes() + " minutes");
                notes.setText(bookingInfo.getDetails());
                passenger_email.setText(bookingInfo.getEmail());
                passengers_count.setText(" X " + bookingInfo.getPassengers());
                account_status.setText(bookingInfo.getScopeText());

                if ("Cash".equals(bookingInfo.getScopeText())) {
                    payment_card.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.green));
                    cardView.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.green));
                }
                if ("Unpaid".equals(bookingInfo.getPaymentStatusText())) {
                    payment_card.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.red));
                }
                if ("Account".equals(bookingInfo.getScopeText())) {
                    payment_card.setVisibility(View.GONE);
                    bookingprice.setVisibility(View.GONE);
                }
            }

            @Override
            public void onfailer(String error) {
                Log.e(TAG, "Failed to get booking info: " + error);
            }
        });

        TextView closeBtn = dialogView.findViewById(R.id.close_dialog);
        closeBtn.setOnClickListener(v -> dialog.dismiss());
        complete_button.setOnClickListener(v -> {
            dialog.dismiss();
            jobCompleteBooking(bookingId);
        });
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