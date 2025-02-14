package com.app.ace_taxi_v2.Fragments.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.ace_taxi_v2.Logic.DeleteAvailbility;
import com.app.ace_taxi_v2.Models.AvailabilityResponse;
import com.app.ace_taxi_v2.R;

import java.util.List;

public class AvailablitiesAdapter extends RecyclerView.Adapter<AvailablitiesAdapter.ViewHolder> {

    private final List<AvailabilityResponse.Driver> list;
    public Context context;

    public AvailablitiesAdapter(List<AvailabilityResponse.Driver> list,Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public AvailablitiesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.availablities_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AvailablitiesAdapter.ViewHolder holder, int position) {
        AvailabilityResponse.Driver response = list.get(position);

        holder.tv_driver_name.setText(response.getFullName());

        // Convert availability type integer to String
        holder.tv_availability_type.setText("Availability Type: " + response.getAvailabilityType());

        // Show available hours instead of description
        holder.tv_availability_details.setText(response.getAvailableHours());

        DeleteAvailbility deleteAvailbility = new DeleteAvailbility(context);

        holder.deleteIcon.setOnClickListener(v -> {
            deleteAvailbility.deleteAva(response.getId());

            // Remove the item from the list
            list.remove(position);

            // Notify RecyclerView about item removal
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, list.size()); // Refresh positions
        });
    }


    @Override
    public int getItemCount() {
        return (list != null) ? list.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_driver_name, tv_availability_type, tv_availability_details;
        ImageView deleteIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_driver_name = itemView.findViewById(R.id.tv_driver_name);
            tv_availability_details = itemView.findViewById(R.id.tv_availability_details);
            tv_availability_type = itemView.findViewById(R.id.tv_availability_type);
            deleteIcon = itemView.findViewById(R.id.deleteIcon);
        }
    }
}
