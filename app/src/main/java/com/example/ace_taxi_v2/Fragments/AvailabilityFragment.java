package com.example.ace_taxi_v2.Fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.ace_taxi_v2.Logic.AvailabilityAddApi;
import com.example.ace_taxi_v2.Logic.SessionManager;
import com.example.ace_taxi_v2.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AvailabilityFragment extends Fragment {

    private MaterialButton dateButton;
    private Button custom_button;
    private Calendar selectedDate = Calendar.getInstance();
    private SimpleDateFormat isoDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_availability, container, false);
        dateButton = rootView.findViewById(R.id.date_button);
        dateButton.setOnClickListener(v -> showDatePicker());
        custom_button = rootView.findViewById(R.id.custom_button);

        custom_button.setOnClickListener(view -> {
            Fragment selected = new CustomerForm();
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, selected);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

        updateDateButtonText();
        return rootView;
    }

    private void showDatePicker() {
        new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
            selectedDate.set(year, month, dayOfMonth, 0, 0, 0);
            updateDateButtonText();
        }, selectedDate.get(Calendar.YEAR), selectedDate.get(Calendar.MONTH), selectedDate.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updateDateButtonText() {
        String formattedDate = isoDateFormat.format(selectedDate.getTime());
        dateButton.setText(formattedDate);
    }
}
