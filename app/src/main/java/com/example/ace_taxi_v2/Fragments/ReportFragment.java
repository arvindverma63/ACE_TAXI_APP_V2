package com.example.ace_taxi_v2.Fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ace_taxi_v2.Activity.LoginActivity;
import com.example.ace_taxi_v2.Components.CustomDialog;
import com.example.ace_taxi_v2.Logic.AvailabilityAddApi;
import com.example.ace_taxi_v2.Logic.EarningResponseApi;
import com.example.ace_taxi_v2.Logic.SessionManager;
import com.example.ace_taxi_v2.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ReportFragment extends Fragment {

    private WebView webView;
    public MaterialButton date_range_button, updateData;
    public RecyclerView recyclarView;
    private SimpleDateFormat isoDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
    public String startDate, endDate;

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
        }

        webView = view.findViewById(R.id.google_pie_chart);
        date_range_button = view.findViewById(R.id.date_range_button);

        webView = view.findViewById(R.id.google_pie_chart);
        updateData = view.findViewById(R.id.updateData);
        recyclarView = view.findViewById(R.id.recycler_view);

        date_range_button.setOnClickListener(v -> showDateRangePicker());
        updateData.setOnClickListener(v -> updateData(startDate, endDate));



        setDefaultDateRange();
    }

    private void setDefaultDateRange() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -2);
        startDate = isoDateFormat.format(calendar.getTime());
        endDate = isoDateFormat.format(Calendar.getInstance().getTime());
        date_range_button.setText(startDate + " - " + endDate);
        Log.e("start and end date: ", "" + startDate + " end date: " + endDate);
        updateData(startDate, endDate);
    }

    private void showDateRangePicker() {
        MaterialDatePicker.Builder<androidx.core.util.Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
        builder.setTitleText("Select Date Range");
        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
        builder.setCalendarConstraints(constraintsBuilder.build());
        MaterialDatePicker<androidx.core.util.Pair<Long, Long>> datePicker = builder.build();

        datePicker.show(getParentFragmentManager(), "DATE_PICKER");

        datePicker.addOnPositiveButtonClickListener(selection -> {
            androidx.core.util.Pair<Long, Long> dateRange = selection;
            if (dateRange != null) {
                startDate = isoDateFormat.format(dateRange.first);
                endDate = isoDateFormat.format(dateRange.second);
                date_range_button.setText(startDate + " - " + endDate);
                updateData(startDate, endDate);
            }
        });
    }

    public void updateData(String from, String to) {
        Log.e("start and end date: ", "" + from + " end date: " + to);
        EarningResponseApi earningResponseApi = new EarningResponseApi(getContext(),webView);
        earningResponseApi.getResponse(from, to, recyclarView);
    }


}
