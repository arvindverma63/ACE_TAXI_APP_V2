package com.app.ace_taxi_v2.Fragments.Adapters;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.ace_taxi_v2.Components.EarningStatementDialog;
import com.app.ace_taxi_v2.Models.EarningResponse;
import com.app.ace_taxi_v2.R;
import com.google.android.material.button.MaterialButton;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EarningsAdapter extends RecyclerView.Adapter<EarningsAdapter.EarningsViewHolder> {
    private final List<EarningResponse> earningsList;
    private final SimpleDateFormat apiDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
    private final SimpleDateFormat displayDateFormat = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
    private final DecimalFormat currencyFormat = new DecimalFormat("£#,##0.00");

    public EarningsAdapter(List<EarningResponse> earningsList) {
        this.earningsList = new ArrayList<>();
        if (earningsList != null) {
            this.earningsList.addAll(earningsList);
        }
    }

    @NonNull
    @Override
    public EarningsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_earning, parent, false);
        return new EarningsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EarningsViewHolder holder, int position) {
        EarningResponse earning = earningsList.get(position);

        // Bind data
        holder.day.setText(formatDate(earning.getDate()));

        // Calculate total jobs
        int totalJobs = earning.getCashJobsCount() + earning.getAccJobsCount() + earning.getRankJobsCount();
        holder.jobs.setText(String.valueOf(totalJobs));

        holder.cash.setText(currencyFormat.format(earning.getNetTotal()));

        // Set up dialog button
        holder.action.setOnClickListener(v -> {
            EarningStatementDialog dialog = new EarningStatementDialog(v.getContext());
            dialog.openDialog(
                    formatDate(earning.getDate()), // Format date as dd/MM/yy
                    earning.getUserId(),
                    earning.getCashTotal(),
                    earning.getAccTotal(),
                    earning.getRankTotal(),
                    earning.getCommsTotal(),
                    earning.getGrossTotal(),
                    earning.getNetTotal(),
                    earning.getCashJobsCount(),
                    earning.getAccJobsCount(),
                    earning.getRankJobsCount(),
                    earning.getCashMilesCount(),
                    earning.getAccMilesCount(),
                    earning.getRankMilesCount()
            );
        });
    }

    @Override
    public int getItemCount() {
        return earningsList.size();
    }

    public static class EarningsViewHolder extends RecyclerView.ViewHolder {
        TextView day, jobs, cash;
        MaterialButton action;

        public EarningsViewHolder(@NonNull View itemView) {
            super(itemView);
            day = itemView.findViewById(R.id.text_day);
            jobs = itemView.findViewById(R.id.text_jobs);
            cash = itemView.findViewById(R.id.text_cash);
            action = itemView.findViewById(R.id.action);
        }
    }

    private String formatDate(String dateStr) {
        if (!TextUtils.isEmpty(dateStr)) {
            try {
                return displayDateFormat.format(apiDateFormat.parse(dateStr));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return "N/A";
    }

    public void updateData(List<EarningResponse> newEarnings) {
        earningsList.clear();
        if (newEarnings != null) {
            earningsList.addAll(newEarnings);
        }
        notifyDataSetChanged();
    }
}
