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
import com.app.ace_taxi_v2.Components.CustomToast;
import com.app.ace_taxi_v2.Fragments.Adapters.AvailablitiesAdapter;
import com.app.ace_taxi_v2.Helper.LogHelperLaravel;
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
    public CustomToast customToast;

    public AvailabilitiesApi(Context context) {
        this.context = context;
        this.customToast = new CustomToast(context);
    }

    public void getAvailabilities(RecyclerView recyclerView) {
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

                    } else {
                        String errorMessage = "Failed to fetch drivers: HTTP " + response.code() + " - " + response.message();
                        LogHelperLaravel.getInstance().e(TAG, errorMessage);
                        Sentry.captureMessage("AvailabilitiesApi Error: " + errorMessage);
                        showToast("Failed to load data. Please try again.");
                    }
                } catch (Exception e) {
                    LogHelperLaravel.getInstance().e(TAG, "Unexpected error in API response handling"+ e);
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

                    LogHelperLaravel.getInstance().e(TAG, failureMessage+ t);
                    Sentry.captureException(t);
                    showToast(failureMessage);
                } catch (Exception e) {
                    LogHelperLaravel.getInstance().e(TAG, "Error handling API failure"+ e);
                    Sentry.captureException(e);
                    showToast("Something went wrong with the server.");
                }
            }
        });
    }


    // Ensure Toast runs on the main UI thread
    private void showToast(String message) {
        customToast.showCustomErrorToast(message);
    }
}
