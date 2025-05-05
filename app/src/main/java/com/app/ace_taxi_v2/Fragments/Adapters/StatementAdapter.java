package com.app.ace_taxi_v2.Fragments.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.ace_taxi_v2.Logic.DownloadStatement;
import com.app.ace_taxi_v2.Logic.Formater.HHMMFormater;
import com.app.ace_taxi_v2.Models.Reports.StatementItem;
import com.app.ace_taxi_v2.R;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class StatementAdapter extends RecyclerView.Adapter<StatementAdapter.StatementViewHolder> {

    private final List<StatementItem> statementList;
    private final Context context;
    private final DownloadStatement downloadStatement;

    public StatementAdapter(Context context, List<StatementItem> statementList, DownloadStatement downloadStatement) {
        this.context = context;
        this.statementList = new ArrayList<>();
        this.downloadStatement = downloadStatement;
        if (statementList != null) {
            this.statementList.addAll(statementList);
        }
    }

    @NonNull
    @Override
    public StatementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_statement_item, parent, false);
        return new StatementViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StatementViewHolder holder, int position) {
        StatementItem item = statementList.get(position);

        HHMMFormater hhmmFormater = new HHMMFormater();
        // Bind data
        holder.statementNumber.setText(String.valueOf(item.getStatementId()));
        holder.date.setText(hhmmFormater.formateTimeStampToDateTime(item.getStatementDate()));
        holder.amount.setText(String.format("£%.2f", item.getSubTotal()));
        holder.jobCount.setText(String.valueOf(item.getTotalJobCount()));
        holder.period.setText(hhmmFormater.formateTimeStampToDateTime(item.getStartDate()) + " - " +
                hhmmFormater.formateTimeStampToDateTime(item.getEndDate()));

        // Handle button click
        holder.viewButton.setOnClickListener(v -> {
            String fileName = "Statement_" + item.getStatementId() + ".pdf";
            downloadStatement.downloadStatement(item.getStatementId(), fileName);
        });
    }

    @Override
    public int getItemCount() {
        return statementList.size();
    }

    public void updateData(List<StatementItem> newStatements) {
        statementList.clear();
        if (newStatements != null) {
            statementList.addAll(newStatements);
        }
        notifyDataSetChanged();
    }

    static class StatementViewHolder extends RecyclerView.ViewHolder {
        TextView statementNumber, date, amount, jobCount, period;
        MaterialButton viewButton;

        public StatementViewHolder(@NonNull View itemView) {
            super(itemView);
            statementNumber = itemView.findViewById(R.id.tv_statement_number);
            date = itemView.findViewById(R.id.tv_date);
            amount = itemView.findViewById(R.id.tv_amount);
            viewButton = itemView.findViewById(R.id.btn_view);
            jobCount = itemView.findViewById(R.id.tv_job_count);
            period = itemView.findViewById(R.id.tv_period);
        }
    }
}