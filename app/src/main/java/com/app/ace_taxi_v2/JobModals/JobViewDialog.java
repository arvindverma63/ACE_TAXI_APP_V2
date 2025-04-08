package com.app.ace_taxi_v2.JobModals;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.app.ace_taxi_v2.Components.JobStatusModal;
import com.app.ace_taxi_v2.Logic.GetBookingInfoApi;
import com.app.ace_taxi_v2.Logic.Service.CurrentBookingSession;
import com.app.ace_taxi_v2.Models.Jobs.GetBookingInfo;
import com.app.ace_taxi_v2.R;
import com.google.android.material.button.MaterialButton;

import org.w3c.dom.Text;

import java.lang.ref.WeakReference;
import java.text.NumberFormat;
import java.util.Locale;

public class JobViewDialog {
    private static final String TAG = "JobViewDialog";
    private final WeakReference<Context> contextRef;

    public JobViewDialog(Context context) {
        this.contextRef = new WeakReference<>(context);
    }

    public void JobViewForTodayJob(int bookingId) {
        showJobView(bookingId, true);
    }

    public void JobViewForFutureAndHistory(int bookingId) {
        showJobView(bookingId, false);
    }

    private void showJobView(int bookingId, boolean showCompleteButton) {
        Context context = contextRef.get();
        if (context == null) return;

        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.job_view, null);

        Dialog dialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setContentView(dialogView);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        dialog.show();

        setupJobView(dialogView, bookingId, dialog, showCompleteButton);
    }

    private void setupJobView(View dialogView, int bookingId, Dialog dialog, boolean showCompleteButton) {

        try{
            Context context = contextRef.get();
            if (context == null) return;

            Log.d(TAG, "booking Id setupJobView : " + bookingId);

            TextView pickupAddress = dialogView.findViewById(R.id.pickup_address);
            TextView destinationAddress = dialogView.findViewById(R.id.destination_address);
            TextView pickupdate = dialogView.findViewById(R.id.pickup_date);
            TextView bookingprice = dialogView.findViewById(R.id.price);
            TextView customerName = dialogView.findViewById(R.id.passenger_name);
            TextView distance_duration = dialogView.findViewById(R.id.distance_duration);
            TextView pickup_subaddress = dialogView.findViewById(R.id.pickup_subaddress);
            TextView destination_subaddress = dialogView.findViewById(R.id.destination_subaddress);
            MaterialButton complete_button = dialogView.findViewById(R.id.complete_button);
            MaterialButton start_button = dialogView.findViewById(R.id.start_button);
            ImageView job_status_change = dialogView.findViewById(R.id.change_status);
            ImageView closeBtn = dialogView.findViewById(R.id.close_btn);
            TextView trip_fare = dialogView.findViewById(R.id.trip_fare);
            MaterialButton cancel_button = dialogView.findViewById(R.id.cancel_button);
            TextView passenger_count = dialogView.findViewById(R.id.passenger_count);


            closeBtn.setContentDescription("Close dialog");
            closeBtn.setOnClickListener(v -> dialog.dismiss());

            job_status_change.setContentDescription("Change job status");
            job_status_change.setOnClickListener(v -> {
                dialog.dismiss();
                JobStatusModal jobStatusModal = new JobStatusModal(context);
                jobStatusModal.openModal(bookingId);
            });

            CurrentBookingSession bookingSession = new CurrentBookingSession(context);
            if (bookingSession.getBookingId().equals(String.valueOf(bookingId))) {
                start_button.setVisibility(View.GONE);
            }else{
                complete_button.setVisibility(View.GONE);
            }

            if (!showCompleteButton) complete_button.setVisibility(View.GONE);

            complete_button.setOnClickListener(v -> {
                dialog.dismiss();
                JobModal jobModal = new JobModal(context);
                jobModal.jobCompleteBooking(bookingId);
            });

            start_button.setOnClickListener(v -> {
                dialog.dismiss();
                Toast.makeText(context, "Starting job for booking " + bookingId, Toast.LENGTH_SHORT).show();
                // Add start job logic here
            });
            if(!showCompleteButton){
                complete_button.setVisibility(View.GONE);
                start_button.setVisibility(View.GONE);
                cancel_button.setVisibility(View.GONE);
            }

            GetBookingInfoApi getBookingInfoApi = new GetBookingInfoApi(context);
            getBookingInfoApi.getInfo(bookingId, new GetBookingInfoApi.BookingCallback() {
                @Override
                public void onSuccess(GetBookingInfo bookingInfo) {
                    pickupdate.setText(bookingInfo.getPickupDateTime());
                    customerName.setText(bookingInfo.getPassengerName());
                    bookingprice.setText(NumberFormat.getCurrencyInstance(Locale.UK).format(bookingInfo.getPrice()));
                    trip_fare.setText(NumberFormat.getCurrencyInstance(Locale.UK).format(bookingInfo.getPrice()));
                    distance_duration.setText(bookingInfo.getDurationMinutes() + " minutes");

                    String pickup = bookingInfo.getPickupAddress() != null ? bookingInfo.getPickupAddress() : "";
                    String[] pickupParts = pickup.split(",");
                    String firstPickup = pickupParts.length > 0 ? pickupParts[0].trim() : "";
                    String lastPickup = pickupParts.length > 1 ? pickupParts[1].trim() : "";
                    if (bookingInfo.getPickupPostCode() != null) {
                        lastPickup += " " + bookingInfo.getPickupPostCode();
                    }
                    pickup_subaddress.setText(lastPickup.isEmpty() ? "N/A" : lastPickup);
                    pickupAddress.setText(firstPickup.isEmpty() ? "N/A" : firstPickup);

                    String destination = bookingInfo.getDestinationAddress() != null ? bookingInfo.getDestinationAddress() : "";
                    String[] destinationParts = destination.split(",");
                    String firstDestination = destinationParts.length > 0 ? destinationParts[0].trim() : "";
                    String lastDestination = destinationParts.length > 1 ? destinationParts[1].trim() : "";
                    if (bookingInfo.getDestinationPostCode() != null) {
                        lastDestination += " " + bookingInfo.getDestinationPostCode();
                    }
                    destination_subaddress.setText(lastDestination.isEmpty() ? "N/A" : lastDestination);
                    destinationAddress.setText(firstDestination.isEmpty() ? "N/A" : firstDestination);
                    passenger_count.setText(bookingInfo.getPassengers() + " Passengers");

                }

                @Override
                public void onfailer(String error) {

                }


            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}