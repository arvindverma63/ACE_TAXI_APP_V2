package com.app.ace_taxi_v2.JobModals;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.app.ace_taxi_v2.Activity.HomeActivity;
import com.app.ace_taxi_v2.Components.BookingStartStatus;
import com.app.ace_taxi_v2.Components.CustomToast;
import com.app.ace_taxi_v2.Components.JobStatusModal;
import com.app.ace_taxi_v2.Helper.LogHelperLaravel;
import com.app.ace_taxi_v2.Logic.ArrivedJobApi;
import com.app.ace_taxi_v2.Logic.BookingCompleteApi;
import com.app.ace_taxi_v2.Logic.Formater.HHMMFormater;
import com.app.ace_taxi_v2.Logic.GetBookingInfoApi;
import com.app.ace_taxi_v2.Logic.JobResponseApi;
import com.app.ace_taxi_v2.Logic.Service.CurrentBookingSession;
import com.app.ace_taxi_v2.Logic.Service.NotificationModalSession;
import com.app.ace_taxi_v2.Logic.Service.NotificationService;
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
        closeBtn.setOnClickListener(v -> {
            NotificationService service = NotificationService.getInstance();
            if (service != null) {
                service.stopSound();
            }

            Intent intent = new Intent(context,HomeActivity.class);
            context.startActivity(intent);
        });
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
        MaterialTextView pickup_subAddress = fullScreenDialog.findViewById(R.id.pickup_subaddress);
        MaterialTextView destination_subAddress = fullScreenDialog.findViewById(R.id.destination_subaddress);
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
        MaterialTextView distance = fullScreenDialog.findViewById(R.id.distance);



        GetBookingInfoApi getBookingInfoApi = new GetBookingInfoApi(context);
        getBookingInfoApi.getInfo(bookingId, new GetBookingInfoApi.BookingCallback() {
            @Override
            public void onSuccess(GetBookingInfo bookingInfo) {
                try{

                    String pickup = bookingInfo.getPickupAddress() != null ? bookingInfo.getPickupAddress() : "";
                    String[] pickupParts = pickup.split(",");
                    String firstPickup = pickupParts.length > 0 ? pickupParts[0].trim() : "";
                    String lastPickup = pickupParts.length > 1 ? pickupParts[1].trim() : "";
                    if (bookingInfo.getPickupPostCode() != null) {
                        lastPickup += " " + bookingInfo.getPickupPostCode();
                    }
                    pickup_subAddress.setText(lastPickup.isEmpty() ? "N/A" : lastPickup);
                    pickup_address.setText(firstPickup.isEmpty() ? "N/A" : firstPickup);
                    distance.setText(bookingInfo.getMileage()+" Miles");
                    if(bookingInfo.isASAP()){
                        asap_card.setVisibility(View.VISIBLE);
                    }

                    String destination = bookingInfo.getDestinationAddress() != null ? bookingInfo.getDestinationAddress() : "";
                    String[] destinationParts = destination.split(",");
                    String firstDestination = destinationParts.length > 0 ? destinationParts[0].trim() : "";
                    String lastDestination = destinationParts.length > 1 ? destinationParts[1].trim() : "";
                    if (bookingInfo.getDestinationPostCode() != null) {
                        lastDestination += " " + bookingInfo.getDestinationPostCode();
                    }
                    destination_subAddress.setText(lastDestination.isEmpty() ? "N/A" : lastDestination);
                    destination_address.setText(firstDestination.isEmpty() ? "N/A" : firstDestination);
                    tripfare.setText(bookingInfo.getBookedByName());

                    passengerCount.setText(bookingInfo.getPassengers()+" Passengers");
                    jobId.setText("#"+bookingInfo.getBookingId());
                    distance_duration.setText(formatDuration(bookingInfo.getDurationMinutes()));
                    HHMMFormater formater = new HHMMFormater();
                    pickupTime.setText(formater.formatDateTime(bookingInfo.getPickupDateTime()));
                    if(bookingInfo.isASAP()){
                        asap_card.setVisibility(View.VISIBLE);
                    }
                    if(bookingInfo.getArriveBy()== null){
                        destinationTime.setVisibility(View.GONE);
                    }else {
                        destinationTime.setText(formater.formatDateTime(bookingInfo.getArriveBy()));
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
        NotificationService service = NotificationService.getInstance();
        if (service != null) {
            service.stopSound();
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
            NotificationService service = NotificationService.getInstance();
            if (service != null) {
                service.stopSound();
            }
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

    public void jobCompleteBooking(int bookingId, double iprice) {
        Dialog fullScreenDialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.job_complete_dialog, null);
        fullScreenDialog.setContentView(dialogView);
        fullScreenDialog.setCancelable(true);

        // Initialize views (assuming EditText for input fields)
        EditText etWaitingTime = dialogView.findViewById(R.id.etWaitingTime);
        EditText etParking = dialogView.findViewById(R.id.etParking);
        EditText etPrice = dialogView.findViewById(R.id.etPrice);
        EditText etTip = dialogView.findViewById(R.id.etTip);
        Button btnSubmit = dialogView.findViewById(R.id.btnSubmit);
        setupCloseButton(dialogView, fullScreenDialog, R.id.btnClose);

        // Set price with proper formatting
        etPrice.setText(String.format("%.2f", iprice));

        // Fetch booking info before showing dialog
        GetBookingInfoApi getBookingInfoApi = new GetBookingInfoApi(context);
        getBookingInfoApi.getInfo(bookingId, new GetBookingInfoApi.BookingCallback() {
            @Override
            public void onSuccess(GetBookingInfo bookingInfo) {
                // Set enabled state based on scopeText
                if ("Account".equals(bookingInfo.getScopeText()) || "Card".equals(bookingInfo.getScopeText())) {
                    etParking.setEnabled(false);
                    etWaitingTime.setEnabled(false);
                }
                // Show dialog after setting UI state
                fullScreenDialog.show();
            }

            @Override
            public void onfailer(String error) {
                Log.e(TAG, "Failed to get booking info: " + error);
                new CustomToast(context).showCustomErrorToast("Failed to load booking info: " + error);
                // Optionally show dialog or handle error
                fullScreenDialog.show();
            }
        });

        btnSubmit.setOnClickListener(v -> {
            // Validate and parse inputs
            double waitingTime, parking, price, tipAmount;
            try {
                waitingTime = parseDoubleOrZero(etWaitingTime.getText().toString());
                parking = parseDoubleOrZero(etParking.getText().toString());
                price = parseDoubleOrZero(etPrice.getText().toString());
                tipAmount = parseDoubleOrZero(etTip.getText().toString());
            } catch (NumberFormatException e) {
                new CustomToast(context).showCustomErrorToast("Invalid input values");
                return;
            }

            LogHelperLaravel.getInstance().i(TAG, "bookingId " + bookingId + " click on complete button");
            BookingCompleteApi bookingCompleteApi = new BookingCompleteApi(context);
            bookingCompleteApi.complete(bookingId, (int) waitingTime, (int) parking, price, 0, tipAmount);

            // Fetch booking info again for bottom sheet
            getBookingInfoApi.getInfo(bookingId, new GetBookingInfoApi.BookingCallback() {
                @Override
                public void onSuccess(GetBookingInfo bookingInfo) {
                    LogHelperLaravel.getInstance().i(TAG, bookingInfo + " success");
                    // Navigate to HomeActivity and dismiss dialog
                    Intent intent = new Intent(context, HomeActivity.class);
                    context.startActivity(intent);
                    fullScreenDialog.dismiss();
                }

                @Override
                public void onfailer(String error) {
                    Log.e(TAG, "Failed to get booking info: " + error);
                    new CustomToast(context).showCustomErrorToast("Failed to load booking info: " + error);
                    // Still navigate and dismiss
                    Intent intent = new Intent(context, HomeActivity.class);
                    context.startActivity(intent);
                    fullScreenDialog.dismiss();
                }
            });
        });
    }

    // Helper method to parse double with fallback
    private double parseDoubleOrZero(String input) {
        try {
            return input.isEmpty() ? 0.0 : Double.parseDouble(input);
        } catch (NumberFormatException e) {
            return 0.0;
        }
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

        datetime.setText("" + date);
        tvMessage.setText(message);

        closeBtn.setOnClickListener(v -> fullScreenDialog.dismiss());
        backIcon.setOnClickListener(v -> fullScreenDialog.dismiss());
        fullScreenDialog.show();
    }
    public String formatDuration(int totalMinutes) {
        int hours = totalMinutes / 60;
        int minutes = totalMinutes % 60;

        StringBuilder result = new StringBuilder();
        if (hours > 0) {
            result.append(hours).append(" Hr");
        }
        if (minutes > 0) {
            if (result.length() > 0) {
                result.append(", ");
            }
            result.append(minutes).append(" min");
        }
        if (result.length() == 0) {
            return "0 min"; // fallback if duration is 0
        }

        return result.toString();
    }

}