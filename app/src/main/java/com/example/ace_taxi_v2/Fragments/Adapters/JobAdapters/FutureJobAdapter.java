package com.example.ace_taxi_v2.Fragments.Adapters.JobAdapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ace_taxi_v2.Models.Jobs.FutureJob;
import com.example.ace_taxi_v2.R;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class FutureJobAdapter extends RecyclerView.Adapter<FutureJobAdapter.ViewHolder> {

    private final List<FutureJob> futureJobList;
    private final OnItemClickListener listener;

    // Constructor
    public FutureJobAdapter(List<FutureJob> futureJobList, OnItemClickListener listener) {
        this.futureJobList = futureJobList;
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
        FutureJob futureJob = futureJobList.get(position);
        holder.bind(futureJob, listener);
    }

    @Override
    public int getItemCount() {
        return futureJobList.size();
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

        public void bind(FutureJob futureJob, OnItemClickListener listener) {
            // Bind FutureJob data to views
            timeTextView.setText(futureJob.getTime());
            customerCountTextView.setText(String.valueOf(futureJob.getCustomerCount()));
            mainAddressTextView.setText(futureJob.getMainAddress());
            subAddressTextView.setText(futureJob.getSubAddress());

            // Set button click listeners
            viewButton.setOnClickListener(v -> listener.onViewClick(futureJob));
            startButton.setOnClickListener(v -> listener.onStartClick(futureJob));
        }
    }

    // Listener Interface for button clicks
    public interface OnItemClickListener {
        void onViewClick(FutureJob job);
        void onStartClick(FutureJob job);
    }
}
