package com.app.ace_taxi_v2.Fragments;

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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.app.ace_taxi_v2.Activity.LoginActivity;
import com.app.ace_taxi_v2.Components.CustomDialog;
import com.app.ace_taxi_v2.Logic.AvailabilityAddApi;
import com.app.ace_taxi_v2.Logic.EarningResponseApi;
import com.app.ace_taxi_v2.Logic.SessionManager;
import com.app.ace_taxi_v2.Models.EarningResponse;
import com.app.ace_taxi_v2.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ReportFragment extends Fragment {

    private WebView webView;
    public MaterialButton date_range_button, updateData;
    public RecyclerView recyclarView;
    private SimpleDateFormat isoDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
    public String startDate, endDate;
    public TextView total_cash,total_epay;


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

        date_range_button = view.findViewById(R.id.date_range_button);
        total_cash = view.findViewById(R.id.total_cash);
        total_epay = view.findViewById(R.id.total_epay);
        MaterialToolbar toolbar = view.findViewById(R.id.header_toolbar);
        recyclarView = view.findViewById(R.id.recycler_view);

        date_range_button.setOnClickListener(v -> showDateRangePicker());
        toolbar.setNavigationOnClickListener(v -> {
            Fragment select = new ReportPageFragment();
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, select); // Use the correct container ID
            fragmentTransaction.addToBackStack(null); // Optional: Allows back navigation
            fragmentTransaction.commit();
        });



        setDefaultDateRange();
    }

    private void setDefaultDateRange() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -2);

        startDate = isoDateFormat.format(calendar.getTime());
        endDate = isoDateFormat.format(Calendar.getInstance().getTime());

        // Convert to readable format for display
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
        datePicker.show(getParentFragmentManager(), "DATE_PICKER");

        datePicker.addOnPositiveButtonClickListener(selection -> {
            androidx.core.util.Pair<Long, Long> dateRange = selection;
            if (dateRange != null) {
                startDate = isoDateFormat.format(dateRange.first);
                endDate = isoDateFormat.format(dateRange.second);

                // Convert to readable format
                SimpleDateFormat displayFormat = new SimpleDateFormat("dd MM yyyy", Locale.getDefault());

                String displayStartDate = displayFormat.format(dateRange.first);
                String displayEndDate = displayFormat.format(dateRange.second);

                date_range_button.setText(displayStartDate + " - " + displayEndDate);

                updateData(startDate, endDate);
            }
        });
    }


    public void updateData(String from, String to) {
        Log.e("start and end date: ", "" + from + " end date: " + to);
        EarningResponseApi earningResponseApi = new EarningResponseApi(getContext(),webView);
        earningResponseApi.getResponse(from, to, recyclarView, new EarningResponseApi.EarningCallback() {
            @Override
            public void onSuccess(List<EarningResponse> responses) {
                updateTotal(responses);
            }

            @Override
            public void onError(String error) {

            }
        });
    }

    public void updateTotal(List<EarningResponse> earningResponseList){

        double totalCash = 0;
        double epay = 0;
        for(EarningResponse earningResponse : earningResponseList){
            totalCash += earningResponse.getCashTotal();
            epay += earningResponse.getAccTotal();
        }

        total_cash.setText("Total Cash: £"+totalCash);
        total_epay.setText("Total ACC: £"+epay);
    }


}
