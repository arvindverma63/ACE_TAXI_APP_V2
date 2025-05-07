package com.app.ace_taxi_v2.Fragments.Adapters;

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
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.app.ace_taxi_v2.Logic.DeleteAvailbility;
import com.app.ace_taxi_v2.Logic.Formater.HHMMFormater;
import com.app.ace_taxi_v2.Models.AvailabilityResponse;
import com.app.ace_taxi_v2.R;
import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class AvailablitiesAdapter extends RecyclerView.Adapter<AvailablitiesAdapter.ViewHolder> {

    private final List<AvailabilityResponse.Driver> originalList;
    private List<AvailabilityResponse.Driver> list;
    private final Context context;
    private TextView emptyStateView; // Optional: For empty state UI

    public enum AvailabilityType {
        AVAILABLE(1, R.string.available, R.color.green),
        UNAVAILABLE(2, R.string.unavailable, R.color.red);

        private final int id;
        private final int stringResId;
        private final int colorResId;

        AvailabilityType(int id, int stringResId, int colorResId) {
            this.id = id;
            this.stringResId = stringResId;
            this.colorResId = colorResId;
        }

        public static AvailabilityType fromId(int id) {
            for (AvailabilityType type : values()) {
                if (type.id == id) return type;
            }
            return null; // Handle unknown types gracefully
        }
    }

    public AvailablitiesAdapter(List<AvailabilityResponse.Driver> list, Context context, TextView emptyStateView) {
        this.context = context;
        this.originalList = new ArrayList<>(list);
        this.list = new ArrayList<>(list);
        this.emptyStateView = emptyStateView;
        updateEmptyState();
    }

    public AvailablitiesAdapter(List<AvailabilityResponse.Driver> list, Context context) {
        this(list, context, null);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.availablities_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AvailabilityResponse.Driver response = list.get(position);

        HHMMFormater hhmmFormater = new HHMMFormater();
        holder.datetime.setText(hhmmFormater.formateTimeStampToDateTime(response.getDate()));
        holder.availText.setText(response.getDescription());

        AvailabilityType type = AvailabilityType.fromId(response.getAvailabilityType());
        if (type != null) {
            holder.pinText.setText(context.getString(type.stringResId));
            holder.pinCard.setBackgroundTintList(ContextCompat.getColorStateList(context, type.colorResId));
        } else {
            holder.pinText.setText(context.getString(R.string.unknown));
            holder.pinCard.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.gray));
        }

        DeleteAvailbility deleteAvailbility = new DeleteAvailbility(context);
        holder.deleteIcon.setOnClickListener(v -> {
            deleteAvailbility.deleteAva(response.getId());
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition != RecyclerView.NO_POSITION) {
                list.remove(currentPosition);
                notifyItemRemoved(currentPosition);
                notifyItemRangeChanged(currentPosition, list.size());
                updateEmptyState();
            }
        });
    }

    public AvailablitiesAdapter updateListForDate(String selectedDate) {
        Log.d("FilterDebug", "Updating list for date: " + selectedDate);
        Log.d("FilterDebug", "Original list size: " + originalList.size());

        SimpleDateFormat apiFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat[] formatters = {
                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()),
                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()),
                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
        };
        for (SimpleDateFormat formatter : formatters) {
            formatter.setLenient(false);
        }

        List<AvailabilityResponse.Driver> newList = new ArrayList<>();
        for (AvailabilityResponse.Driver item : originalList) {
            Date fullDate = null;
            String itemDate = item.getDate();
            for (SimpleDateFormat formatter : formatters) {
                try {
                    fullDate = formatter.parse(itemDate);
                    break;
                } catch (Exception e) {
                    // Try next format
                }
            }
            if (fullDate == null) {
                Log.e("FilterDebug", "Failed to parse date: " + itemDate);
                continue;
            }

            String onlyDate = apiFormat.format(fullDate);
            Log.d("FilterDebug", "Item date: " + itemDate + " -> Parsed: " + onlyDate);
            if (onlyDate.equals(selectedDate)) {
                newList.add(item);
            }
        }

        Collections.sort(newList, (a, b) -> {
            Date date1 = null, date2 = null;
            for (SimpleDateFormat formatter : formatters) {
                try {
                    date1 = formatter.parse(a.getDate());
                    date2 = formatter.parse(b.getDate());
                    break;
                } catch (Exception e) {
                    // Try next format
                }
            }
            if (date1 != null && date2 != null) {
                return date1.compareTo(date2);
            }
            Log.e("FilterDebug", "Error sorting dates: " + a.getDate() + ", " + b.getDate());
            return 0;
        });

        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return list.size();
            }

            @Override
            public int getNewListSize() {
                return newList.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                String oldId = String.valueOf(list.get(oldItemPosition).getId());
                String newId = String.valueOf(newList.get(newItemPosition).getId());
                return oldId != null && newId != null && oldId.equals(newId);
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                AvailabilityResponse.Driver oldItem = list.get(oldItemPosition);
                AvailabilityResponse.Driver newItem = newList.get(newItemPosition);
                return Objects.equals(oldItem.getId(), newItem.getId()) &&
                        Objects.equals(oldItem.getDate(), newItem.getDate()) &&
                        oldItem.getAvailabilityType() == newItem.getAvailabilityType() &&
                        Objects.equals(oldItem.getDescription(), newItem.getDescription());
            }
        });

        list = newList;
        diffResult.dispatchUpdatesTo(this);
        Log.d("FilterDebug", "Filtered list size: " + list.size());

        updateEmptyState();
        if (list.isEmpty()) {
            Log.w("FilterDebug", "No availabilities found for " + selectedDate);
        }

        return this;
    }

    private void updateEmptyState() {
        if (emptyStateView != null) {
            emptyStateView.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView datetime, pinText, availText;
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