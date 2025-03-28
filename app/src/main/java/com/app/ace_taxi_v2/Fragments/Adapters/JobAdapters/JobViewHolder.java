package com.app.ace_taxi_v2.Fragments.Adapters.JobAdapters;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.app.ace_taxi_v2.Components.BookingStartStatus;
import com.app.ace_taxi_v2.JobModals.JobModal;
import com.app.ace_taxi_v2.Logic.GetBookingInfoApi;
import com.app.ace_taxi_v2.Logic.Service.CurrentBookingSession;
import com.app.ace_taxi_v2.Models.Jobs.TodayBooking;
import com.app.ace_taxi_v2.R;
import com.google.android.material.button.MaterialButton;

public class JobViewHolder extends RecyclerView.ViewHolder {
    private final TextView customerTextView;
    private final TextView mainAddressTextView;
    private final TextView subAddressTextView;
    private final MaterialButton viewButton;
    private final MaterialButton startButton;
    private final TextView price;
    private final TextView pickupAddress;
    private final TextView destinationAddress;
    private final Context context;
    private final TodayJobAdapter.OnItemClickListener listener;
    private final BookingStatusHandler statusHandler;
    private final TodayJobAdapter adapter; // Reference to the adapter

    public JobViewHolder(View itemView, Context context, TodayJobAdapter.OnItemClickListener listener, TodayJobAdapter adapter) {
        super(itemView);
        this.context = context;
        this.listener = listener;
        this.statusHandler = new BookingStatusHandler(context);
        this.adapter = adapter;

        customerTextView = itemView.findViewById(R.id.customer);
        mainAddressTextView = itemView.findViewById(R.id.mainAddressTextView);
        subAddressTextView = itemView.findViewById(R.id.subAddressTextView);
        viewButton = itemView.findViewById(R.id.viewButton);
        startButton = itemView.findViewById(R.id.startButton);
        price = itemView.findViewById(R.id.price);
        pickupAddress = itemView.findViewById(R.id.pickupAddress);
        destinationAddress = itemView.findViewById(R.id.destinationAddress);
    }

    public void bind(TodayBooking job, int position) {
        try {
            setupAddressViews(job);
            setupBasicInfo(job);
            setupButtonListeners(job, position);
            fetchAndUpdateStatus(job, position);
        } catch (Exception e) {
            Log.e("JobViewHolder", "Error binding job data", e);
            Toast.makeText(context, "Error loading job details", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupAddressViews(TodayBooking job) {
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

        mainAddressTextView.setText(firstPickup);
        subAddressTextView.setText(firstDestination);
        pickupAddress.setText(lastPickup);
        destinationAddress.setText(lastDestination);


    }

    private void setupBasicInfo(TodayBooking job) {
        customerTextView.setText(String.valueOf(job.getPassengers()));
        price.setText("£" + job.getPrice());

        if("Account".equals(job.getScopeText())){
            price.setText("ACC");
            price.setTextColor(ContextCompat.getColor(context,R.color.red));
        }
    }

    private void setupButtonListeners(TodayBooking job, int position) {
        viewButton.setOnClickListener(v -> {
            listener.onViewClick(job);
            notifyItemChanged(position);
        });

        startButton.setOnClickListener(v -> handleStartClick(job, position));
    }

    private void fetchAndUpdateStatus(TodayBooking job, int position) {
        GetBookingInfoApi getBookingInfoApi = new GetBookingInfoApi(context);
        getBookingInfoApi.getInfo(job.getBookingId(), new GetBookingInfoApi.BookingCallback() {
            @Override
            public void onSuccess(com.app.ace_taxi_v2.Models.Jobs.GetBookingInfo bookingInfo) {
                statusHandler.updateButtonStatus(startButton, job, bookingInfo.getStatus());
                adapter.updateAndSort(); // Re-sort after status update
            }

            @Override
            public void onfailer(String error) {
                Log.e("JobViewHolder", "Failed to fetch booking info: " + error);
                Toast.makeText(context, "Failed to load booking status", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleStartClick(TodayBooking job, int position) {
        try {
            BookingStartStatus bookingStartStatus = statusHandler.getBookingStartStatus();
            String currentBookingId = bookingStartStatus.getBookingId();
            int activeBookingId = currentBookingId != null && !currentBookingId.isEmpty()
                    ? Integer.parseInt(currentBookingId) : -1;

            if (job.getStatus() == null || "0".equals(job.getStatus())) {
                new JobModal(context).jobOfferModalForTodayJob(job.getBookingId());
            }

            if (activeBookingId != -1 && activeBookingId != job.getBookingId()) {
                Toast.makeText(context, "Please complete the current booking first", Toast.LENGTH_LONG).show();
                return;
            }

            if (bookingStartStatus.getBookingId() == null && "1".equals(job.getStatus())) {
                bookingStartStatus.setBookingId(String.valueOf(job.getBookingId()));
            }

            if (activeBookingId == job.getBookingId()) {
                bookingStartStatus.setBookingId(String.valueOf(job.getBookingId()));
                listener.onStartClick(job);
                startButton.setText("Started");
                notifyItemChanged(position);
                adapter.updateAndSort(); // Re-sort after starting the booking
            }

        } catch (Exception e) {
            Log.e("JobViewHolder", "Error handling start button click", e);
            Toast.makeText(context, "Error starting job", Toast.LENGTH_SHORT).show();
        }
    }

    private void notifyItemChanged(int position) {
        RecyclerView recyclerView = (RecyclerView) itemView.getParent();
        if (recyclerView != null) {
            recyclerView.getAdapter().notifyItemChanged(position);
        }
    }
}