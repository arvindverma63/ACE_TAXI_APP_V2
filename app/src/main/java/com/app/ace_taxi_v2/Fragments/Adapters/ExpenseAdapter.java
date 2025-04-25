package com.app.ace_taxi_v2.Fragments.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.ace_taxi_v2.Models.Expense;
import com.app.ace_taxi_v2.R;

import java.util.ArrayList;
import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ViewHolder> {
    private List<Expense> expenseList;  // Currently filtered list
    private List<Expense> allExpenses;  // Backup of full list
    private Context context;
    private static final String TAG = "ExpenseAdapter";

    public interface OnExpenseChangeListener {
        void onExpenseUpdated();
    }

    public ExpenseAdapter(Context context, List<Expense> expenses) {
        this.context = context;
        this.expenseList = new ArrayList<>(expenses);
        this.allExpenses = new ArrayList<>(expenses);
        Log.d(TAG, "Adapter created: " + this.hashCode() + ", initialized with " + expenses.size() + " expenses");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_expense, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (expenseList == null || position >= expenseList.size()) {
            Log.e(TAG, "Invalid position: " + position + ", expenseList size: " + (expenseList != null ? expenseList.size() : "null"));
            return;
        }
        Expense expense = expenseList.get(position);
        Log.d(TAG, "Adapter: " + this.hashCode() + ", Binding expense at position " + position + ": Category=" + checkCategory(expense.getCategory()) + ", Amount=" + expense.getAmount());
        holder.date.setText(expense.getDate());
        holder.category.setText(checkCategory(expense.getCategory()));
        holder.amount.setText("£" + String.format("%.2f", expense.getAmount()));
        holder.description.setText(expense.getDescription());
    }

    @Override
    public int getItemCount() {
        int size = expenseList != null ? expenseList.size() : 0;
        Log.d(TAG, "Adapter: " + this.hashCode() + ", getItemCount: " + size + " items");
        return size;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView date, category, amount, description;

        public ViewHolder(View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.expenseDate);
            category = itemView.findViewById(R.id.expenseCategory);
            amount = itemView.findViewById(R.id.expenseAmount);
            description = itemView.findViewById(R.id.description);
        }
    }

    public String checkCategory(int id) {
        switch (id) {
            case 0: return "Fuel";
            case 1: return "Part";
            case 2: return "Insurance";
            case 3: return "Mot";
            case 4: return "DBS";
            case 5: return "Vehicle Badge";
            case 6: return "Maintenance";
            case 7: return "Certification";
            case 8: return "Others";
            default:
                Log.e(TAG, "Invalid category ID: " + id);
                return "Unknown";
        }
    }

    public void filterByCategory(String categoryName) {
        try {
            Log.d(TAG, "Adapter: " + this.hashCode() + ", filterByCategory called with category: " + categoryName);
            Log.d(TAG, "allExpenses size: " + allExpenses.size());

            if (categoryName == null) {
                Log.e(TAG, "categoryName is null");
                return;
            }

            List<Expense> filteredList = new ArrayList<>();
            if (categoryName.equalsIgnoreCase("All")) {
                filteredList.addAll(allExpenses);
            } else {
                for (Expense e : allExpenses) {
                    if (e == null) {
                        Log.e(TAG, "Null expense found in allExpenses");
                        continue;
                    }
                    String category = checkCategory(e.getCategory());
                    Log.d(TAG, "Checking expense: ID=" + e.getCategory() + ", Name=" + category);
                    if (category.equalsIgnoreCase(categoryName)) {
                        filteredList.add(e);
                    }
                }
            }

            Log.d(TAG, "Filtered list size: " + filteredList.size());
            expenseList.clear();
            expenseList.addAll(filteredList);
            Log.d(TAG, "Adapter: " + this.hashCode() + ", Notifying data set changed");
            notifyDataSetChanged();
        } catch (Exception e) {
            Log.e(TAG, "Error in filterByCategory: " + e.getMessage(), e);
        }
    }

    public void updateList(List<Expense> newExpenses) {
        try {
            Log.d(TAG, "Adapter: " + this.hashCode() + ", updateList called with " + (newExpenses != null ? newExpenses.size() : "null") + " expenses");
            if (newExpenses == null) {
                Log.e(TAG, "newExpenses is null");
                this.allExpenses.clear();
                this.expenseList.clear();
            } else {
                this.allExpenses.clear();
                this.allExpenses.addAll(newExpenses);
                this.expenseList.clear();
                this.expenseList.addAll(newExpenses);
            }
            Log.d(TAG, "Adapter: " + this.hashCode() + ", Notifying data set changed");
            notifyDataSetChanged();
        } catch (Exception e) {
            Log.e(TAG, "Error in updateList: " + e.getMessage(), e);
        }
    }

    public List<Expense> getFilteredList() {
        Log.d(TAG, "Adapter: " + this.hashCode() + ", getFilteredList: Returning " + expenseList.size() + " expenses");
        return new ArrayList<>(expenseList);
    }
}