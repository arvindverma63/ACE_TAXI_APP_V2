package com.app.ace_taxi_v2.Fragments.AvailablitiyFragmentAction;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.ace_taxi_v2.Fragments.Adapters.AllDriverAvailAdapter;
import com.app.ace_taxi_v2.Fragments.Adapters.AvailablitiesAdapter;
import com.app.ace_taxi_v2.Logic.Worker.AvailabiltiesApiResponse;
import com.app.ace_taxi_v2.Logic.availability.AllDriverAvailApi;
import com.app.ace_taxi_v2.Models.AllDriverAvailabilityResponse;
import com.app.ace_taxi_v2.Models.AvailabilityResponse;
import com.app.ace_taxi_v2.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class DateTimeSelector {

    public Context context;
    public TextView dateText,all_driver_selectDate;
    public LinearLayout dateButton;
    public LinearLayout buttonContainer;
    public Calendar selectedDate;
    public SimpleDateFormat apiDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
    public SimpleDateFormat displayDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    public String selectedDateStringForAPI;
    public MaterialButton selectedButton;
    public Calendar selectedButtonDate;
    public ImageView week_prev, week_next;
    public MaterialTextView selectedDateAvail;
    public RecyclerView recyclerView;
    private OnDateSelectedListener dateSelectedListener;
    public RecyclerView recycler_view_all_driver;
    private AllDriverAvailAdapter allDriverAvailAdapter;
    private static final String TAG = "DateTimeSelector";

    public interface OnDateSelectedListener {
        void onDateSelected(String selectedDate);
    }

    public DateTimeSelector() {
    }

    public void init(Context context, TextView dateText, LinearLayout dateButton, LinearLayout buttonContainer,
                     ImageView week_prev, ImageView week_next, MaterialTextView selectedDateAvail, RecyclerView recyclerView, RecyclerView recycler_view_all_driver,TextView all_driver_selectDate, OnDateSelectedListener onDateSelectedListener) {
        this.context = context;
        this.dateText = dateText;
        this.dateButton = dateButton;
        this.buttonContainer = buttonContainer;
        this.week_prev = week_prev;
        this.week_next = week_next;
        this.selectedDateAvail = selectedDateAvail;
        this.recyclerView = recyclerView;
        this.recycler_view_all_driver = recycler_view_all_driver;
        this.dateSelectedListener = onDateSelectedListener;
        selectedDate = Calendar.getInstance();
        this.all_driver_selectDate = all_driver_selectDate;
        selectedButtonDate = Calendar.getInstance();
        allDriverAvailAdapter = new AllDriverAvailAdapter(context, new ArrayList<>());
        recycler_view_all_driver.setLayoutManager(new LinearLayoutManager(context));
        recycler_view_all_driver.setAdapter(allDriverAvailAdapter);
        updateDateButtonText();
        buttonController();
        Log.d(TAG, "DateTimeSelector initialized");
    }

    public void showDatePicker() {
        try {
            new DatePickerDialog(context, (view, year, month, dayOfMonth) -> {
                selectedDate.set(year, month, dayOfMonth);
                showTimePicker();
            }, selectedDate.get(Calendar.YEAR), selectedDate.get(Calendar.MONTH), selectedDate.get(Calendar.DAY_OF_MONTH)).show();
            Log.d(TAG, "Showing date picker");
        } catch (Exception e) {
            Toast.makeText(context, "Error showing date picker: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Error showing date picker: " + e.getMessage(), e);
        }
    }

    private void showTimePicker() {
        try {
            new TimePickerDialog(context, (view, hourOfDay, minute) -> {
                selectedDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
                selectedDate.set(Calendar.MINUTE, minute);
                selectedDate.set(Calendar.SECOND, 0);
                selectedDate.set(Calendar.MILLISECOND, 0);
                updateDateButtonText();
                buttonController();
                Log.d(TAG, "Time selected: " + hourOfDay + ":" + minute);
            }, selectedDate.get(Calendar.HOUR_OF_DAY), selectedDate.get(Calendar.MINUTE), true).show();
            Log.d(TAG, "Showing time picker");
        } catch (Exception e) {
            Toast.makeText(context, "Error showing time picker: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Error showing time picker: " + e.getMessage(), e);
        }
    }

    public void updateDateButtonText() {
        try {
            Calendar endDate = (Calendar) selectedDate.clone();
            endDate.add(Calendar.DATE, 6);

            String range = displayDateFormat.format(selectedDate.getTime()) + " to " +
                    displayDateFormat.format(endDate.getTime());

            dateText.setText(range);

            selectedButtonDate.setTime(selectedDate.getTime());

            selectedDateStringForAPI = apiDateFormat.format(selectedButtonDate.getTime());
            Log.d(TAG, "Updated date text: " + range + ", API date: " + selectedDateStringForAPI);
        } catch (Exception e) {
            Toast.makeText(context, "Error updating date text: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Error updating date text: " + e.getMessage(), e);
        }
    }

    public void updateWeekPrev() {
        try {
            selectedDate.add(Calendar.DATE, -7);
            updateDateButtonText();
            buttonController();
            Log.d(TAG, "Moved to previous week: " + displayDateFormat.format(selectedDate.getTime()));
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Error updating previous week: " + e.getMessage(), e);
        }
    }

    public void updateWeekNext() {
        try {
            selectedDate.add(Calendar.DATE, 7);
            updateDateButtonText();
            buttonController();
            Log.d(TAG, "Moved to next week: " + displayDateFormat.format(selectedDate.getTime()));
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Error updating next week: " + e.getMessage(), e);
        }
    }

    public String getSelectedDateStringForAPI() {
        return selectedDateStringForAPI;
    }

    public String getSelectedButtonDateForAPI() {
        return apiDateFormat.format(selectedButtonDate.getTime());
    }

    public void buttonController() {
        buttonContainer.removeAllViews();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE dd/MM", Locale.getDefault());
        String displayDate = displayDateFormat.format(selectedButtonDate.getTime());
        all_driver_selectDate.setText(displayDate);
        loadAvailablities(displayDate);
        selectedDateAvail.setText(displayDateFormat.format(selectedButtonDate.getTime()));
        for (int i = 0; i < 7; i++) {
            Calendar tempCalendar = (Calendar) selectedDate.clone();
            tempCalendar.add(Calendar.DATE, i);

            MaterialButton button = new MaterialButton(buttonContainer.getContext());
            button.setText(sdf.format(tempCalendar.getTime()));
            button.setCornerRadius(8);
            button.setStrokeColor(ContextCompat.getColorStateList(buttonContainer.getContext(), R.color.red));
            button.setStrokeWidth(2);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    160,
                    120
            );
            params.setMargins(8, 8, 8, 8);
            button.setLayoutParams(params);

            if (i == 0) {
                selectedButton = button;
                button.setBackgroundTintList(ContextCompat.getColorStateList(buttonContainer.getContext(), R.color.red));
                button.setTextColor(ContextCompat.getColor(buttonContainer.getContext(), R.color.white));
                selectedButtonDate.setTime(tempCalendar.getTime());
                selectedDateStringForAPI = apiDateFormat.format(selectedButtonDate.getTime());
            } else {
                button.setBackgroundTintList(ContextCompat.getColorStateList(buttonContainer.getContext(), R.color.white));
                button.setTextColor(ContextCompat.getColor(buttonContainer.getContext(), R.color.red));
            }

            Calendar finalTempCalendar = (Calendar) tempCalendar.clone();
            button.setOnClickListener(v -> {
                if (selectedButton != null) {
                    selectedButton.setBackgroundTintList(ContextCompat.getColorStateList(buttonContainer.getContext(), R.color.white));
                    selectedButton.setTextColor(ContextCompat.getColor(buttonContainer.getContext(), R.color.red));
                }

                selectedButton = button;
                button.setBackgroundTintList(ContextCompat.getColorStateList(buttonContainer.getContext(), R.color.red));
                button.setTextColor(ContextCompat.getColor(buttonContainer.getContext(), R.color.white));

                selectedButtonDate.setTime(finalTempCalendar.getTime());
                selectedDateStringForAPI = apiDateFormat.format(selectedButtonDate.getTime());
                String displayDateupdate = displayDateFormat.format(selectedButtonDate.getTime());
                Log.d("FilterDebug", "Button clicked. Selected date: " + displayDateupdate);
                selectedDateAvail.setText(displayDateupdate);
                all_driver_selectDate.setText(displayDateupdate);
                loadAvailablities(displayDateupdate);

                if (dateSelectedListener != null) {
                    dateSelectedListener.onDateSelected(selectedDateStringForAPI);
                }
            });

            if (dateSelectedListener != null) {
                dateSelectedListener.onDateSelected(selectedDateStringForAPI);
            }
            buttonContainer.addView(button);
        }
        Log.d(TAG, "Button controller updated with 7 date buttons");
    }

    public void loadAvailablities(String displayDate) {
        AvailabiltiesApiResponse availabiltiesApiResponse = new AvailabiltiesApiResponse(context);
        availabiltiesApiResponse.getAvailabilities(new AvailabiltiesApiResponse.AvailabilityCallback() {
            @Override
            public void onSuccess(List<AvailabilityResponse.Driver> drivers) {
                Log.d("FilterDebug", "API returned " + drivers.size() + " drivers");
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                AvailablitiesAdapter adapter = new AvailablitiesAdapter(drivers, context);
                recyclerView.setAdapter(adapter);
                adapter.updateListForDate(displayDate);
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("FilterDebug", "API error: " + errorMessage);
                Toast.makeText(context, "Failed to load availabilities: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        AllDriverAvailApi allDriverAvailApi = new AllDriverAvailApi(context);
        allDriverAvailApi.getResponse(displayDate, new AllDriverAvailApi.AllDriverAvailCallback() {
            @Override
            public void onResponse(List<AllDriverAvailabilityResponse> response) {
                Log.d(TAG, "AllDriverAvailApi returned " + response.size() + " records for date: " + displayDate);
                allDriverAvailAdapter.updateData(response);
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e(TAG, "Failed to load all driver availability: " + t.getMessage(), t);
                Toast.makeText(context, "Failed to load all driver availability: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}