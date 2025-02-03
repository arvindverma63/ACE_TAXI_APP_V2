package com.example.ace_taxi_v2.Fragments.Adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ace_taxi_v2.Components.StatementDialog;
import com.example.ace_taxi_v2.Models.Reports.StatementItem;
import com.example.ace_taxi_v2.R;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class StatementAdapter extends RecyclerView.Adapter<StatementAdapter.StatementViewHolder> {

    private final List<StatementItem> statementList;
    private final Context context;

    public StatementAdapter(Context context, List<StatementItem> statementList) {
        this.context = context;
        this.statementList = statementList;
    }

    @NonNull
    @Override
    public StatementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_statement_item, parent, false);
        return new StatementViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StatementViewHolder holder, int position) {
        if (statementList == null || statementList.isEmpty()) return;

        StatementItem item = statementList.get(position);

        // ✅ Fix: Convert int to String to avoid NotFoundException
        holder.statementNumber.setText(String.valueOf(item.getStatementId()));

        // Format date safely
        if (item.getDateCreated() != null) {
            holder.date.setText(item.getDateCreated());
        } else {
            holder.date.setText("N/A");
        }

        // Format earnings safely
        holder.amount.setText(String.format("£%.2f", item.getTotalEarned()));

        // Handle button click
        holder.viewButton.setOnClickListener(v -> {
            StatementDialog statementDialog = new StatementDialog();
            statementDialog.showStatementDialog(context,item);
        });
    }


    @Override
    public int getItemCount() {
        return statementList.size();
    }

    static class StatementViewHolder extends RecyclerView.ViewHolder {

        TextView statementNumber, date, amount;
        MaterialButton viewButton;

        public StatementViewHolder(@NonNull View itemView) {
            super(itemView);
            statementNumber = itemView.findViewById(R.id.tv_statement_number);
            date = itemView.findViewById(R.id.tv_date);
            amount = itemView.findViewById(R.id.tv_amount);
            viewButton = itemView.findViewById(R.id.btn_view);
        }
    }
}
