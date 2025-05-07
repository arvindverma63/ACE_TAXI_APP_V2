package com.app.ace_taxi_v2.Fragments.AvailablitiyFragmentAction;

import android.app.TimePickerDialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.ace_taxi_v2.Fragments.Adapters.AvailablitiesAdapter;
import com.app.ace_taxi_v2.Logic.AvailabilitiesApi;
import com.app.ace_taxi_v2.Logic.AvailabilityAddApi;
import com.app.ace_taxi_v2.Logic.SessionManager;
import com.app.ace_taxi_v2.Logic.Worker.AvailabiltiesApiResponse;
import com.app.ace_taxi_v2.Models.AvailabilityResponse;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CustomAvailability implements DateTimeSelector.OnDateSelectedListener {
    private final Context context;
    private MaterialButton fromTimeEditText, toTimeEditText;
    private TextView dateText;
    private Calendar selectedDate = Calendar.getInstance();
    private final SimpleDateFormat displayDateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
    private final SimpleDateFormat isoDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private SessionManager sessionManager;
    private TextInputEditText noteEditText;
    private MaterialButton addAva, addUn, unavailableButton;
    private MaterialCheckBox giveOrTake;
    private RecyclerView recyclerView;
    private ImageView sideMenu;
    private DateTimeSelector dateTimeSelector;
    public String btnSelectDate;


    public CustomAvailability(Context context) {
        this.context = context;
    }

    public void initViews(
            MaterialButton fromTimeEditText,
            MaterialButton toTimeEditText,
            TextView dateText,
            TextInputEditText noteEditText,
            MaterialButton addAva,
            MaterialButton addUn,
            MaterialButton unavailableButton,
            MaterialCheckBox giveOrTake,
            RecyclerView recyclerView,
            ImageView sideMenu,
            SessionManager sessionManager,
            DateTimeSelector dateTimeSelector
    ) {
        this.fromTimeEditText = fromTimeEditText;
        this.toTimeEditText = toTimeEditText;
        this.dateText = dateText;
        this.noteEditText = noteEditText;
        this.addAva = addAva;
        this.addUn = addUn;
        this.unavailableButton = unavailableButton;
        this.giveOrTake = giveOrTake;
        this.recyclerView = recyclerView;
        this.sideMenu = sideMenu;
        this.sessionManager = sessionManager;
        this.dateTimeSelector = dateTimeSelector;



        // Set up click listeners
        fromTimeEditText.setOnClickListener(v -> showTimePicker(fromTimeEditText));
        toTimeEditText.setOnClickListener(v -> showTimePicker(toTimeEditText));
        addAva.setOnClickListener(v -> addAvailability());
        addUn.setOnClickListener(v -> unAvailability());
        unavailableButton.setOnClickListener(v -> unavailableAllDay());
    }


    public void renderListAva() {
        Log.e("render method call","render "+btnSelectDate);
        AvailabiltiesApiResponse availabilitiesApi = new AvailabiltiesApiResponse(context);
        availabilitiesApi.getAvailabilities(new AvailabiltiesApiResponse.AvailabilityCallback() {
            @Override
            public void onSuccess(List<AvailabilityResponse.Driver> drivers) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                AvailablitiesAdapter adapter = new AvailablitiesAdapter(drivers, context);
                recyclerView.setAdapter(adapter);
                String date = isoDateFormat.format(btnSelectDate);
                Log.d("formated Date : ",date+"formated date");
                adapter.updateListForDate(isoDateFormat.format(btnSelectDate));
            }

            @Override
            public void onError(String errorMessage) {

            }
        });
    }

    private void showTimePicker(final MaterialButton timeEditText) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                context,
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


    @Override
    public void onDateSelected(String selectedDate) {
        this.btnSelectDate = selectedDate;
        Log.d("CustomAvailability", "Date updated to: " + selectedDate+"");
    }

    public void addAvailability() {
        try {
            String startDate = btnSelectDate;
            String from = fromTimeEditText.getText().toString();
            String to = toTimeEditText.getText().toString();
            String note = noteEditText.getText().toString();
            boolean giveOrTake = this.giveOrTake.isChecked();

            Log.d("startDate for addAvailability", startDate+"  btn Date");


            int userId = sessionManager.getUserId();
            AvailabilityAddApi availabilityAddApi = new AvailabilityAddApi(context);
            availabilityAddApi.addAvailability(userId, startDate, from, to, giveOrTake, 1, note);
            renderListAva();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Error adding availability: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void unAvailability() {
        String startDate = btnSelectDate;
        String from = fromTimeEditText.getText().toString();
        String to = toTimeEditText.getText().toString();
        String note = noteEditText.getText().toString();
        boolean giveOrTake = this.giveOrTake.isChecked();

        if (startDate.isEmpty() || from.isEmpty() || to.isEmpty()) {
            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_LONG).show();
            return;
        }

        int userId = sessionManager.getUserId();
        AvailabilityAddApi availabilityAddApi = new AvailabilityAddApi(context);
        availabilityAddApi.addAvailability(userId, startDate, from, to, giveOrTake, 2, note);
        renderListAva();
    }

    public void unavailableAllDay() {
        int userId = sessionManager.getUserId();
        String startDate = isoDateFormat.format(selectedDate.getTime());
        AvailabilityAddApi availabilityAddApi = new AvailabilityAddApi(context);
        availabilityAddApi.addAvailability(userId, startDate, "00:00", "23:59", true, 2, "Unavailable All Day");
    }


}