package com.example.ace_taxi_v2.Fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ace_taxi_v2.Logic.AvailabilitiesApi;
import com.example.ace_taxi_v2.Logic.AvailabilityAddApi;
import com.example.ace_taxi_v2.Logic.SessionManager;
import com.example.ace_taxi_v2.R;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CustomerForm extends Fragment {

    private TextInputEditText fromTimeEditText, toTimeEditText;
    private Button dateButton;
    private Calendar selectedDate = Calendar.getInstance();
    private SimpleDateFormat isoDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
    public SessionManager sessionManager;
    public TextInputEditText note_edit_text;
    public Button add_ava, add_un,unavailable_all_day;
    public MaterialCheckBox give_or_take;
    public RecyclerView recyclerView;

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
        dateButton = view.findViewById(R.id.date_button);
        sessionManager = new SessionManager(getContext());
        note_edit_text = view.findViewById(R.id.note_edit_text);
        add_ava = view.findViewById(R.id.add_ava);
        add_un = view.findViewById(R.id.add_un);
        give_or_take = view.findViewById(R.id.give_or_take);
        unavailable_all_day = view.findViewById(R.id.unavailable_all_day);

        add_ava.setOnClickListener(v -> addAvailability());
        add_un.setOnClickListener(v -> unAvailability());
        unavailable_all_day.setOnClickListener(v -> unavailableAllDay());

        recyclerView = view.findViewById(R.id.recyclar_view);
        renderList();

        // Initialize Date Button Text
        updateDateButtonText();

        // Set Click Listeners for Time Pickers
        fromTimeEditText.setOnClickListener(v -> showTimePicker(fromTimeEditText));
        toTimeEditText.setOnClickListener(v -> showTimePicker(toTimeEditText));

        // Set Click Listener for Date Picker
        dateButton.setOnClickListener(v -> showDatePicker());
    }

    public void renderList(){
        AvailabilitiesApi availabilitiesApi = new AvailabilitiesApi(getContext());
        availabilitiesApi.getAvailablities(recyclerView);
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

    public void addAvailability() {
        String startDate = dateButton.getText().toString();
        String from = fromTimeEditText.getText().toString();
        String to = toTimeEditText.getText().toString();
        String note = note_edit_text.getText().toString();
        boolean giveOrTake = give_or_take.isChecked();

        if (startDate.isEmpty() || from.isEmpty() || to.isEmpty()) {
            Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_LONG).show();
        } else {
            int userId = sessionManager.getUserId();
            AvailabilityAddApi availabilityAddApi = new AvailabilityAddApi(getContext());
            availabilityAddApi.addAvailability(userId, startDate, from, to, giveOrTake, 1, note);
        }
        try {
            Thread.sleep(2000);
            renderList();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    public void unAvailability() {
        String startDate = dateButton.getText().toString();
        String from = fromTimeEditText.getText().toString();
        String to = toTimeEditText.getText().toString();
        String note = note_edit_text.getText().toString();
        boolean giveOrTake = give_or_take.isChecked();

        if (startDate.isEmpty() || from.isEmpty() || to.isEmpty()) {
            Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_LONG).show();
        } else {
            int userId = sessionManager.getUserId();
            AvailabilityAddApi availabilityAddApi = new AvailabilityAddApi(getContext());
            availabilityAddApi.addAvailability(userId, startDate, from, to, giveOrTake, 2, note);
        }
        try {
            Thread.sleep(2000);
            renderList();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void unavailableAllDay(){
        int userId = sessionManager.getUserId();
        String startDate = dateButton.getText().toString();
        AvailabilityAddApi availabilityAddApi = new AvailabilityAddApi(getContext());
        availabilityAddApi.addAvailability(userId, startDate, "00:00", "23:59", true, 2, "unavailable All Day");
        try {
            Thread.sleep(2000);
            renderList();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
