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
import com.example.ace_taxi_v2.Models.Expense;
import com.example.ace_taxi_v2.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ViewExpenses extends Fragment {

    private TextInputEditText dateRangeEditText;
    private RecyclerView expenseRecyclerView;
    private TextView totalAmountTextView;
    private MaterialButton cancelButton, recordButton;

    private ExpenseAdapter expenseAdapter;
    private List<Expense> expenseList;
    private double totalExpense = 0.00;

    public ViewExpenses() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout
        return inflater.inflate(R.layout.fragment_view_expenses, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Views
        dateRangeEditText = view.findViewById(R.id.dateRangeEditText);
        expenseRecyclerView = view.findViewById(R.id.expenseRecyclerView);
        totalAmountTextView = view.findViewById(R.id.totalAmount);
        cancelButton = view.findViewById(R.id.cancelButton);
        recordButton = view.findViewById(R.id.recordButton);

        // Set up RecyclerView
        expenseList = new ArrayList<>();
        loadDummyExpenses();  // Load sample expenses
        expenseAdapter = new ExpenseAdapter(requireContext(), expenseList, this::updateTotalAmount);
        expenseRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        expenseRecyclerView.setAdapter(expenseAdapter);

        MaterialToolbar toolbar = view.findViewById(R.id.toolbar_header);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment selectedFragment = new ProfileFragment();
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container,selectedFragment);
                fragmentTransaction.commit();
            }
        });
        // Date Range Picker
        dateRangeEditText.setOnClickListener(v -> showDateRangePicker());

        // Cancel Button
        cancelButton.setOnClickListener(v -> requireActivity().onBackPressed());

        // Record Button (Handle Saving or Next Action)
        recordButton.setOnClickListener(v -> {
            // Implement Save or Navigation logic here
        });

        // Update Total
        updateTotalAmount();
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
                            },
                            calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
                    );
                    endDatePicker.show();
                },
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
        );
        startDatePicker.show();
    }

    private void loadDummyExpenses() {
        expenseList.add(new Expense("20/01/25", "FUEL", "For School Run", 23.00));
        expenseList.add(new Expense("22/01/25", "MOT", "6 Monthly", 23.00));
        expenseList.add(new Expense("23/01/25", "MISC", "For School Run", 23.00));
        expenseList.add(new Expense("24/01/25", "TYRE", "For School Run", 23.00));

        // Notify Adapter
        if (expenseAdapter != null) {
            expenseAdapter.notifyDataSetChanged();
        }

        updateTotalAmount();
    }

    private void updateTotalAmount() {
        totalExpense = 0.00;
        for (Expense expense : expenseList) {
            totalExpense += expense.getAmount();
        }
        totalAmountTextView.setText("£" + String.format("%.2f", totalExpense));
    }
}
