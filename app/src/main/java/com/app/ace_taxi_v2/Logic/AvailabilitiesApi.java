package com.app.ace_taxi_v2.Logic;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.ace_taxi_v2.ApiService.ApiService;
import com.app.ace_taxi_v2.Fragments.Adapters.AvailablitiesAdapter;
import com.app.ace_taxi_v2.Instance.RetrofitClient;
import com.app.ace_taxi_v2.Models.AvailabilityResponse;
import com.app.ace_taxi_v2.R;
import com.google.android.material.card.MaterialCardView;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import io.sentry.Sentry;
import io.sentry.protocol.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AvailabilitiesApi {
    private static final String TAG = "AvailabilitiesApi";
    private final Context context;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private List<AvailabilityResponse.Driver> fullList; // Store full list for filtering
    private static final SimpleDateFormat apiDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
    private static final SimpleDateFormat displayDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    public AvailabilitiesApi(Context context) {
        this.context = context;
    }

    public void getAvailabilities(RecyclerView recyclerView, View parentView) {
        SessionManager sessionManager = new SessionManager(context);
        String token = sessionManager.getToken();
        int userId = sessionManager.getUserId(); // Assuming session manager provides the user ID

        // Attach user details to Sentry for tracking errors
        User sentryUser = new User();
        sentryUser.setId(String.valueOf(userId));
        Sentry.setUser(sentryUser);

        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        apiService.getAva(token).enqueue(new Callback<AvailabilityResponse>() {
            @Override
            public void onResponse(Call<AvailabilityResponse> call, Response<AvailabilityResponse> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        fullList = response.body().getDrivers(); // Store full data for filtering
                        recyclerView.setLayoutManager(new LinearLayoutManager(context));
                        recyclerView.setAdapter(new AvailablitiesAdapter(fullList, context));

                        // Get UI elements
                        MaterialCardView datePicker = parentView.findViewById(R.id.datePicker);
                        TextView datetimeText = parentView.findViewById(R.id.datetime_text);

                        // Set default date to today's date (formatted)
                        if (datetimeText != null) {
                            String todayDate = displayDateFormat.format(Calendar.getInstance().getTime());
                            datetimeText.setText(todayDate);
                        }

                        // Set DatePicker click listener
                        if (datePicker != null) {
                            datePicker.setOnClickListener(v -> showDatePicker(recyclerView, datetimeText));
                        } else {
                            Log.e(TAG, "datePicker is null, check your layout XML.");
                        }
                    } else {
                        String errorMessage = "Failed to fetch drivers: HTTP " + response.code() + " - " + response.message();
                        Log.e(TAG, errorMessage);
                        Sentry.captureMessage("AvailabilitiesApi Error: " + errorMessage);
                        showToast("Failed to load data. Please try again.");
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Unexpected error in API response handling", e);
                    Sentry.captureException(e);
                    showToast("Something went wrong. Please try again.");
                }
            }

            @Override
            public void onFailure(Call<AvailabilityResponse> call, Throwable t) {
                try {
                    String failureMessage;
                    if (t instanceof SocketTimeoutException) {
                        failureMessage = "Request timed out. Please check your internet connection.";
                    } else if (t instanceof IOException) {
                        failureMessage = "Network error. Please check your connection.";
                    } else {
                        failureMessage = "Unexpected error: " + t.getMessage();
                    }

                    Log.e(TAG, failureMessage, t);
                    Sentry.captureException(t);
                    showToast(failureMessage);
                } catch (Exception e) {
                    Log.e(TAG, "Error handling API failure", e);
                    Sentry.captureException(e);
                    showToast("Something went wrong with the server.");
                }
            }
        });
    }

    // Show Date Picker
    private void showDatePicker(RecyclerView recyclerView, TextView datetimeText) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(context, (DatePicker view, int year, int month, int dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth);
            String selectedDate = displayDateFormat.format(calendar.getTime());

            // Update TextView with selected date
            if (datetimeText != null) {
                datetimeText.setText(selectedDate);
            }

            // Filter list based on selected date
            filterListByDate(selectedDate, recyclerView);
        },
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    // Filter the list based on the selected date
    private void filterListByDate(String selectedDate, RecyclerView recyclerView) {
        if (fullList == null || fullList.isEmpty()) {
            showToast("No data available.");
            return;
        }

        List<AvailabilityResponse.Driver> filteredList = fullList.stream()
                .filter(driver -> {
                    try {
                        String driverDate = displayDateFormat.format(apiDateFormat.parse(driver.getDate())); // Convert API format to yyyy-MM-dd
                        return driverDate.equals(selectedDate);
                    } catch (ParseException e) {
                        Log.e(TAG, "Date parsing error for: " + driver.getDate(), e);
                        return false;
                    }
                })
                .collect(Collectors.toList());

        // Update RecyclerView
        recyclerView.setAdapter(new AvailablitiesAdapter(filteredList, context));

        // Show toast if no data found for selected date
        if (filteredList.isEmpty()) {
            showToast("No drivers available for " + selectedDate);
        }
    }

    // Ensure Toast runs on the main UI thread
    private void showToast(String message) {
//        mainHandler.post(() -> Toast.makeText(context, message, Toast.LENGTH_LONG).show());
    }
}
