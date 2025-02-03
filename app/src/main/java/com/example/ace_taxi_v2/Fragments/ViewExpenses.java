package com.example.ace_taxi_v2.Fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.ace_taxi_v2.Fragments.Adapters.ExpenseAdapter;
import com.example.ace_taxi_v2.Logic.ExpensesResponseApi;
import com.example.ace_taxi_v2.Models.Expense;
import com.example.ace_taxi_v2.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ViewExpenses extends Fragment implements ExpenseAdapter.OnExpenseChangeListener {

    private TextInputEditText dateRangeEditText;
    private RecyclerView expenseRecyclerView;
    private TextView totalAmountTextView;
    private ExpenseAdapter expenseAdapter;
    private List<Expense> expenseList; // ✅ Fixed: Use List<Expense>
    private double totalExpense = 0.00;

    public ViewExpenses() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_view_expenses, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Views
        dateRangeEditText = view.findViewById(R.id.dateRangeEditText);
        expenseRecyclerView = view.findViewById(R.id.expenseRecyclerView);
        totalAmountTextView = view.findViewById(R.id.totalAmount);

        // Toolbar Navigation
        MaterialToolbar toolbar = view.findViewById(R.id.toolbar_header);
        toolbar.setNavigationOnClickListener(v -> navigateToProfile());

        // Setup RecyclerView
        expenseList = new ArrayList<>();
        expenseAdapter = new ExpenseAdapter(getContext(), expenseList);
        expenseRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        expenseRecyclerView.setAdapter(expenseAdapter);

        // Date Range Picker
        dateRangeEditText.setOnClickListener(v -> showDateRangePicker());



        updateTotalAmount();
    }

    private void navigateToProfile() {
        Fragment selectedFragment = new ProfileFragment();
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, selectedFragment);
        fragmentTransaction.commit();
    }

    private void showDateRangePicker() {
        Calendar calendar = Calendar.getInstance();

        // Start Date Picker
        DatePickerDialog startDatePicker = new DatePickerDialog(requireContext(),
                (view, year, month, dayOfMonth) -> {
                    String startDate = dayOfMonth + "/" + (month + 1) + "/" + year;

                    // End Date Picker
                    DatePickerDialog endDatePicker = new DatePickerDialog(requireContext(),
                            (endView, endYear, endMonth, endDay) -> {
                                String endDate = endDay + "/" + (endMonth + 1) + "/" + endYear;
                                dateRangeEditText.setText(startDate + " – " + endDate);

                                // Fetch expenses after selecting date range
                                fetchExpenses(startDate, endDate);
                            },
                            calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
                    );
                    endDatePicker.show();
                },
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
        );
        startDatePicker.show();
    }

    private void fetchExpenses(String startDate, String endDate) {
        ExpensesResponseApi api = new ExpensesResponseApi(requireContext());

        api.getExpenses(startDate, endDate, expenseRecyclerView,new ExpensesResponseApi.OnExpensesFetchedListener() {
            @Override
            public void onExpensesFetched(List<Expense> expenses) {
                if (expenses != null) {
                    expenseList.clear(); // ✅ Clear old data
                    expenseList.addAll(expenses); // ✅ Update with new data
                    expenseAdapter.notifyDataSetChanged(); // ✅ Notify adapter of changes
                    updateTotalAmount(); // ✅ Update total amount after setting the data
                }
            }
        });
    }


    public void updateTotalAmount() {
        totalExpense = 0.00;
        for (Expense expense : expenseList) { // ✅ Fixed: Loop through List<Expense>
            totalExpense += expense.getAmount();
        }
        totalAmountTextView.setText("£" + String.format("%.2f", totalExpense));
    }

    @Override
    public void onExpenseUpdated() {
        updateTotalAmount();
    }
}
