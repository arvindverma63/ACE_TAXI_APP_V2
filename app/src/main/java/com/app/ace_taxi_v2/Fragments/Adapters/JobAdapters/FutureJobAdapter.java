package com.app.ace_taxi_v2.Fragments.Adapters.JobAdapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.ace_taxi_v2.Models.Jobs.Booking;
import com.app.ace_taxi_v2.R;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class FutureJobAdapter extends RecyclerView.Adapter<FutureJobAdapter.ViewHolder> {

    private final List<Booking> bookingList;
    private final OnItemClickListener listener;

    // Constructor
    public FutureJobAdapter(List<Booking> bookingList, OnItemClickListener listener) {
        this.bookingList = bookingList;
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
        holder.bind(booking, listener);
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    // ViewHolder Class
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView timeTextView;
        private final TextView customerCountTextView;
        private final TextView mainAddressTextView;
        private final TextView subAddressTextView;
        private final MaterialButton viewButton;
        private final MaterialButton startButton;
        private final ImageView clockIcon;
        private final ImageView personIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // Initialize views
            timeTextView = itemView.findViewById(R.id.timeTextView);
            customerCountTextView = itemView.findViewById(R.id.customer);
            mainAddressTextView = itemView.findViewById(R.id.mainAddressTextView);
            subAddressTextView = itemView.findViewById(R.id.subAddressTextView);
            viewButton = itemView.findViewById(R.id.viewButton);
            startButton = itemView.findViewById(R.id.startButton);
            clockIcon = itemView.findViewById(R.id.clockIcon);
            personIcon = itemView.findViewById(R.id.person);
        }

        public void bind(Booking booking, OnItemClickListener listener) {
            // Bind Booking data to views
            timeTextView.setText(booking.getPickupDateTime());
            customerCountTextView.setText(String.valueOf(booking.getPassengers()));
            mainAddressTextView.setText(booking.getPickupAddress());
            subAddressTextView.setText(booking.getDestinationAddress());

            // Set button click listeners
            viewButton.setOnClickListener(v -> listener.onViewClick(booking));
            startButton.setOnClickListener(v -> listener.onStartClick(booking));
        }
    }

    // Listener Interface for button clicks
    public interface OnItemClickListener {
        void onViewClick(Booking booking);
        void onStartClick(Booking booking);
    }


}
