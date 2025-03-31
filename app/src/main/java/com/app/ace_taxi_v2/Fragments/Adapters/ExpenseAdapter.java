package com.app.ace_taxi_v2.Fragments.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.ace_taxi_v2.Components.ExpensesDialog;
import com.app.ace_taxi_v2.Models.Expense;
import com.app.ace_taxi_v2.R;
import com.google.android.material.button.MaterialButton;
import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ViewHolder> {
    private List<Expense> expenseList;
    private Context context;

    public interface OnExpenseChangeListener {
        void onExpenseUpdated();
    }

    public ExpenseAdapter(Context context, List<Expense> expenses) {
        this.context = context;
        this.expenseList = expenses;
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
        holder.category.setText(checkCategory(expense.getCategory())); // ✅ Fixed: Convert category ID to text
        holder.amount.setText("£" + String.format("%.2f", expense.getAmount()));

        holder.viewBtn.setOnClickListener(v -> {
            ExpensesDialog dialog = new ExpensesDialog(context);
            dialog.openExpensesDescriptionDialog(expense.getDescription());
        });
    }

    @Override
    public int getItemCount() {
        return expenseList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView date, category, amount;
        MaterialButton viewBtn;


        public ViewHolder(View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.expenseDate);
            category = itemView.findViewById(R.id.expenseCategory);
            amount = itemView.findViewById(R.id.expenseAmount);
            viewBtn = itemView.findViewById(R.id.btn_view);
        }
    }

    // ✅ Fixed: Removed unnecessary `break` statements and changed `null` to "Unknown"
    public String checkCategory(int id) {
        switch (id) {
            case 0: return "Fuel";
            case 1: return "Part";
            case 2: return "Insurance";
            case 3: return "Mot";
            case 4: return "DBS";
            case 5: return "Vehicle Badge"; // ✅ Fixed Spelling
            case 6: return "Maintenance";
            case 7: return "Certification";
            case 8: return "Others";
            default: return "Unknown"; // ✅ Fixed: No null return
        }
    }
}
