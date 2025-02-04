package com.example.ace_taxi_v2.Fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ace_taxi_v2.R;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CustomerForm extends Fragment {

    private TextInputEditText fromTimeEditText, toTimeEditText;
    private Button dateRangeButton;
    private Calendar fromDate = Calendar.getInstance();
    private Calendar toDate = Calendar.getInstance();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_customer_form, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fromTimeEditText = view.findViewById(R.id.from_time_edit_text);
        toTimeEditText = view.findViewById(R.id.to_time_edit_text);
        dateRangeButton = view.findViewById(R.id.date_range_button);

        // Initialize Date Range Button Text
        updateDateRangeButtonText();

        // Set Click Listeners for Time Pickers
        fromTimeEditText.setOnClickListener(v -> showTimePicker(fromTimeEditText));
        toTimeEditText.setOnClickListener(v -> showTimePicker(toTimeEditText));

        // Set Click Listener for Date Picker
        dateRangeButton.setOnClickListener(v -> showDateRangePicker());
    }

    private void showTimePicker(final TextInputEditText timeEditText) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                requireContext(),
                (view, hourOfDay, minute) -> {
                    String formattedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                    timeEditText.setText(formattedTime);
                },
                8, // Default Hour
                0, // Default Minute
                true // 24-hour format
        );
        timePickerDialog.show();
    }

    private void showDateRangePicker() {
        // Open FROM Date Picker
        new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
            fromDate.set(year, month, dayOfMonth);

            // Open TO Date Picker after FROM is selected
            new DatePickerDialog(requireContext(), (view1, year1, month1, dayOfMonth1) -> {
                toDate.set(year1, month1, dayOfMonth1);
                updateDateRangeButtonText();
            }, toDate.get(Calendar.YEAR), toDate.get(Calendar.MONTH), toDate.get(Calendar.DAY_OF_MONTH)).show();

        }, fromDate.get(Calendar.YEAR), fromDate.get(Calendar.MONTH), fromDate.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updateDateRangeButtonText() {
        String formattedDateRange = dateFormat.format(fromDate.getTime()) + " – " + dateFormat.format(toDate.getTime());
        dateRangeButton.setText(formattedDateRange);
    }

    public void addAvailiblity(){
        String startDate = String.valueOf(fromTimeEditText.getText());
    }
}
