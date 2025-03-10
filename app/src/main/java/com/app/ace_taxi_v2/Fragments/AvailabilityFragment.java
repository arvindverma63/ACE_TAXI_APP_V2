package com.app.ace_taxi_v2.Fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.net.Uri;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.app.ace_taxi_v2.JobModals.BottomSheetDialogs;
import com.app.ace_taxi_v2.Logic.AvailabilitiesApi;
import com.app.ace_taxi_v2.Logic.AvailabilityAddApi;
import com.app.ace_taxi_v2.Logic.SessionManager;
import com.app.ace_taxi_v2.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AvailabilityFragment extends Fragment {

    private MaterialCardView dateButton;
    private MaterialCardView custom_button;
    private Calendar selectedDate = Calendar.getInstance();
    private SimpleDateFormat apiDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
    private SimpleDateFormat displayDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm", Locale.getDefault());
    private String selectedDateStringForAPI; // Stores formatted date for API
    public RecyclerView recyclerView;
    private TextView dateText;
    public SessionManager sessionManager;
    public MaterialCardView am_school_button,pm_school_button,am_pm_school_button,unavailable_button,view_all;
    public Button close;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_availability, container, false);

        // Initialize UI Elements
        dateButton = rootView.findViewById(R.id.date_button);
        dateButton.setOnClickListener(v -> showDatePicker());
        custom_button = rootView.findViewById(R.id.custom_button);
        recyclerView = rootView.findViewById(R.id.recyclar_view);
        am_school_button = rootView.findViewById(R.id.am_school_button);
        unavailable_button = rootView.findViewById(R.id.unavailable_button);
        pm_school_button = rootView.findViewById(R.id.pm_school_button);
        am_pm_school_button = rootView.findViewById(R.id.am_pm_school_button);
        dateText = rootView.findViewById(R.id.dateText);
        view_all = rootView.findViewById(R.id.view_all);
        pm_school_button.setOnClickListener(v -> pmSchoolOnly());
        am_school_button.setOnClickListener(v -> amSchoolOnly());
        unavailable_button.setOnClickListener(v -> setUnavailable());
        am_pm_school_button.setOnClickListener(v -> bothOnly());
        close = rootView.findViewById(R.id.btnClose);
        close.setOnClickListener(v -> {
            Fragment selected = new HomeFragment();
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, selected);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commitAllowingStateLoss();
        });

        view_all.setOnClickListener(v -> {
            Fragment selected = new ListAvailabillity();
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, selected);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commitAllowingStateLoss();
        });


        sessionManager = new SessionManager(getContext());

       renderList();

        // Navigate to Customer Form
        custom_button.setOnClickListener(view -> {
            Fragment selected = new CustomerForm();
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, selected);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commitAllowingStateLoss();
        });

        updateDateButtonText(); // Set default date on button
        return rootView;
    }

    public void renderList(){
        AvailabilitiesApi availabilitiesApi = new AvailabilitiesApi(getContext());
        availabilitiesApi.getAvailabilities(recyclerView,getView());
    }

    // Show Date Picker and then Time Picker
    private void showDatePicker() {
        new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
            selectedDate.set(year, month, dayOfMonth);
            showTimePicker(); // After selecting date, open time picker
        }, selectedDate.get(Calendar.YEAR), selectedDate.get(Calendar.MONTH), selectedDate.get(Calendar.DAY_OF_MONTH)).show();
    }

    // Show Time Picker
    private void showTimePicker() {
        new TimePickerDialog(requireContext(), (view, hourOfDay, minute) -> {
            selectedDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
            selectedDate.set(Calendar.MINUTE, minute);
            selectedDate.set(Calendar.SECOND, 0);
            selectedDate.set(Calendar.MILLISECOND, 0);

            updateDateButtonText(); // Update button with formatted date
        }, selectedDate.get(Calendar.HOUR_OF_DAY), selectedDate.get(Calendar.MINUTE), true).show();
    }

    // Update button text (Readable format) and store API-formatted date
    private void updateDateButtonText() {
        selectedDateStringForAPI = apiDateFormat.format(selectedDate.getTime()); // API Format
        dateText.setText(displayDateFormat.format(selectedDate.getTime())); // Display Format
    }

    // Send formatted date to API
    public void amSchoolOnly() {
        int userId = sessionManager.getUserId();

        if (selectedDateStringForAPI == null) {
            Toast.makeText(getContext(), "Please select a date first!", Toast.LENGTH_SHORT).show();
            return;
        }

        AvailabilityAddApi availabilityAddApi = new AvailabilityAddApi(getContext());
        availabilityAddApi.addAvailability(userId, selectedDateStringForAPI, "07:30", "09:15", true, 1, "Am only");

        try {
            Thread.sleep(2000);
            renderList();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    public void bothOnly(){
        int userId = sessionManager.getUserId();

        if (selectedDateStringForAPI == null) {
            Toast.makeText(getContext(), "Please select a date first!", Toast.LENGTH_SHORT).show();
            return;
        }

        AvailabilityAddApi availabilityAddApi = new AvailabilityAddApi(getContext());
        availabilityAddApi.addAvailability(userId, selectedDateStringForAPI, "14:30", "16:15", true, 1, "PM only");
        availabilityAddApi.addAvailability(userId, selectedDateStringForAPI, "07:30", "09:15", true, 1, "Am only");

        try {
            Thread.sleep(2000);
            renderList();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void pmSchoolOnly(){
        int userId = sessionManager.getUserId();

        if (selectedDateStringForAPI == null) {
            Toast.makeText(getContext(), "Please select a date first!", Toast.LENGTH_SHORT).show();
            return;
        }

        AvailabilityAddApi availabilityAddApi = new AvailabilityAddApi(getContext());
        availabilityAddApi.addAvailability(userId, selectedDateStringForAPI, "14:30", "16:15", true, 1, "PM only");

        try {
            Thread.sleep(2000);
            renderList();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void setUnavailable(){
        int userId = sessionManager.getUserId();

        if (selectedDateStringForAPI == null) {
            Toast.makeText(getContext(), "Please select a date first!", Toast.LENGTH_SHORT).show();
            return;
        }

        AvailabilityAddApi availabilityAddApi = new AvailabilityAddApi(getContext());
        availabilityAddApi.addAvailability(userId, selectedDateStringForAPI, "00:00", "23:59", true, 1, "Unavailable All Day");

        Toast.makeText(getContext(), "added successfully!", Toast.LENGTH_SHORT).show();
        try {
            Thread.sleep(2000);
            renderList();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}

