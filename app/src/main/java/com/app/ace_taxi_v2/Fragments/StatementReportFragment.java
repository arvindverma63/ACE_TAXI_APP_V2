package com.app.ace_taxi_v2.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.app.ace_taxi_v2.Fragments.Adapters.StatementAdapter;
import com.app.ace_taxi_v2.Logic.DownloadStatement;
import com.app.ace_taxi_v2.Logic.GetStatementsApi;
import com.app.ace_taxi_v2.Logic.SessionManager;
import com.app.ace_taxi_v2.Models.Reports.StatementItem;
import com.app.ace_taxi_v2.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textview.MaterialTextView;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StatementReportFragment extends Fragment {

    private TextView totalEarn;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MaterialTextView noDataText;
    private List<StatementItem> statementList = new ArrayList<>();
    private StatementAdapter statementAdapter;
    private SimpleDateFormat isoDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
    private SimpleDateFormat displayFormat = new SimpleDateFormat("dd MM yyyy", Locale.getDefault());
    private String startDate, endDate;
    private MaterialButton dateRangeButton;
    private int userId;
    private DownloadStatement downloadStatement;

    private static final String KEY_START_DATE = "start_date";
    private static final String KEY_END_DATE = "end_date";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_statement_report, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize SessionManager and userId
        SessionManager sessionManager = new SessionManager(requireContext());
        userId = sessionManager.getUserId();

        // Initialize DownloadStatement
        downloadStatement = new DownloadStatement(this);

        // Initialize UI Components
        recyclerView = view.findViewById(R.id.recycler_view);
        totalEarn = view.findViewById(R.id.totalAmount);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        noDataText = view.findViewById(R.id.no_data_text);
        MaterialToolbar toolbar = view.findViewById(R.id.header_toolbar);
        dateRangeButton = view.findViewById(R.id.date_range_button);

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        statementAdapter = new StatementAdapter(requireContext(), statementList, downloadStatement);
        recyclerView.setAdapter(statementAdapter);

        // Set up SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(this::fetchStatements);

        // Set toolbar navigation
        toolbar.setNavigationOnClickListener(v -> navigateToReportPage());

        // Set up date range button
        dateRangeButton.setOnClickListener(v -> showDateRangePicker());

        // Restore state or set default date range
        if (savedInstanceState != null) {
            startDate = savedInstanceState.getString(KEY_START_DATE);
            endDate = savedInstanceState.getString(KEY_END_DATE);
            updateDateRangeButtonText();
        } else {
            setDefaultDateRange();
        }

        // Load Data Initially
        fetchStatements();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_START_DATE, startDate);
        outState.putString(KEY_END_DATE, endDate);
    }

    private void setDefaultDateRange() {
        Calendar calendar = Calendar.getInstance();
        endDate = isoDateFormat.format(calendar.getTime());
        calendar.add(Calendar.DAY_OF_YEAR, -30);
        startDate = isoDateFormat.format(calendar.getTime());
        updateDateRangeButtonText();
    }

    private void updateDateRangeButtonText() {
        try {
            Date start = isoDateFormat.parse(startDate);
            Date end = isoDateFormat.parse(endDate);
            if (start != null && end != null) {
                dateRangeButton.setText(displayFormat.format(start) + " - " + displayFormat.format(end));
            }
        } catch (Exception e) {
            Log.e("StatementReport", "Error parsing dates for button text", e);
        }
    }

    private void fetchStatements() {
        swipeRefreshLayout.setRefreshing(true);

        GetStatementsApi getStatementsApi = new GetStatementsApi(requireContext(), startDate, endDate, userId);
        getStatementsApi.getStatements(new GetStatementsApi.statementListener() {
            @Override
            public void onSuccess(List<StatementItem> items) {
                statementAdapter.updateData(items);
                calculateTotalEarnings(items);
                updateEmptyView();
                if (items == null || items.isEmpty()) {
                    Toast.makeText(requireContext(), "No statements found", Toast.LENGTH_SHORT).show();
                }
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFail(String error) {
                String errorMessage = "Failed to fetch statements";
                if (error != null) {
                    if (error.contains("network")) {
                        errorMessage = "Network error. Please check your connection.";
                    } else if (error.contains("server")) {
                        errorMessage = "Server error. Please try again later.";
                    }
                }
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
                statementAdapter.updateData(null);
                calculateTotalEarnings(null);
                updateEmptyView();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void calculateTotalEarnings(List<StatementItem> items) {
        double total = 0;
        if (items != null) {
            for (StatementItem item : items) {
                if (item != null) {
                    total += item.getSubTotal();
                }
            }
        }
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.UK);
        totalEarn.setText(currencyFormat.format(total));

    }

    private void updateEmptyView() {
        if (statementAdapter.getItemCount() == 0) {
            recyclerView.setVisibility(View.GONE);
            noDataText.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            noDataText.setVisibility(View.GONE);
        }
    }

    private void navigateToReportPage() {
        Fragment selectedFragment = new ReportPageFragment();
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, selectedFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void showDateRangePicker() {
        MaterialDatePicker.Builder<androidx.core.util.Pair<Long, Long>> builder =
                MaterialDatePicker.Builder.dateRangePicker().setTheme(R.style.ThemeOverlay_App_MaterialCalendar);
        builder.setTitleText("Select Date Range");

        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
        builder.setCalendarConstraints(constraintsBuilder.build());

        MaterialDatePicker<androidx.core.util.Pair<Long, Long>> datePicker = builder.build();
        datePicker.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDatePickerTheme);
        datePicker.show(getParentFragmentManager(), "DATE_PICKER");

        datePicker.addOnPositiveButtonClickListener(selection -> {
            try {
                startDate = isoDateFormat.format(selection.first);
                endDate = isoDateFormat.format(selection.second);

                updateDateRangeButtonText();
                fetchStatements();
            } catch (Exception e) {
                Log.e("DatePicker", "Error formatting dates", e);
                Toast.makeText(requireContext(), "Error selecting date range", Toast.LENGTH_SHORT).show();
            }
        });
    }
}