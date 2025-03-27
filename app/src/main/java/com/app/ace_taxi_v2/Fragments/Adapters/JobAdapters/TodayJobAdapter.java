package com.app.ace_taxi_v2.Fragments.Adapters.JobAdapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.app.ace_taxi_v2.Logic.Service.CurrentBookingSession;
import com.app.ace_taxi_v2.Models.Jobs.TodayBooking;
import com.app.ace_taxi_v2.R;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TodayJobAdapter extends RecyclerView.Adapter<JobViewHolder> {
    private final List<TodayBooking> jobList;
    private final OnItemClickListener listener;
    private final Context context;
    private final CurrentBookingSession currentBookingSession;

    public TodayJobAdapter(Context context, List<TodayBooking> jobList, OnItemClickListener listener) {
        this.context = context;
        this.jobList = jobList;
        this.listener = listener;
        this.currentBookingSession = new CurrentBookingSession(context);
        sortBookings(); // Sort initially
    }

    @NonNull
    @Override
    public JobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.today_job_item, parent, false);
        return new JobViewHolder(view, context, listener, this);
    }

    @Override
    public void onBindViewHolder(@NonNull JobViewHolder holder, int position) {
        TodayBooking job = jobList.get(position);
        holder.bind(job, position);
    }

    @Override
    public int getItemCount() {
        return jobList.size();
    }

    // Method to sort bookings: started bookings on top, others below
    public void sortBookings() {
        Collections.sort(jobList, new Comparator<TodayBooking>() {
            @Override
            public int compare(TodayBooking job1, TodayBooking job2) {
                boolean isJob1Started = isBookingStarted(job1);
                boolean isJob2Started = isBookingStarted(job2);

                // If both are started or both are not started, maintain their relative order
                if (isJob1Started == isJob2Started) {
                    return 0;
                }
                // Started bookings come first
                return isJob1Started ? -1 : 1;
            }
        });
        notifyDataSetChanged(); // Notify the adapter to refresh the entire list
    }

    // Helper method to determine if a booking has started
    private boolean isBookingStarted(TodayBooking job) {
        // Check if the booking ID matches the current booking ID in the session
        String currentBookingId = currentBookingSession.getBookingId();
        int activeBookingId = currentBookingId != null && !currentBookingId.isEmpty()
                ? Integer.parseInt(currentBookingId) : -1;

        // A booking is considered "started" if its ID matches the active booking ID
        // or if its status indicates it's in progress (e.g., status "1" or specific shift states)
        if (job.getBookingId() == activeBookingId) {
            return true;
        }

        // Additionally, check the status and shift state
        String status = job.getStatus();
        String bookingShift = currentBookingSession.getBookingShift();
        return "1".equals(status) || (bookingShift != null &&
                (bookingShift.equals("3003") || // On Route
                        bookingShift.equals("3004") || // On Pickup
                        bookingShift.equals("3005") || // POB
                        bookingShift.equals("3006"))); // STC
    }

    // Method to update the list and re-sort (e.g., after a booking starts)
    public void updateAndSort() {
        sortBookings();
    }

    public interface OnItemClickListener {
        void onViewClick(TodayBooking job);
        void onStartClick(TodayBooking job);
    }
}