package com.app.ace_taxi_v2.Fragments.Adapters.JobAdapters;

import static android.content.ContentValues.TAG;

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
import com.app.ace_taxi_v2.JobModals.JobViewDialog;
import com.app.ace_taxi_v2.Models.Jobs.Booking;
import com.app.ace_taxi_v2.Models.Jobs.HistoryBooking;
import com.app.ace_taxi_v2.R;
import com.google.android.material.button.MaterialButton;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

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

        private final TextView mainAddressTextView;
        private final TextView subAddressTextView;
        private final MaterialButton viewButton;
        private final MaterialButton startButton;
        private final TextView price,datetime;
        private final TextView pickupAddress, destinationAddress,pickup_location;
        private final TextView pickupTime,destinationTime,distance_duration;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mainAddressTextView = itemView.findViewById(R.id.mainAddressTextView);
            subAddressTextView = itemView.findViewById(R.id.subAddressTextView);
            viewButton = itemView.findViewById(R.id.viewButton);
            startButton = itemView.findViewById(R.id.startButton);
            price = itemView.findViewById(R.id.price);
            pickupAddress = itemView.findViewById(R.id.pickupAddress);
            destinationAddress = itemView.findViewById(R.id.destinationAddress);
            pickup_location = itemView.findViewById(R.id.pickup_location);
            datetime = itemView.findViewById(R.id.pickup_datetime);
            pickupTime = itemView.findViewById(R.id.pickup_time);
            destinationTime = itemView.findViewById(R.id.destination_time);
            distance_duration = itemView.findViewById(R.id.distance_duration);
        }

        public void bind(Booking job, FutureJobAdapter.OnItemClickListener listener, Context context) {
            try {
                // Address parsing with null checks
                String pickup = job.getPickupAddress() != null ? job.getPickupAddress() : "";
                String[] pickupParts = pickup.split(",");
                String firstPickup = pickupParts.length > 0 ? pickupParts[0].trim() : "";
                String lastPickup = pickupParts.length > 1 ? pickupParts[1].trim() : "";
                if (job.getPickupPostCode() != null) {
                    lastPickup += " " + job.getPickupPostCode();
                }
                distance_duration.setText(job.getMileage() + " Miles");
                String pickupTimeText = job.getPickupDateTime();
                String[] parts = pickupTimeText.split(",");
                pickupTime.setText(parts[parts.length - 1]);
                if(job.getArriveBy()== null){
                    destinationTime.setVisibility(View.GONE);
                }else {
                    destinationTime.setText(job.getArriveBy());
                }
                mainAddressTextView.setText(firstPickup.isEmpty() ? "N/A" : firstPickup);
                pickupAddress.setText(lastPickup.isEmpty() ? "N/A" : lastPickup);

                String destination = job.getDestinationAddress() != null ? job.getDestinationAddress() : "";
                String[] destinationParts = destination.split(",");
                String firstDestination = destinationParts.length > 0 ? destinationParts[0].trim() : "";
                String lastDestination = destinationParts.length > 1 ? destinationParts[1].trim() : "";
                if (job.getDestinationPostCode() != null) {
                    lastDestination += " " + job.getDestinationPostCode();
                }
                destinationAddress.setText(lastDestination.isEmpty() ? "N/A" : lastDestination);
                subAddressTextView.setText(firstDestination.isEmpty() ? "N/A" : firstDestination);
                pickup_location.setText(firstPickup);

                Log.d(TAG,"pickupdatetime history jobs: "+job.getPickupDateTime());
                datetime.setText(job.getFormattedDateTime());
                NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.UK);
                price.setText(formatter.format(job.getPrice()));
                if ("Account".equals(job.getScopeText())) {

                }

                viewButton.setOnClickListener(v -> {
                    if (listener != null) listener.onViewClick(job);
                    JobViewDialog jobModal = new JobViewDialog(context);
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
