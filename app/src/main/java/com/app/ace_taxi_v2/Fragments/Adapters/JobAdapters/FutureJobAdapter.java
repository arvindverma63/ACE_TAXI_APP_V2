package com.app.ace_taxi_v2.Fragments.Adapters.JobAdapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.app.ace_taxi_v2.Components.DeleteDialog;
import com.app.ace_taxi_v2.JobModals.JobModal;
import com.app.ace_taxi_v2.Models.Jobs.Booking;
import com.app.ace_taxi_v2.Models.Jobs.HistoryBooking;
import com.app.ace_taxi_v2.R;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class FutureJobAdapter extends RecyclerView.Adapter<FutureJobAdapter.ViewHolder> {

    private final List<Booking> bookingList;
    private final OnItemClickListener listener;
    public Context context;
    // Constructor
    public FutureJobAdapter(List<Booking> bookingList,Context context, OnItemClickListener listener) {
        this.bookingList = bookingList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.future_job_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Bind data to the ViewHolder
        Booking booking = bookingList.get(position);
        holder.bind(booking, listener,context);
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    // ViewHolder Class
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

        public void bind(Booking job, FutureJobAdapter.OnItemClickListener listener, Context context) {
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

                if("2".equals(job.getStatus())){
                    startButton.setText("Rejected");
                }else {
                    startButton.setOnClickListener(v -> {
                        DeleteDialog deleteDialog = new DeleteDialog(context);
                        deleteDialog.rejectBooking(job.getBookingId());
                    });
                }
                if("Account".equals(job.getScopeText())){
                    price.setText("ACC");
                    price.setTextColor(ContextCompat.getColor(context,R.color.red));
                }
            } catch (Exception e) {
                Log.e("TodayJobAdapter", "Error binding job data", e);

            }
        }
    }

    // Listener Interface for button clicks
    public interface OnItemClickListener {
        void onViewClick(Booking booking);
        void onStartClick(Booking booking);
    }


}
