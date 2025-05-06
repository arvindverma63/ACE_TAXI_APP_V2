package com.app.ace_taxi_v2.Fragments.AvailablitiyFragmentAction;


import android.content.Context;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.app.ace_taxi_v2.Logic.AvailabilitiesApi;
import com.app.ace_taxi_v2.Logic.AvailabilityAddApi;
import com.app.ace_taxi_v2.Logic.SessionManager;

public class AvailabilityActionHandler {

    private Context context;
    private SessionManager sessionManager;
    private RecyclerView recyclerView;
    private DateTimeSelector dateTimeSelector;

    public AvailabilityActionHandler(Context context, SessionManager sessionManager, RecyclerView recyclerView, DateTimeSelector dateTimeSelector) {
        this.context = context;
        this.sessionManager = sessionManager;
        this.recyclerView = recyclerView;
        this.dateTimeSelector = dateTimeSelector;
    }

    public void renderList(View view) {
        try {
            AvailabilitiesApi availabilitiesApi = new AvailabilitiesApi(context);
            availabilitiesApi.getAvailabilities(recyclerView, view);
        } catch (Exception e) {
            Toast.makeText(context, "Error fetching availabilities: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void amSchoolOnly() {
        try {
            int userId = sessionManager.getUserId();
            String selectedDateString = dateTimeSelector.getSelectedButtonDateForAPI();

            if (selectedDateString == null) {
                Toast.makeText(context, "Please select a date first!", Toast.LENGTH_SHORT).show();
                return;
            }

            AvailabilityAddApi availabilityAddApi = new AvailabilityAddApi(context);
            availabilityAddApi.addAvailability(userId, selectedDateString, "07:30", "09:15", true, 1, "Am only");

            Thread.sleep(2000);
            renderList(recyclerView);
        } catch (Exception e) {
            Toast.makeText(context, "Error adding AM availability: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void pmSchoolOnly() {
        try {
            int userId = sessionManager.getUserId();
            String selectedDateString = dateTimeSelector.getSelectedButtonDateForAPI();

            if (selectedDateString == null) {
                Toast.makeText(context, "Please select a date first!", Toast.LENGTH_SHORT).show();
                return;
            }

            AvailabilityAddApi availabilityAddApi = new AvailabilityAddApi(context);
            availabilityAddApi.addAvailability(userId, selectedDateString, "14:30", "16:15", true, 1, "PM only");

            Thread.sleep(2000);
            renderList(recyclerView);
        } catch (Exception e) {
            Toast.makeText(context, "Error adding PM availability: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void bothOnly() {
        try {
            int userId = sessionManager.getUserId();
            String selectedDateString = dateTimeSelector.getSelectedButtonDateForAPI();

            if (selectedDateString == null) {
                Toast.makeText(context, "Please select a date first!", Toast.LENGTH_SHORT).show();
                return;
            }

            AvailabilityAddApi availabilityAddApi = new AvailabilityAddApi(context);
            availabilityAddApi.addAvailability(userId, selectedDateString, "14:30", "16:15", true, 1, "PM only");
            availabilityAddApi.addAvailability(userId, selectedDateString, "07:30", "09:15", true, 1, "Am only");

            Thread.sleep(2000);
            renderList(recyclerView);
        } catch (Exception e) {
            Toast.makeText(context, "Error adding AM/PM availability: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void setUnavailable() {
        try {
            int userId = sessionManager.getUserId();
            String selectedDateString = dateTimeSelector.getSelectedButtonDateForAPI();

            if (selectedDateString == null) {
                Toast.makeText(context, "Please select a date first!", Toast.LENGTH_SHORT).show();
                return;
            }

            AvailabilityAddApi availabilityAddApi = new AvailabilityAddApi(context);
            availabilityAddApi.addAvailability(userId, selectedDateString, "00:00", "23:59", true, 2, "Unavailable All Day");

            Toast.makeText(context, "Added successfully!", Toast.LENGTH_SHORT).show();
            Thread.sleep(2000);
            renderList(recyclerView);
        } catch (Exception e) {
            Toast.makeText(context, "Error setting unavailable: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}