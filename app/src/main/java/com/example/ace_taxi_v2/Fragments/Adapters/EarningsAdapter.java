package com.example.ace_taxi_v2.Fragments.Adapters;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ace_taxi_v2.Models.EarningResponse;
import com.example.ace_taxi_v2.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class EarningsAdapter extends RecyclerView.Adapter<EarningsAdapter.EarningsViewHolder> {
    private final List<EarningResponse> earningsList;
    private final SimpleDateFormat apiDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private final SimpleDateFormat displayDateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

    public EarningsAdapter(List<EarningResponse> earningsList) {
        this.earningsList = earningsList;
    }

    @NonNull
    @Override
    public EarningsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_earning, parent, false);
        return new EarningsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EarningsViewHolder holder, int position) {
        EarningResponse earningResponse = earningsList.get(position);

        // Format Date
        String formattedDate = formatDate(earningResponse.getDate());
        holder.day.setText(formattedDate);

        // Set values with null checks
        holder.jobs.setText(String.valueOf(earningResponse.getAccJobsCount()));
        holder.cash.setText(formatCurrency(earningResponse.getCashTotal()));
        holder.ePayment.setText(formatCurrency(earningResponse.getAccTotal()));
    }

    @Override
    public int getItemCount() {
        return earningsList != null ? earningsList.size() : 0; // Prevents potential NullPointerException
    }

    public static class EarningsViewHolder extends RecyclerView.ViewHolder {
        TextView day, jobs, cash, ePayment;

        public EarningsViewHolder(@NonNull View itemView) {
            super(itemView);
            day = itemView.findViewById(R.id.text_day);
            jobs = itemView.findViewById(R.id.text_jobs);
            cash = itemView.findViewById(R.id.text_cash);
            ePayment = itemView.findViewById(R.id.text_ePayment);
        }
    }

    // Format date from API response
    private String formatDate(String dateStr) {
        if (!TextUtils.isEmpty(dateStr)) {
            try {
                return displayDateFormat.format(apiDateFormat.parse(dateStr));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return "N/A"; // Default if date is missing
    }

    // Format currency with two decimal places
    private String formatCurrency(double amount) {
        return String.format(Locale.getDefault(), "£%.2f", amount);
    }
}
