package com.app.ace_taxi_v2.Fragments.AvailablitiyFragmentAction;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.media.Image;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.app.ace_taxi_v2.Fragments.Adapters.AvailablitiesAdapter;
import com.app.ace_taxi_v2.Logic.AvailabilitiesApi;
import com.app.ace_taxi_v2.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DateTimeSelector {

    public Context context;
    public TextView dateText;
    public LinearLayout dateButton;
    public LinearLayout buttonContainer;
    public Calendar selectedDate = Calendar.getInstance();
    public SimpleDateFormat apiDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
    public SimpleDateFormat displayDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
    public String selectedDateStringForAPI;
    public MaterialButton selectedButton = null;
    public Calendar selectedButtonDate = Calendar.getInstance();
    public ImageView week_prev,week_next;
    public MaterialTextView selectedDateAvail;

    public DateTimeSelector(Context context, TextView dateText, LinearLayout dateButton, LinearLayout buttonContainer, ImageView week_prev, ImageView week_next, MaterialTextView selectedDateAvail                            ) {
        this.context = context;
        this.dateText = dateText;
        this.dateButton = dateButton;
        this.buttonContainer = buttonContainer;
        this.week_prev = week_prev;
        this.week_next = week_next;
        this.selectedDateAvail = selectedDateAvail;

        selectedDate = Calendar.getInstance();
        updateDateButtonText();      // show current date/time in TextView
        buttonController();
    }

    public void showDatePicker() {
        try {
            new DatePickerDialog(context, (view, year, month, dayOfMonth) -> {
                selectedDate.set(year, month, dayOfMonth);
                showTimePicker();
            }, selectedDate.get(Calendar.YEAR), selectedDate.get(Calendar.MONTH), selectedDate.get(Calendar.DAY_OF_MONTH)).show();
        } catch (Exception e) {
            Toast.makeText(context, "Error showing date picker: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
            }, selectedDate.get(Calendar.HOUR_OF_DAY), selectedDate.get(Calendar.MINUTE), true).show();
        } catch (Exception e) {
            Toast.makeText(context, "Error showing time picker: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void updateDateButtonText() {
        try {
            Calendar endDate = (Calendar) selectedDate.clone();
            endDate.add(Calendar.DATE, 6);

            String range = displayDateFormat.format(selectedDate.getTime()) + " to " +
                    displayDateFormat.format(endDate.getTime());

            dateText.setText(range);

            // Also store selectedButtonDate default as day 1
            selectedButtonDate.setTime(selectedDate.getTime());

            selectedDateStringForAPI = apiDateFormat.format(selectedButtonDate.getTime());
        } catch (Exception e) {
            Toast.makeText(context, "Error updating date text: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void updateWeekPrev() {
        try {
            selectedDate.add(Calendar.DATE, -7);
            updateDateButtonText();
            buttonController();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateWeekNext() {
        try {
            selectedDate.add(Calendar.DATE, 7);
            updateDateButtonText();
            buttonController();
        } catch (Exception e) {
            e.printStackTrace();
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
        selectedDateAvail.setText(displayDateFormat.format(selectedButtonDate.getTime()));
        for (int i = 0; i < 7; i++) {
            Calendar tempCalendar = (Calendar) selectedDate.clone();
            tempCalendar.add(Calendar.DATE, i);

            MaterialButton button = new MaterialButton(buttonContainer.getContext());
            button.setText(sdf.format(tempCalendar.getTime()));
            button.setCornerRadius(16);
            button.setStrokeColor(ContextCompat.getColorStateList(buttonContainer.getContext(), R.color.red));
            button.setStrokeWidth(2);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
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
                // Log the date selected for debugging
                Log.d("filter", "Button clicked. Selected date: " + displayDateFormat.format(selectedButtonDate.getTime()));
                selectedDateAvail.setText(displayDateFormat.format(selectedButtonDate.getTime()));
            });

            buttonContainer.addView(button);
        }
    }


}