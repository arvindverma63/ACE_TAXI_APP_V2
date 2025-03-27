package com.app.ace_taxi_v2.Fragments.Adapters.JobAdapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.app.ace_taxi_v2.Components.BookingStartStatus;
import com.app.ace_taxi_v2.JobModals.JobModal;
import com.app.ace_taxi_v2.Logic.GetBookingInfoApi;
import com.app.ace_taxi_v2.Logic.Service.CurrentBookingSession;
import com.app.ace_taxi_v2.Logic.Service.CurrentShiftStatus;
import com.app.ace_taxi_v2.Models.Jobs.HistoryBooking;
import com.app.ace_taxi_v2.R;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private final List<HistoryBooking> jobList;
    private final OnItemClickListener listener;
    public Context context;

    public HistoryAdapter(List<HistoryBooking> jobList,Context context ,OnItemClickListener listener) {
        this.jobList = jobList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.history_job_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HistoryBooking job = jobList.get(position);
        holder.bind(job, listener,context);
    }

    @Override
    public int getItemCount() {
        return jobList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView customerTextView;
        private final TextView mainAddressTextView;
        private final TextView subAddressTextView;
        private final MaterialButton viewButton;
        private final MaterialButton startButton;
        private final TextView price;
        private final TextView pickupAddress, destinationAddress;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            customerTextView = itemView.findViewById(R.id.customer);
            mainAddressTextView = itemView.findViewById(R.id.mainAddressTextView);
            subAddressTextView = itemView.findViewById(R.id.subAddressTextView);
            viewButton = itemView.findViewById(R.id.viewButton);
            startButton = itemView.findViewById(R.id.startButton);
            price = itemView.findViewById(R.id.price);
            pickupAddress = itemView.findViewById(R.id.pickupAddress);
            destinationAddress = itemView.findViewById(R.id.destinationAddress);
        }

        public void bind(HistoryBooking job, OnItemClickListener listener,Context context) {
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

                viewButton.setOnClickListener(v -> {
                    JobModal jobModal = new JobModal(context);
                    jobModal.JobViewForFutureAndHistory(job.getBookingId());
                });
                startButton.setVisibility(View.GONE);


            } catch (Exception e) {
                Log.e("TodayJobAdapter", "Error binding job data", e);

            }
        }
    }

    public interface OnItemClickListener {
        void onViewClick(HistoryBooking job);
        void onStartClick(HistoryBooking job);
    }
}
