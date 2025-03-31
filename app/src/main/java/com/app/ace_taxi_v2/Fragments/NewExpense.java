package com.app.ace_taxi_v2.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.app.ace_taxi_v2.Activity.LoginActivity;
import com.app.ace_taxi_v2.Components.CustomDialog;
import com.app.ace_taxi_v2.Logic.AddExpenses;
import com.app.ace_taxi_v2.Logic.SessionManager;
import com.app.ace_taxi_v2.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialDatePicker.Builder;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class NewExpense extends Fragment {

    private EditText dateEditText;
    private EditText descriptionEditText;
    private EditText amout;
    private Button recordButton,view_button;
    private AutoCompleteTextView categoryDropdown;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_expense, container, false);

        // Session Management
        SessionManager sessionManager = new SessionManager(requireContext());
        if (!sessionManager.isLoggedIn()) {
            Intent intent = new Intent(getContext(), LoginActivity.class);
            startActivity(intent);
            requireActivity().finish();
        }

        // Toolbar Navigation
        MaterialToolbar toolbar = view.findViewById(R.id.toolbar_header);
        toolbar.setNavigationOnClickListener(v -> {
            Fragment selectedFragment = new ProfileFragment();
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, selectedFragment);
            fragmentTransaction.commit();
        });

        view_button = view.findViewById(R.id.view_button);
        view_button.setOnClickListener(v -> {
            Fragment selectedFragment = new ViewExpenses();
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, selectedFragment);
            fragmentTransaction.commit();
        });

        // Initialize Views
        dateEditText = view.findViewById(R.id.dateEditText);
        descriptionEditText = view.findViewById(R.id.descriptionEditText);
        amout = view.findViewById(R.id.amountEditText);
        recordButton = view.findViewById(R.id.recordButton);
        categoryDropdown = view.findViewById(R.id.categoryDropdown);

        // Populate the dropdown menu
        setDataToAdapter();

        // Set current date as default
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String currentDate = sdf.format(Calendar.getInstance().getTime());
        dateEditText.setText(currentDate);

        // Set up Date Picker
        dateEditText.setOnClickListener(v -> showDatePicker());

        // Set up Record Button Listener
        recordButton.setOnClickListener(v -> addRecord());

        return view;
    }

    private void showDatePicker() {
        // Create Date Picker Constraints (Disable Past Dates)
        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder()
                .setValidator(DateValidatorPointForward.now());

        // Initialize MaterialDatePicker
        MaterialDatePicker<Long> datePicker = Builder.datePicker()
                .setTitleText("Select a Date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds()) // Default to today
                .setCalendarConstraints(constraintsBuilder.build())
                .build();

        // Show the Date Picker Dialog
        datePicker.show(getParentFragmentManager(), "DATE_PICKER");

        // Set the selected date in the EditText
        datePicker.addOnPositiveButtonClickListener(selection -> {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String formattedDate = sdf.format(selection);
            dateEditText.setText(formattedDate);
        });
    }

    public void addRecord() {
        // Get values from input fields
        String originalDate = dateEditText.getText().toString().trim();
        String amountText = amout.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        String selectedCategory = categoryDropdown.getText().toString().trim();

        // Validate Date
        if (originalDate.isEmpty()) {
            dateEditText.setError("Please select a date");
            dateEditText.requestFocus();
            return;
        }

        // Convert to ISO 8601 format
        String isoDate = convertToISO8601(originalDate);
        if (isoDate == null) {
            Toast.makeText(getContext(), "Invalid date format", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate Amount
        if (amountText.isEmpty()) {
            amout.setError("Amount is required");
            amout.requestFocus();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountText);
            if (amount <= 0) {
                amout.setError("Enter a valid amount");
                amout.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            amout.setError("Invalid number format");
            amout.requestFocus();
            return;
        }


        // Validate Category
        if (selectedCategory.isEmpty() || getCategoryId(selectedCategory) == -1) {
            categoryDropdown.setError("Please select a valid category");
            categoryDropdown.requestFocus();
            return;
        }

        int categoryId = getCategoryId(selectedCategory);

        // Proceed with API Call
        AddExpenses addExpenses = new AddExpenses(getContext());
        addExpenses.addExpnses(isoDate, description, amount, categoryId, new AddExpenses.ExpensesCallback() {
            @Override
            public void onSuccess(Boolean ok) {
                if (ok) {
                    Toast.makeText(getContext(), "Expense recorded successfully", Toast.LENGTH_SHORT).show();
                    // Clear input fields
                    amout.setText("");
                    descriptionEditText.setText("");
                    categoryDropdown.setText("");
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }



    /**
     * Maps the selected category string to its corresponding category ID.
     */
    private int getCategoryId(String category) {
        // Adjust the mapping as needed.
        switch (category) {
            case "Fuel":
                return 0;
            case "Parts":
                return 1;
            case "Insurance":
                return 2;
            case "MOT":
                return 3;
            case "DBS":
                return 4;
            case "Vehicle Badge":
                return 5;
            case "Maintenance":
                return 6;
            case "Certification":
                return 7;
            case "Other":
                return 8;
            default:
                // Return a default or invalid id if the category is not recognized.
                return -1;
        }
    }


    public void setDataToAdapter() {
        // Define your categories array
        String[] categories = new String[] {
                "Fuel",         // 0
                "Parts",        // 1
                "Insurance",
                "MOT",
                "DBS",
                "Vehicle Badge",
                "Maintenance",
                "Certification",
                "Other"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_dropdown_item_1line,
                categories
        );
        categoryDropdown.setAdapter(adapter);
    }
    public String convertToISO8601(String inputDate) {
        // Define the input format (for example, if input is "01/02/2025")
        SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        // Define the output ISO 8601 format.
        // The pattern "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'" produces an output like "2025-02-01T06:16:21.793Z"
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        // Set timezone to UTC so that the 'Z' (Zulu time) is correct.
        outputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        try {
            // Parse the input date
            Date date = inputFormat.parse(inputDate);
            // Format the date into ISO 8601 string
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
