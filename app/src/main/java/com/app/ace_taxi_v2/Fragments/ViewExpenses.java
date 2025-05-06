package com.app.ace_taxi_v2.Fragments;

import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.ace_taxi_v2.Fragments.Adapters.ExpenseAdapter;
import com.app.ace_taxi_v2.Logic.ExpensesResponseApi;
import com.app.ace_taxi_v2.Models.Expense;
import com.app.ace_taxi_v2.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import androidx.core.util.Pair;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class ViewExpenses extends Fragment implements ExpenseAdapter.OnExpenseChangeListener {
    private static final String TAG = "ViewExpenses";
    private TextInputEditText dateRangeEditText;
    private RecyclerView expenseRecyclerView;
    private TextView totalAmountTextView;
    private ExpenseAdapter expenseAdapter;
    private List<Expense> expenseList;
    private double totalExpense = 0.00;
    private MaterialButton selectedCategoryButton;

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
        Log.d(TAG, "Setting adapter: " + expenseAdapter.hashCode());
        expenseRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        expenseRecyclerView.setAdapter(expenseAdapter);
        Log.d(TAG, "RecyclerView and adapter initialized");

        // Date Range Picker
        dateRangeEditText.setOnClickListener(v -> showDateRangePicker());

        String[] categories = {
                "All", "Fuel", "Part", "Insurance", "Mot", "DBS",
                "Vehicle Badge", "Maintenance", "Certification", "Others"
        };

        LinearLayout buttonContainer = view.findViewById(R.id.buttonContainer);

        for (String category : categories) {
            MaterialButton button = new MaterialButton(getContext());
            button.setText(category);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMarginEnd(16);
            button.setLayoutParams(params);

            button.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.red));
            button.setTextColor(ContextCompat.getColor(getContext(),R.color.white));

            button.setOnClickListener(v -> {
                Log.d(TAG, "Category button clicked: " + category);

                // Reset previous selected button
                if (selectedCategoryButton != null) {
                    selectedCategoryButton.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.red));
                }

                // Highlight newly selected button
                button.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.green));
                selectedCategoryButton = button;

                // Filter expenses and sync lists
                Log.d(TAG, "Calling filterByCategory on adapter: " + expenseAdapter.hashCode());
                expenseAdapter.filterByCategory(category);
                expenseList.clear();
                expenseList.addAll(expenseAdapter.getFilteredList());
                Log.d(TAG, "expenseList updated with " + expenseList.size() + " expenses");
                updateTotalAmount();
                expenseRecyclerView.invalidate();

                // Show toast only if filtered list is empty
                if (expenseList.isEmpty() && !category.equalsIgnoreCase("All")) {
                    Toast.makeText(getContext(), "No expenses found for " + category, Toast.LENGTH_SHORT).show();
                }
            });

            buttonContainer.addView(button);
        }

        updateTotalAmount();

        Calendar calendar = Calendar.getInstance();
        Date endDate = calendar.getTime(); // today
        calendar.add(Calendar.DAY_OF_YEAR, -30);
        Date startDate = calendar.getTime(); // 15 days ago

        SimpleDateFormat apiDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        apiDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        SimpleDateFormat displayDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        String startDateInternal = apiDateFormat.format(startDate);
        String endDateInternal = apiDateFormat.format(endDate);
        String startDateDisplay = displayDateFormat.format(startDate);
        String endDateDisplay = displayDateFormat.format(endDate);

        dateRangeEditText.setText(startDateDisplay + " – " + endDateDisplay);
        fetchExpenses(startDateInternal, endDateInternal);
    }

    private void navigateToProfile() {
        Fragment selectedFragment = new UserProfileFragment();
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, selectedFragment);
        fragmentTransaction.commit();
    }

    private void showDateRangePicker() {
        MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker().setTheme(R.style.ThemeOverlay_App_MaterialCalendar);
        builder.setTitleText("Select Date Range");

        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
        constraintsBuilder.setValidator(DateValidatorPointBackward.now());

        builder.setCalendarConstraints(constraintsBuilder.build());

        MaterialDatePicker<Pair<Long, Long>> datePicker = builder.build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            if (selection.first != null && selection.second != null) {
                SimpleDateFormat apiDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                SimpleDateFormat displayDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

                apiDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

                String startDateInternal = apiDateFormat.format(selection.first);
                String endDateInternal = apiDateFormat.format(selection.second);
                String startDateDisplay = displayDateFormat.format(selection.first);
                String endDateDisplay = displayDateFormat.format(selection.second);

                dateRangeEditText.setText(startDateDisplay + " – " + endDateDisplay);

                fetchExpenses(startDateInternal, endDateInternal);
            }
        });

        datePicker.show(getParentFragmentManager(), "DATE_PICKER");
    }

    private void fetchExpenses(String startDate, String endDate) {
        Log.d(TAG, "fetchExpenses called with startDate: " + startDate + ", endDate: " + endDate);
        ExpensesResponseApi api = new ExpensesResponseApi(requireContext());
        api.getExpenses(startDate, endDate, new ExpensesResponseApi.OnExpensesFetchedListener() {
            @Override
            public void onExpensesFetched(List<Expense> expenses) {
                Log.d(TAG, "Expenses fetched: " + (expenses != null ? expenses.size() : "null"));
                if (expenses != null) {
                    for (Expense e : expenses) {
                        Log.d(TAG, "Expense: ID=" + e.getCategory() + ", Name=" + expenseAdapter.checkCategory(e.getCategory()) + ", Amount=" + e.getAmount());
                    }
                    expenseList.clear();
                    expenseList.addAll(expenses);
                    Log.d(TAG, "Updating adapter: " + expenseAdapter.hashCode());
                    expenseAdapter.updateList(expenses);
                    updateTotalAmount();
                    // Reset to "All" category after fetching
                    expenseAdapter.filterByCategory("All");
                    expenseList.clear();
                    expenseList.addAll(expenseAdapter.getFilteredList());
                    if (selectedCategoryButton != null) {
                        selectedCategoryButton.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.red));
                        selectedCategoryButton = null;
                    }
                    expenseRecyclerView.invalidate();
                } else {
                    Toast.makeText(getContext(), "Failed to fetch expenses", Toast.LENGTH_SHORT).show();
                    expenseList.clear();
                    expenseAdapter.updateList(new ArrayList<>());
                    updateTotalAmount();
                    expenseRecyclerView.invalidate();
                }
            }
        });
    }

    public void updateTotalAmount() {
        totalExpense = 0.00;
        for (Expense expense : expenseList) {
            totalExpense += expense.getAmount();
        }
        Log.d(TAG, "Total expense calculated: " + totalExpense + " from " + expenseList.size() + " expenses");
        totalAmountTextView.setText("£" + String.format("%.2f", totalExpense));
    }

    @Override
    public void onExpenseUpdated() {
        updateTotalAmount();
    }


}