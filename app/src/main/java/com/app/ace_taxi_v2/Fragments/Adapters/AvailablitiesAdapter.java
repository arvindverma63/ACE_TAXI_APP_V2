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
import com.app.ace_taxi_v2.Logic.Formater.HHMMFormater;
import com.app.ace_taxi_v2.Models.AvailabilityResponse;
import com.app.ace_taxi_v2.R;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AvailablitiesAdapter extends RecyclerView.Adapter<AvailablitiesAdapter.ViewHolder> {

    private final List<AvailabilityResponse.Driver> originalList;
    private List<AvailabilityResponse.Driver> list;

    public Context context;

    public AvailablitiesAdapter(List<AvailabilityResponse.Driver> list, Context context) {
        this.context = context;
        this.originalList = new ArrayList<>(list); // full copy to preserve
        this.list = new ArrayList<>(list);
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

        HHMMFormater hhmmFormater = new HHMMFormater();
        holder.datetime.setText(hhmmFormater.formateTimeStampToDateTime(response.getDate()));
        holder.availText.setText(response.getDescription());

        if(response.getAvailabilityType() == 2){
            holder.pinText.setText("UnAvailable");
            holder.pinCard.setBackgroundTintList(ContextCompat.getColorStateList(context,R.color.red));
        }if(response.getAvailabilityType() == 1){
            holder.pinText.setText("Available");
            holder.pinCard.setBackgroundTintList(ContextCompat.getColorStateList(context,R.color.green));
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

    public void updateListForDate(String selectedDate) {
        SimpleDateFormat apiFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat fullFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());

        List<AvailabilityResponse.Driver> filteredList = new ArrayList<>();
        for (AvailabilityResponse.Driver item : originalList) { // use originalList here
            try {
                Date fullDate = fullFormat.parse(item.getDate());
                String onlyDate = apiFormat.format(fullDate);

                if (onlyDate.equals(selectedDate)) {
                    filteredList.add(item);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Collections.sort(filteredList, (a, b) -> {
            try {
                Date date1 = fullFormat.parse(a.getDate());
                Date date2 = fullFormat.parse(b.getDate());
                return date1.compareTo(date2);
            } catch (Exception e) {
                return 0;
            }
        });

        this.list = filteredList;
        notifyDataSetChanged();
    }



    @Override
    public int getItemCount() {
        return (list != null) ? list.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView datetime,pinText,availText;
        ImageView deleteIcon;
        MaterialCardView pinCard;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            datetime = itemView.findViewById(R.id.datetime);
            deleteIcon = itemView.findViewById(R.id.delete_icon);
            availText = itemView.findViewById(R.id.availText);
            pinText = itemView.findViewById(R.id.pinText);
            pinCard = itemView.findViewById(R.id.pinCard);
        }
    }
}
