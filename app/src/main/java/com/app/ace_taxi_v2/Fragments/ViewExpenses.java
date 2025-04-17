package com.app.ace_taxi_v2.Fragments;

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
import com.app.ace_taxi_v2.Fragments.Adapters.ExpenseAdapter;
import com.app.ace_taxi_v2.Logic.ExpensesResponseApi;
import com.app.ace_taxi_v2.Models.Expense;
import com.app.ace_taxi_v2.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import androidx.core.util.Pair;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class ViewExpenses extends Fragment implements ExpenseAdapter.OnExpenseChangeListener {

    private TextInputEditText dateRangeEditText;
    private RecyclerView expenseRecyclerView;
    private TextView totalAmountTextView;
    private ExpenseAdapter expenseAdapter;
    private List<Expense> expenseList;
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
        Fragment selectedFragment = new UserProfileFragment();
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, selectedFragment);
        fragmentTransaction.commit();
    }

    private void showDateRangePicker() {
        // Build MaterialDatePicker for date range selection
        MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
        builder.setTitleText("Select Date Range");

        // Restrict dates to today and past only
        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
        constraintsBuilder.setValidator(DateValidatorPointBackward.now());

        builder.setCalendarConstraints(constraintsBuilder.build());

        MaterialDatePicker<Pair<Long, Long>> datePicker = builder.build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            if (selection.first != null && selection.second != null) {
                // Define date formats
                SimpleDateFormat apiDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                SimpleDateFormat displayDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

                apiDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

                // Convert timestamps to formatted dates
                String startDateInternal = apiDateFormat.format(selection.first);
                String endDateInternal = apiDateFormat.format(selection.second);
                String startDateDisplay = displayDateFormat.format(selection.first);
                String endDateDisplay = displayDateFormat.format(selection.second);

                // Show formatted date range in the EditText
                dateRangeEditText.setText(startDateDisplay + " – " + endDateDisplay);

                // Fetch expenses using API formatted dates
                fetchExpenses(startDateInternal, endDateInternal);
            }
        });

        datePicker.show(getParentFragmentManager(), "DATE_PICKER");
    }

    private void fetchExpenses(String startDate, String endDate) {
        ExpensesResponseApi api = new ExpensesResponseApi(requireContext());

        api.getExpenses(startDate, endDate, expenseRecyclerView, new ExpensesResponseApi.OnExpensesFetchedListener() {
            @Override
            public void onExpensesFetched(List<Expense> expenses) {
                if (expenses != null) {
                    expenseList.clear();
                    expenseList.addAll(expenses);
                    expenseAdapter.notifyDataSetChanged();
                    updateTotalAmount();
                }
            }
        });
    }

    public void updateTotalAmount() {
        totalExpense = 0.00;
        for (Expense expense : expenseList) {
            totalExpense += expense.getAmount();
        }
        totalAmountTextView.setText("£" + String.format("%.2f", totalExpense));
    }

    @Override
    public void onExpenseUpdated() {
        updateTotalAmount();
    }
}
