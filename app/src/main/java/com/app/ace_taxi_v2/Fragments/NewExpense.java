package com.app.ace_taxi_v2.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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
    private EditText amountEditText;
    private Button recordButton, viewButton;
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
            return view;
        }

        // Toolbar Navigation
        MaterialToolbar toolbar = view.findViewById(R.id.toolbar_header);
        toolbar.setNavigationOnClickListener(v -> {
            Fragment selectedFragment = new HomeFragment();
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, selectedFragment);
            fragmentTransaction.commit();
        });

        // View Expenses Button
        viewButton = view.findViewById(R.id.view_button);
        viewButton.setOnClickListener(v -> {
            Fragment selectedFragment = new ViewExpenses();
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, selectedFragment);
            fragmentTransaction.commit();
        });

        // Initialize Views
        dateEditText = view.findViewById(R.id.dateEditText);
        descriptionEditText = view.findViewById(R.id.descriptionEditText);
        amountEditText = view.findViewById(R.id.amountEditText);
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
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
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

    private void addRecord() {
        // Get values from input fields
        String originalDate = dateEditText.getText().toString().trim();
        String amountText = amountEditText.getText().toString().trim();
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
            amountEditText.setError("Amount is required");
            amountEditText.requestFocus();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountText);
            if (amount <= 0) {
                amountEditText.setError("Enter a valid amount");
                amountEditText.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            amountEditText.setError("Invalid number format");
            amountEditText.requestFocus();
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
                    amountEditText.setText("");
                    descriptionEditText.setText("");
                    categoryDropdown.setText("", false);
                    // Reset dropdown to default
                    if (categoryDropdown.getAdapter() instanceof CustomAdapter) {
                        ((CustomAdapter) categoryDropdown.getAdapter()).setSelectedPosition(0);
                        categoryDropdown.setText(getResources().getStringArray(R.array.categories)[0], false);
                    }
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
        switch (category) {
            case "Fuel":
                return 0;
            case "Part":
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
            case "Others":
                return 8;
            default:
                return -1;
        }
    }

    public class CustomAdapter extends ArrayAdapter<String> {
        private int selectedPosition = -1;

        public CustomAdapter(Context context, int resource, String[] objects) {
            super(context, resource, objects);
        }

        public void setSelectedPosition(int position) {
            selectedPosition = position;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.dropdown_item, parent, false);
                holder = new ViewHolder();
                holder.textView = convertView.findViewById(R.id.textView);
                holder.checkMark = convertView.findViewById(R.id.checkMark);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            String item = getItem(position);
            holder.textView.setText(item != null ? item : "");
            holder.checkMark.setVisibility(position == selectedPosition ? View.VISIBLE : View.GONE);

            return convertView;
        }

        @Override
        public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            return getView(position, convertView, parent); // Reuse getView for dropdown items
        }

        class ViewHolder {
            TextView textView;
            ImageView checkMark;
        }
    }

    private void setDataToAdapter() {
        Context context = getContext();
        if (context == null) return;

        // Load categories from resources
        String[] categories = context.getResources().getStringArray(R.array.categories);

        // Use CustomAdapter
        CustomAdapter adapter = new CustomAdapter(context, R.layout.dropdown_item, categories);
        categoryDropdown.setAdapter(adapter);

        // Set default selection
        if (categories.length > 0) {
            categoryDropdown.setText(categories[0], false); // Set "Fuel" as default
            adapter.setSelectedPosition(0); // Show checkmark for default
        }

        // Handle item selection
        categoryDropdown.setOnItemClickListener((parent, view, position, id) -> {
            String selectedCategory = (String) parent.getItemAtPosition(position);
            categoryDropdown.setText(selectedCategory, false);
            ((CustomAdapter) parent.getAdapter()).setSelectedPosition(position);
        });
    }

    private String convertToISO8601(String inputDate) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        outputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        try {
            Date date = inputFormat.parse(inputDate);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}