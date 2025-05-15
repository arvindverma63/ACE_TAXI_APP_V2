package com.app.ace_taxi_v2.Fragments.AvailablitiyFragmentAction;


import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.ace_taxi_v2.Fragments.Adapters.AvailablitiesAdapter;
import com.app.ace_taxi_v2.Logic.AvailabilitiesApi;
import com.app.ace_taxi_v2.Logic.AvailabilityAddApi;
import com.app.ace_taxi_v2.Logic.SessionManager;
import com.app.ace_taxi_v2.Logic.Worker.AvailabiltiesApiResponse;
import com.app.ace_taxi_v2.Models.AvailabilityResponse;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class AvailabilityActionHandler {

    private Context context;
    private SessionManager sessionManager;
    private RecyclerView recyclerView;
    private DateTimeSelector dateTimeSelector;
    private final SimpleDateFormat isoDateFormat = new SimpleDateFormat("yyyy-mm-dd", Locale.getDefault());

    public AvailabilityActionHandler(Context context, SessionManager sessionManager, RecyclerView recyclerView, DateTimeSelector dateTimeSelector) {
        this.context = context;
        this.sessionManager = sessionManager;
        this.recyclerView = recyclerView;
        this.dateTimeSelector = dateTimeSelector;
    }

    public void renderList(RecyclerView view) {
        Log.e("render method call","render "+dateTimeSelector.getSelectedButtonDateForAPI());
        AvailabiltiesApiResponse availabilitiesApi = new AvailabiltiesApiResponse(context);
        availabilitiesApi.getAvailabilities(new AvailabiltiesApiResponse.AvailabilityCallback() {
            @Override
            public void onSuccess(List<AvailabilityResponse.Driver> drivers) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                AvailablitiesAdapter adapter = new AvailablitiesAdapter(drivers, context);
                recyclerView.setAdapter(adapter);
                String date = isoDateFormat.format(dateTimeSelector.getSelectedButtonDateForAPI());
                Log.d("formated Date : ",date+"formated date");
                adapter.updateListForDate(isoDateFormat.format(dateTimeSelector.getSelectedButtonDateForAPI()));
            }

            @Override
            public void onError(String errorMessage) {

            }
        });
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
            availabilityAddApi.addAvailability(userId, selectedDateString, "07:30", "09:15", true, 1, "AM only");

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
            availabilityAddApi.addAvailability(userId, selectedDateString, "07:30", "09:15", true, 1, "AM only");

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

            renderList(recyclerView);
        } catch (Exception e) {
        }
    }
}