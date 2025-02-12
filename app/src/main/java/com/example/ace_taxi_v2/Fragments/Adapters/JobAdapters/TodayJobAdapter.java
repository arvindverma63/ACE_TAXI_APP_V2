package com.example.ace_taxi_v2.Fragments.Adapters.JobAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ace_taxi_v2.Logic.GetBookingInfoApi;
import com.example.ace_taxi_v2.Models.Jobs.TodayBooking;
import com.example.ace_taxi_v2.R;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class TodayJobAdapter extends RecyclerView.Adapter<TodayJobAdapter.ViewHolder> {

    private final List<TodayBooking> jobList;
    private final OnItemClickListener listener;
    private final Context context;

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
        holder.bind(job, listener);
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
        private final TextView pickupAddress,destinationAddress;

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

        public void bind(TodayBooking job, OnItemClickListener listener) {
            String pickup = job.getPickupAddress();
            String[] pickupParts = pickup.split(",");
            String firstPickup = pickupParts.length > 0 ? pickupParts[0].trim() : "";
            String lastPickup = pickupParts.length > 1 ? pickupParts[1].trim() : "";

            String destination = job.getDestinationAddress();
            String[] destinationParts = destination.split(",");
            String firstDestination = destinationParts.length > 0 ? destinationParts[0].trim() : "";
            String lastDestination = destinationParts.length > 1 ? destinationParts[1].trim() : "";

            customerTextView.setText(String.valueOf(job.getPassengers()));
            mainAddressTextView.setText(firstPickup);
            subAddressTextView.setText(firstDestination);
            price.setText("£" + job.getPrice());

            pickupAddress.setText(lastPickup);
            destinationAddress.setText(lastDestination);


            // Check job status on load and update button accordingly
            GetBookingInfoApi getBookingInfoApi = new GetBookingInfoApi(context);
            getBookingInfoApi.getInfo(job.getBookingId(), new GetBookingInfoApi.BookingCallback() {
                @Override
                public void onSuccess(com.example.ace_taxi_v2.Models.Jobs.GetBookingInfo bookingInfo) {
                    if ("3".equals(bookingInfo.getStatus())) {
                        startButton.setText("Completed");
                        startButton.setBackgroundColor(ContextCompat.getColor(context, R.color.primaryColor));
                        startButton.setEnabled(false); // Disable button after completion
                    }
                    if ("2".equals(bookingInfo.getStatus())) {
                        startButton.setText("Rejected");
                        startButton.setBackgroundColor(ContextCompat.getColor(context, R.color.primaryColor));
                        startButton.setEnabled(false); // Disable button after completion
                    }

                }

                @Override
                public void onfailer(String error) {
                    // Handle failure (optional logging or error message)
                }
            });

            viewButton.setOnClickListener(v -> listener.onViewClick(job));

            startButton.setOnClickListener(v -> {
                listener.onStartClick(job);

                // Update button dynamically when job is completed
                getBookingInfoApi.getInfo(job.getBookingId(), new GetBookingInfoApi.BookingCallback() {
                    @Override
                    public void onSuccess(com.example.ace_taxi_v2.Models.Jobs.GetBookingInfo bookingInfo) {
                        if ("3".equals(bookingInfo.getStatus())) {
                            startButton.setText("Completed");
                            startButton.setBackgroundColor(ContextCompat.getColor(context, R.color.primaryColor));
                            startButton.setEnabled(false);
                        }
                        if ("2".equals(bookingInfo.getStatus())) {
                            startButton.setText("Rejected");
                            startButton.setBackgroundColor(ContextCompat.getColor(context, R.color.primaryColor));
                            startButton.setEnabled(false); // Disable button after completion
                        }
                    }

                    @Override
                    public void onfailer(String error) {
                        // Handle failure scenario
                    }
                });
            });
        }
    }

    public interface OnItemClickListener {
        void onViewClick(TodayBooking job);

        void onStartClick(TodayBooking job);
    }
}