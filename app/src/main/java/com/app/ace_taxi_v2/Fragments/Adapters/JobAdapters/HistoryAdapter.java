package com.app.ace_taxi_v2.Fragments.Adapters.JobAdapters;

import static android.content.ContentValues.TAG;

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
import com.app.ace_taxi_v2.GoogleMap.StringtoMap;
import com.app.ace_taxi_v2.JobModals.JobViewDialog;
import com.app.ace_taxi_v2.Logic.Formater.HHMMFormater;
import com.app.ace_taxi_v2.Logic.GetBookingInfoApi;
import com.app.ace_taxi_v2.Logic.Service.CurrentBookingSession;
import com.app.ace_taxi_v2.Logic.Service.CurrentShiftStatus;
import com.app.ace_taxi_v2.Models.Jobs.HistoryBooking;
import com.app.ace_taxi_v2.R;
import com.google.android.material.button.MaterialButton;

import java.lang.ref.WeakReference;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private final List<HistoryBooking> jobList;
    private final OnItemClickListener listener;
    private static WeakReference<Context> contextRef;
    public static StringtoMap openmap;

    public HistoryAdapter(List<HistoryBooking> jobList, Context context, OnItemClickListener listener) {
        this.jobList = jobList;
        this.contextRef = new WeakReference<>(context);
        this.listener = listener;
        openmap = new StringtoMap(context);
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
        holder.bind(job, listener);
    }

    @Override
    public int getItemCount() {
        return jobList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView mainAddressTextView;
        private final TextView subAddressTextView;
        private final MaterialButton viewButton;
        private final TextView price;
        private final TextView pickupTime,destinationTime,distance_duration;
        private final TextView pickupAddress, destinationAddress, pickup_location, datetime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mainAddressTextView = itemView.findViewById(R.id.mainAddressTextView);
            subAddressTextView = itemView.findViewById(R.id.subAddressTextView);
            viewButton = itemView.findViewById(R.id.viewButton);
            price = itemView.findViewById(R.id.price);
            pickupAddress = itemView.findViewById(R.id.pickupAddress);
            destinationAddress = itemView.findViewById(R.id.destinationAddress);
            pickup_location = itemView.findViewById(R.id.pickup_location);
            datetime = itemView.findViewById(R.id.pickup_datetime);
            pickupTime = itemView.findViewById(R.id.pickup_time);
            destinationTime = itemView.findViewById(R.id.destination_time);
            distance_duration = itemView.findViewById(R.id.distance_duration);
        }

        public void bind(HistoryBooking job, OnItemClickListener listener) {
            Context context = contextRef.get();
            if (context == null) return;

            try {
                // Address parsing with null checks
                String pickup = job.getPickupAddress() != null ? job.getPickupAddress() : "";
                String[] pickupParts = pickup.split(",");
                String firstPickup = pickupParts.length > 0 ? pickupParts[0].trim() : "";
                String lastPickup = pickupParts.length > 1 ? pickupParts[1].trim() : "";
                if (job.getPickupPostCode() != null) {
                    lastPickup += " " + job.getPickupPostCode();
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

                mainAddressTextView.setOnClickListener(v -> {
                    openmap.openGoogleMaps(job.getPickupAddress());
                });
                subAddressTextView.setOnClickListener(v -> {
                    openmap.openGoogleMaps(job.getDestinationAddress());
                });
                distance_duration.setText(job.getMileage() + " Miles");
                HHMMFormater formater = new HHMMFormater();
                pickupTime.setText(formater.formatDateTime(job.getPickupDateTime()));
                if(job.getArriveBy()== null){
                    destinationTime.setVisibility(View.GONE);
                }else {
                    destinationTime.setText(formater.formatDateTime(job.getArriveBy()));
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


            } catch (Exception e) {
                Log.e("HistoryAdapter", "Error binding job data", e);
                Toast.makeText(context, "Error loading job data", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public interface OnItemClickListener {
        void onViewClick(HistoryBooking job);
        void onStartClick(HistoryBooking job);
    }
}