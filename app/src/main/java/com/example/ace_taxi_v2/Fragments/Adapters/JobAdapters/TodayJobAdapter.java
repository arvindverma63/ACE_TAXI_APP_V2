package com.example.ace_taxi_v2.Fragments.Adapters.JobAdapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ace_taxi_v2.Models.Jobs.TodayBooking;
import com.example.ace_taxi_v2.R;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class TodayJobAdapter extends RecyclerView.Adapter<TodayJobAdapter.ViewHolder> {

    private final List<TodayBooking> jobList;
    private final OnItemClickListener listener;

    public TodayJobAdapter(List<TodayBooking> jobList, OnItemClickListener listener) {
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

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView timeTextView;
        private final TextView customerTextView;
        private final TextView mainAddressTextView;
        private final TextView subAddressTextView;
        private final MaterialButton viewButton;
        private final MaterialButton startButton;
        private final ImageView clockIcon;
        private final ImageView personIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            timeTextView = itemView.findViewById(R.id.timeTextView);
            customerTextView = itemView.findViewById(R.id.customer);
            mainAddressTextView = itemView.findViewById(R.id.mainAddressTextView);
            subAddressTextView = itemView.findViewById(R.id.subAddressTextView);
            viewButton = itemView.findViewById(R.id.viewButton);
            startButton = itemView.findViewById(R.id.startButton);
            clockIcon = itemView.findViewById(R.id.clockIcon);
            personIcon = itemView.findViewById(R.id.person);
        }

        public void bind(TodayBooking job, OnItemClickListener listener) {
            timeTextView.setText(job.getPickupDateTime());
            customerTextView.setText(String.valueOf(job.getPassengers()));
            mainAddressTextView.setText(job.getPickupAddress());
            subAddressTextView.setText(job.getDestinationAddress());

            viewButton.setOnClickListener(v -> listener.onViewClick(job));
            startButton.setOnClickListener(v -> listener.onStartClick(job));
        }
    }

    public interface OnItemClickListener {
        void onViewClick(TodayBooking job);

        void onStartClick(TodayBooking job);
    }
}
