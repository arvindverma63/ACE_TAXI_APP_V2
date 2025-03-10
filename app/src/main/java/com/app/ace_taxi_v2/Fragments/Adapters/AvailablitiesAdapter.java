package com.app.ace_taxi_v2.Fragments.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
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

        holder.fromToEnd.setText(response.getAvailableHours());

        if(response.getAvailabilityType() == 0){
            holder.availText.setText("UnAvailable");
            holder.pinIcon.setImageTintList(ContextCompat.getColorStateList(context,R.color.red));
        }else{
            holder.availText.setText("Available");
            holder.pinIcon.setImageTintList(ContextCompat.getColorStateList(context,R.color.green));
        }


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
        TextView fromToEnd,availText;
        ImageView deleteIcon,pinIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fromToEnd = itemView.findViewById(R.id.fromToEnd);
            deleteIcon = itemView.findViewById(R.id.delete_icon);
            availText = itemView.findViewById(R.id.availText);
            pinIcon = itemView.findViewById(R.id.pin_icon);
        }
    }
}
