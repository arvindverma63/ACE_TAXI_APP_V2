package com.example.ace_taxi_v2.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.ace_taxi_v2.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AvailabilityFragment extends Fragment {

    private MaterialButton dateRangeButton;
    private Button custom_button;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_availability, container, false);
        // Initialize the MaterialButton
        dateRangeButton = rootView.findViewById(R.id.date_range_button);
        // Set click listener to show Date Picker
        dateRangeButton.setOnClickListener(v -> showDateRangePicker());
        custom_button = rootView.findViewById(R.id.custom_button);
        custom_button.setOnClickListener(view -> {
            // Create an instance of the fragment you want to navigate to
            Fragment selected = new CustomerForm();

            // Replace the current fragment with the new one
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            // Add the new fragment with a transition animation if needed
            fragmentTransaction.replace(R.id.fragment_container, selected);
            fragmentTransaction.addToBackStack(null); // Add to back stack to enable back navigation
            fragmentTransaction.commit(); // Commit the transaction
        });

        return rootView;
    }

    private void showDateRangePicker() {
        // Create the Date Range Picker
        MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
        builder.setTitleText("Select Date Range");

        // Add Calendar Constraints (optional)
        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
        builder.setCalendarConstraints(constraintsBuilder.build());

        // Build the Date Picker
        MaterialDatePicker<Pair<Long, Long>> datePicker = builder.build();

        // Show the Date Picker
        datePicker.show(getParentFragmentManager(), "DATE_PICKER");

        // Handle Positive Button Click
        datePicker.addOnPositiveButtonClickListener(selection -> {
            Pair<Long, Long> dateRange = selection;
            if (dateRange != null) {
                Long startDate = dateRange.first;
                Long endDate = dateRange.second;

                // Format Dates
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                String formattedStartDate = sdf.format(new Date(startDate));
                String formattedEndDate = sdf.format(new Date(endDate));

                // Update Button Text
                dateRangeButton.setText(formattedStartDate + " – " + formattedEndDate);
            }
        });

        // Handle Negative Button Click
        datePicker.addOnNegativeButtonClickListener(dialog ->
                Toast.makeText(requireContext(), "Selection Cancelled", Toast.LENGTH_SHORT).show());

        // Handle Cancel Event
        datePicker.addOnCancelListener(dialog ->
                Toast.makeText(requireContext(), "Date Picker Cancelled", Toast.LENGTH_SHORT).show());
    }
}
