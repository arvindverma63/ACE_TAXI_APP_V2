package com.example.ace_taxi_v2.Fragments.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ace_taxi_v2.Models.Expense;
import com.example.ace_taxi_v2.R;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ViewHolder> {
    private List<Expense> expenseList;
    private Context context;
    private OnExpenseChangeListener listener;

    public interface OnExpenseChangeListener {
        void onExpenseUpdated();
    }

    public ExpenseAdapter(Context context, List<Expense> expenseList, OnExpenseChangeListener listener) {
        this.context = context;
        this.expenseList = expenseList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_expense, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Expense expense = expenseList.get(position);
        holder.date.setText(expense.getDate());
        holder.category.setText(expense.getCategory());
        holder.description.setText(expense.getDescription());
        holder.amount.setText("£" + String.format("%.2f", expense.getAmount()));

        // Handle Delete Button Click
        holder.deleteButton.setOnClickListener(v -> {
            expenseList.remove(position);
            notifyItemRemoved(position);
            listener.onExpenseUpdated(); // Update total amount
        });
    }

    @Override
    public int getItemCount() {
        return expenseList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView date, category, description, amount;
        MaterialButton deleteButton;

        public ViewHolder(View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.expenseDate);
            category = itemView.findViewById(R.id.expenseCategory);
            description = itemView.findViewById(R.id.expenseDescription);
            amount = itemView.findViewById(R.id.expenseAmount);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }


}
