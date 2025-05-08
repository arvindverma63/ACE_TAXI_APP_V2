package com.app.ace_taxi_v2.Fragments.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.ace_taxi_v2.Models.AllDriverAvailabilityResponse;
import com.app.ace_taxi_v2.R;

import java.util.ArrayList;
import java.util.List;

public class AllDriverAvailAdapter extends RecyclerView.Adapter<AllDriverAvailAdapter.ViewHolder> {

    private List<AllDriverAvailabilityResponse> list; // Removed 'final' to allow updates
    private final Context context;
    private static final String TAG = "AllDriverAvailAdapter";

    public AllDriverAvailAdapter(Context context, List<AllDriverAvailabilityResponse> list) {
        this.context = context;
        this.list = list != null ? list : new ArrayList<>(); // Ensure list is never null
        Log.d(TAG, "Adapter initialized with list size: " + this.list.size());
    }

    // Method to update the data
    public void updateData(List<AllDriverAvailabilityResponse> newList) {
        this.list.clear();
        if (newList != null) {
            this.list.addAll(newList);
        }
        notifyDataSetChanged();
        Log.d(TAG, "Data updated, new list size: " + this.list.size());
    }

    @NonNull
    @Override
    public AllDriverAvailAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_driver_avail, parent, false);
        Log.d(TAG, "ViewHolder created for viewType: " + viewType);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AllDriverAvailAdapter.ViewHolder holder, int position) {
        AllDriverAvailabilityResponse response = list.get(position);
        Log.d(TAG, "Binding data at position: " + position + ", userId: " + response.getUserId());

        holder.tvUserId.setText(String.valueOf(response.getUserId()));
        holder.tvFullName.setText(response.getFullName());
        holder.tvVehicleType.setText(response.getVehicleType() == 0 ? "Standard" : "Other");

        List<AllDriverAvailabilityResponse.AvailabilityHour> availableHours = response.getAvailableHours();
        if (availableHours != null && !availableHours.isEmpty()) {
            Log.d(TAG, "Binding available hours for userId: " + response.getUserId() + ", count: " + availableHours.size());
            if (availableHours.size() >= 1) {
                AllDriverAvailabilityResponse.AvailabilityHour hour1 = availableHours.get(0);
                holder.btnAvailableHours1.setText(String.format("%s - %s (%s)",
                        hour1.getFrom(), hour1.getTo(), hour1.getNote()));
                holder.btnAvailableHours1.setVisibility(View.VISIBLE);
                Log.d(TAG, "Set first availability slot: " + hour1.getFrom() + " - " + hour1.getTo());
            } else {
                holder.btnAvailableHours1.setVisibility(View.GONE);
                Log.d(TAG, "Hiding first availability slot for userId: " + response.getUserId());
            }

            if (availableHours.size() >= 2) {
                AllDriverAvailabilityResponse.AvailabilityHour hour2 = availableHours.get(1);
                holder.btnAvailableHours2.setText(String.format("%s - %s (%s)",
                        hour2.getFrom(), hour2.getTo(), hour2.getNote()));
                holder.btnAvailableHours2.setVisibility(View.VISIBLE);
                Log.d(TAG, "Set second availability slot: " + hour2.getFrom() + " - " + hour2.getTo());
            } else {
                holder.btnAvailableHours2.setVisibility(View.GONE);
                Log.d(TAG, "Hiding second availability slot for userId: " + response.getUserId());
            }
        } else {
            holder.btnAvailableHours1.setVisibility(View.GONE);
            holder.btnAvailableHours2.setVisibility(View.GONE);
            Log.d(TAG, "No available hours for userId: " + response.getUserId());
        }

        // Ensure the color is in the correct ARGB format
        String colorCode = response.getColorCode().toUpperCase();
        if (colorCode.length() == 9) {
            // Convert #RRGGBBAA to #AARRGGBB
            colorCode = "#" + colorCode.substring(7, 9) + colorCode.substring(1, 7);
        } else if (colorCode.length() == 7) {
            // Add full opacity if missing
            colorCode = "#FF" + colorCode.substring(1);
        }


        holder.itemView.setBackgroundColor(android.graphics.Color.parseColor(colorCode));
        Log.d(TAG, "Set background color: " + response.getColorCode() + " for userId: " + response.getUserId());
    }

    @Override
    public int getItemCount() {
        int count = list != null ? list.size() : 0;
        Log.d(TAG, "Item count: " + count);
        return count;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserId, tvFullName, tvVehicleType;
        Button btnAvailableHours1, btnAvailableHours2;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserId = itemView.findViewById(R.id.tv_user_id);
            tvFullName = itemView.findViewById(R.id.tv_full_name);
            tvVehicleType = itemView.findViewById(R.id.tv_vehicle_type);
            btnAvailableHours1 = itemView.findViewById(R.id.btn_available_hours_1);
            btnAvailableHours2 = itemView.findViewById(R.id.btn_available_hours_2);
            Log.d(TAG, "ViewHolder initialized for itemView");
        }
    }
}