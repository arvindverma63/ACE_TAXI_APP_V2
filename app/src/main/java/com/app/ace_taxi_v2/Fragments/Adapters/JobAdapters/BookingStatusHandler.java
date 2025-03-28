// BookingStatusHandler.java
package com.app.ace_taxi_v2.Fragments.Adapters.JobAdapters;

import android.content.Context;
import androidx.core.content.ContextCompat;
import com.app.ace_taxi_v2.Components.BookingStartStatus;
import com.app.ace_taxi_v2.Logic.Service.CurrentBookingSession;
import com.app.ace_taxi_v2.Models.Jobs.TodayBooking;
import com.app.ace_taxi_v2.R;
import com.google.android.material.button.MaterialButton;

public class BookingStatusHandler {
    private final Context context;
    private final BookingStartStatus bookingStartStatus;
    private final CurrentBookingSession currentBookingSession;

    public BookingStatusHandler(Context context) {
        this.context = context;
        this.bookingStartStatus = new BookingStartStatus(context);
        this.currentBookingSession = new CurrentBookingSession(context);
    }

    public BookingStartStatus getBookingStartStatus() {
        return bookingStartStatus;
    }

    public void updateButtonStatus(MaterialButton startButton, TodayBooking job, String status) {
        try {
            int bookingId = getCurrentBookingId();

            if (job.getBookingId() == bookingId) {
                startButton.setText("Started");
                startButton.setBackgroundColor(ContextCompat.getColor(context, R.color.dark_green));
            }

            if ("4".equals(job.getStatus())) {
                startButton.setText("TimeOut");
                startButton.setBackgroundColor(ContextCompat.getColor(context, R.color.red));
            }

            switch (status) {
                case "3":
                    bookingStartStatus.clearBookingId();
                    startButton.setText("Completed");
                    startButton.setBackgroundColor(ContextCompat.getColor(context, R.color.primaryColor));
                    startButton.setEnabled(false);
                    break;
                case "2":
                    bookingStartStatus.clearBookingId();
                    startButton.setText("Rejected");
                    startButton.setBackgroundColor(ContextCompat.getColor(context, R.color.primaryColor));
                    startButton.setEnabled(false);
                    break;
                case "1":
                    startButton.setText("Start");
                    int statusBookingId = getStatusBookingId();
                    if (statusBookingId == job.getBookingId()) {
                        startButton.setText("Active");
                        startButton.setBackgroundColor(ContextCompat.getColor(context, R.color.dark_blue));
//                        updateDetailedStatus(startButton);
                    }
                    break;
            }
        } catch (Exception e) {
            // Handle exception if needed
        }
    }

    private int getCurrentBookingId() {
        try {
            String id = currentBookingSession.getBookingId();
            return id != null && !id.isEmpty() ? Integer.parseInt(id) : -1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private int getStatusBookingId() {
        try {
            String id = bookingStartStatus.getBookingId();
            return id != null ? Integer.parseInt(id) : -1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

//    private void updateDetailedStatus(MaterialButton statusText) {
//        String bookingShift = currentBookingSession.getBookingShift();
//        if (bookingShift == null) return;
//
//        switch (bookingShift) {
//            case "3003":
//                statusText.setText("On Route");
//                statusText.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.light_red));
//                break;
//            case "3004":
//                statusText.setText("On Pickup");
//                statusText.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.light_blue));
//                break;
//            case "3005":
//                statusText.setText("POB");
//                statusText.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.green));
//                break;
//            case "3006":
//                statusText.setText("STC");
//                statusText.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.light_orange));
//                break;
//        }
//    }
}