package com.app.ace_taxi_v2.Fragments.Adapters.JobAdapters;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.ace_taxi_v2.Activity.JobOfferDialogActivity;
import com.app.ace_taxi_v2.Components.BookingStartStatus;
import com.app.ace_taxi_v2.Components.ShiftChangeModal;
import com.app.ace_taxi_v2.JobModals.JobModal;
import com.app.ace_taxi_v2.Logic.GetBookingInfoApi;
import com.app.ace_taxi_v2.Logic.Service.CurrentBookingSession;
import com.app.ace_taxi_v2.Logic.Service.CurrentShiftStatus;
import com.app.ace_taxi_v2.Models.Jobs.TodayBooking;
import com.app.ace_taxi_v2.R;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class TodayJobAdapter extends RecyclerView.Adapter<TodayJobAdapter.ViewHolder> {

    private final List<TodayBooking> jobList;
    private final OnItemClickListener listener;
    private final Context context;
    int statusBookingId = 0;

    public TodayJobAdapter(Context context, List<TodayBooking> jobList, OnItemClickListener listener) {
        this.context = context;
        this.jobList = jobList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.today_job_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TodayBooking job = jobList.get(position);
        holder.bind(job, listener, position); // Pass position to bind method
    }

    @Override
    public int getItemCount() {
        return jobList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView customerTextView;
        private final TextView mainAddressTextView;
        private final TextView subAddressTextView;
        private final MaterialButton viewButton;
        private final MaterialButton startButton;
        private final ImageView clockIcon;
        private final ImageView personIcon;
        private final TextView price;
        private final TextView pickupAddress, destinationAddress;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            customerTextView = itemView.findViewById(R.id.customer);
            mainAddressTextView = itemView.findViewById(R.id.mainAddressTextView);
            subAddressTextView = itemView.findViewById(R.id.subAddressTextView);
            viewButton = itemView.findViewById(R.id.viewButton);
            startButton = itemView.findViewById(R.id.startButton);
            clockIcon = itemView.findViewById(R.id.clockIcon);
            personIcon = itemView.findViewById(R.id.person);
            price = itemView.findViewById(R.id.price);
            pickupAddress = itemView.findViewById(R.id.pickupAddress);
            destinationAddress = itemView.findViewById(R.id.destinationAddress);
        }

        public void bind(TodayBooking job, OnItemClickListener listener, int position) { // Added position parameter
            try {
                // Address parsing with null checks
                String pickup = job.getPickupAddress() != null ? job.getPickupAddress() : "";
                String[] pickupParts = pickup.split(",");
                String firstPickup = pickupParts.length > 0 ? pickupParts[0].trim() : "";
                String lastPickup = pickupParts.length > 1 ? pickupParts[1].trim() + (job.getPickupPostCode() != null ? job.getPickupPostCode() : "")
                        : (job.getPickupPostCode() != null ? job.getPickupPostCode() : "");

                String destination = job.getDestinationAddress() != null ? job.getDestinationAddress() : "";
                String[] destinationParts = destination.split(",");
                String firstDestination = destinationParts.length > 0 ? destinationParts[0].trim() : "";
                String lastDestination = destinationParts.length > 1 ? destinationParts[1].trim() + (job.getDestinationPostCode() != null ? job.getDestinationPostCode() : "")
                        : (job.getDestinationPostCode() != null ? job.getDestinationPostCode() : "");

                // Set basic UI elements
                customerTextView.setText(String.valueOf(job.getPassengers()));
                mainAddressTextView.setText(firstPickup);
                subAddressTextView.setText(firstDestination);
                price.setText("£" + job.getPrice());
                pickupAddress.setText(lastPickup);
                destinationAddress.setText(lastDestination);

                BookingStartStatus bookingStartStatus = new BookingStartStatus(context);

                // Initial button state check

                // Check job status and update button
                GetBookingInfoApi getBookingInfoApi = new GetBookingInfoApi(context);
                getBookingInfoApi.getInfo(job.getBookingId(), new GetBookingInfoApi.BookingCallback() {
                    @Override
                    public void onSuccess(com.app.ace_taxi_v2.Models.Jobs.GetBookingInfo bookingInfo) {
                        try {
                            CurrentBookingSession currentBookingSession = new CurrentBookingSession(context);
                            int bookingId = -1;
                            try {
                                String id = currentBookingSession.getBookingId();
                                if (id != null && !id.isEmpty()) {
                                    bookingId = Integer.parseInt(id);
                                }
                            } catch (NumberFormatException e) {
                                Log.e("TodayJobAdapter", "Error parsing current booking ID", e);
                            }

                            if (job.getBookingId() == bookingId) {
                                startButton.setText("Started");
                                startButton.setBackgroundColor(ContextCompat.getColor(context, R.color.dark_green));
                            }

                            String status = bookingInfo.getStatus();
                            if ("3".equals(status)) {
                                bookingStartStatus.clearBookingId();
                                startButton.setText("Completed");
                                startButton.setBackgroundColor(ContextCompat.getColor(context, R.color.primaryColor));
                                startButton.setEnabled(false);
                            } else if ("2".equals(status)) {
                                bookingStartStatus.clearBookingId();
                                startButton.setText("Rejected");
                                startButton.setBackgroundColor(ContextCompat.getColor(context, R.color.primaryColor));
                                startButton.setEnabled(false);
                            } else if ("1".equals(status)) {
                                startButton.setText("Start");
                                try {
                                    statusBookingId = bookingStartStatus.getBookingId() != null ?
                                            Integer.parseInt(bookingStartStatus.getBookingId()) : -1;
                                    if (statusBookingId == job.getBookingId()) {
                                        startButton.setText("Active");
                                        startButton.setBackgroundColor(ContextCompat.getColor(context, R.color.dark_blue));
                                    }
                                } catch (NumberFormatException e) {
                                    Log.e("TodayJobAdapter", "Invalid booking ID format", e);
                                }
                            }
                        } catch (Exception e) {
                            Log.e("TodayJobAdapter", "Error processing booking status", e);
                        }
                    }

                    @Override
                    public void onfailer(String error) {
                        Log.e("TodayJobAdapter", "Failed to fetch booking info: " + error);
                        Toast.makeText(context, "Failed to load booking status", Toast.LENGTH_SHORT).show();
                    }
                });

                // Setup click listeners
                viewButton.setOnClickListener(v -> {
                    listener.onViewClick(job);
                    notifyItemChanged(position); // Rerender this item after view button click
                });

                startButton.setOnClickListener(v -> {
                    Log.e("Status and Id", "Status and Id : " + job.getStatus() + " " + job.getBookingId() + " bookingStart Status: " + bookingStartStatus.getBookingId());

                    try {
                        CurrentShiftStatus currentShiftStatus = new CurrentShiftStatus(context);
                        int activeBookingId = -1; // Default value if no active booking exists

                        // Check if bookingStartStatus has a valid ID before parsing
                        if (bookingStartStatus != null && bookingStartStatus.getBookingId() != null && !bookingStartStatus.getBookingId().isEmpty()) {
                            try {
                                activeBookingId = Integer.parseInt(bookingStartStatus.getBookingId());
                            } catch (NumberFormatException e) {
                                Log.e("TodayJobAdapter", "Error parsing active booking ID", e);
                            }
                        }

                        if (job.getStatus() == null || "0".equals(job.getStatus())) {
                            JobModal jobModal = new JobModal(context);
                            jobModal.jobOfferModalForTodayJob(job.getBookingId());
                        }

                        if (activeBookingId != -1 && activeBookingId != job.getBookingId()) {
                            Toast.makeText(context, "Please complete the current booking first", Toast.LENGTH_LONG).show();
                            return;
                        }

                        if (bookingStartStatus.getBookingId() == null && "1".equals(job.getStatus())) {
                            bookingStartStatus.setBookingId(String.valueOf(job.getBookingId()));
                        }

//                        if (!"onShift".equals(currentShiftStatus.getStatus())) {
//                            Toast.makeText(context, "Start Your Shift", Toast.LENGTH_LONG).show();
//                            Log.d("Current Driver shift: ", " " + currentShiftStatus.getStatus());
//                            return;
//                        }

                        // Set booking ID and start job if no active booking exists
                        if (activeBookingId == job.getBookingId()) {
                            bookingStartStatus.setBookingId(String.valueOf(job.getBookingId()));
                            listener.onStartClick(job);
                        }

                        notifyItemChanged(position); // Rerender this item after start button click

                    } catch (Exception e) {
                        Log.e("TodayJobAdapter", "Error handling start button click", e);
                        Toast.makeText(context, "Error starting job", Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                Log.e("TodayJobAdapter", "Error binding job data", e);
                Toast.makeText(context, "Error loading job details", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public interface OnItemClickListener {
        void onViewClick(TodayBooking job);
        void onStartClick(TodayBooking job);
    }
}