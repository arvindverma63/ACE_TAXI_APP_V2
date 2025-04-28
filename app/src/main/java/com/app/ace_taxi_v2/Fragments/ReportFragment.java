package com.app.ace_taxi_v2.Fragments;

import android.content.Intent;
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

import com.app.ace_taxi_v2.Activity.LoginActivity;
import com.app.ace_taxi_v2.Fragments.Adapters.EarningsAdapter;
import com.app.ace_taxi_v2.Logic.EarningResponseApi;
import com.app.ace_taxi_v2.Logic.SessionManager;
import com.app.ace_taxi_v2.Models.EarningResponse;
import com.app.ace_taxi_v2.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.textview.MaterialTextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ReportFragment extends Fragment {

    private MaterialButton date_range_button;
    private RecyclerView recyclerView;
    private MaterialTextView noDataText;
    private TextView total_cash, total_epay;
    private SimpleDateFormat isoDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
    private String startDate, endDate;
    private EarningsAdapter adapter; // Use the actual EarningsAdapter

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_report, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SessionManager sessionManager = new SessionManager(getContext());
        if (!sessionManager.isLoggedIn()) {
            Intent intent = new Intent(getContext(), LoginActivity.class);
            startActivity(intent);
            requireActivity().finish();
        }

        // Initialize views
        date_range_button = view.findViewById(R.id.date_range_button);
        total_cash = view.findViewById(R.id.total_cash);
        total_epay = view.findViewById(R.id.total_epay);
        recyclerView = view.findViewById(R.id.recycler_view);
        noDataText = view.findViewById(R.id.no_data_text);
        MaterialToolbar toolbar = view.findViewById(R.id.header_toolbar);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new EarningsAdapter(null); // Initialize with empty list
        recyclerView.setAdapter(adapter);

        // Set click listeners
        date_range_button.setOnClickListener(v -> showDateRangePicker());
        toolbar.setNavigationOnClickListener(v -> {
            Fragment select = new ReportPageFragment();
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, select);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

        // Set default date range and fetch data
        setDefaultDateRange();
    }

    private void setDefaultDateRange() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -2);

        startDate = isoDateFormat.format(calendar.getTime());
        endDate = isoDateFormat.format(Calendar.getInstance().getTime());

        SimpleDateFormat displayFormat = new SimpleDateFormat("dd MM yyyy", Locale.getDefault());
        String displayStartDate = displayFormat.format(calendar.getTime());
        String displayEndDate = displayFormat.format(Calendar.getInstance().getTime());

        date_range_button.setText(displayStartDate + " - " + displayEndDate);

        Log.e("start and end date: ", startDate + " end date: " + endDate);
        updateData(startDate, endDate);
    }

    private void showDateRangePicker() {
        MaterialDatePicker.Builder<androidx.core.util.Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
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

                SimpleDateFormat displayFormat = new SimpleDateFormat("dd MM yyyy", Locale.getDefault());
                String displayStartDate = displayFormat.format(selection.first);
                String displayEndDate = displayFormat.format(selection.second);

                date_range_button.setText(displayStartDate + " - " + displayEndDate);
                updateData(startDate, endDate);
            } catch (Exception e) {
                Log.e("DatePicker", "Error formatting dates", e);
            }
        });
    }

    public void updateData(String from, String to) {
        Log.e("start and end date: ", "" + from + " end date: " + to);
        EarningResponseApi earningResponseApi = new EarningResponseApi(getContext(), null); // WebView not needed here
        earningResponseApi.getResponse(from, to, recyclerView, new EarningResponseApi.EarningCallback() {
            @Override
            public void onSuccess(List<EarningResponse> responses) {
                updateTotal(responses);
                updateEmptyView(); // Check empty state
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
                adapter.updateData(null); // Clear data on error
                updateEmptyView(); // Check empty state
            }
        });
    }

    public void updateTotal(List<EarningResponse> earningResponseList) {
        double totalCash = 0;
        double epay = 0;
        if (earningResponseList != null) {
            for (EarningResponse earningResponse : earningResponseList) {
                totalCash += earningResponse.getCashTotal();
                epay += earningResponse.getAccTotal();
            }
        }
        total_cash.setText("Total Cash: £" + totalCash);
        total_epay.setText("Total ACC: £" + epay);
    }

    private void updateEmptyView() {
        if (adapter.getItemCount() == 0) {
            recyclerView.setVisibility(View.GONE);
            noDataText.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            noDataText.setVisibility(View.GONE);
        }
    }
}