package com.app.ace_taxi_v2.Logic.dashboard;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.app.ace_taxi_v2.ApiService.ApiService;
import com.app.ace_taxi_v2.Instance.RetrofitClient;
import com.app.ace_taxi_v2.Logic.SessionManager;
import com.app.ace_taxi_v2.Models.Jobs.TodayBooking;
import com.app.ace_taxi_v2.Models.Jobs.TodayJobResponse;

import java.util.List;

import io.sentry.Sentry;
import io.sentry.protocol.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CurrentBooking {
    private static final String TAG = "CurrentBooking";
    private static final int HTTP_SUCCESS = 200;

    private final Context context;
    private final ApiService apiService;

    public CurrentBooking(@NonNull Context context) {
        this.context = context.getApplicationContext();
        this.apiService = RetrofitClient.getInstance().create(ApiService.class);
    }

    public void getCurrentBooking(@NonNull CurrentJobCallback jobCallback) {
        if (jobCallback == null) {
            throw new IllegalArgumentException("Callback cannot be null");
        }

        SessionManager sessionManager = new SessionManager(context);
        String token = sessionManager.getToken();
        int userId = sessionManager.getUserId();

        if (token == null || token.isEmpty()) {
            String errorMsg = "Token is null or empty for user ID: " + userId;
            Log.e(TAG, errorMsg);
            Sentry.captureMessage(errorMsg);
            jobCallback.onError("Authentication failed. Please log in again.");
            return;
        }

        if (userId > 0) {
            User sentryUser = new User();
            sentryUser.setId(String.valueOf(userId));
            Sentry.setUser(sentryUser);
        } else {
            Log.w(TAG, "Invalid user ID: " + userId);
            Sentry.captureMessage("Invalid user ID: " + userId);
        }

        Sentry.setTag("api_endpoint", "todayJobs");
        Sentry.setExtra("token_length", String.valueOf(token.length()));

        Call<TodayJobResponse> call = apiService.todayJobs(token);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<TodayJobResponse> call, @NonNull Response<TodayJobResponse> response) {
                if (response.code() == HTTP_SUCCESS && response.body() != null && response.body().getBookings() != null) {
                    List<TodayBooking> bookings = response.body().getBookings();
                    Log.d(TAG, "Fetched " + bookings.size() + " current bookings for user ID: " + userId);
                    jobCallback.onSuccess(bookings);
                } else {
                    String errorMessage = "Failed to fetch current bookings: HTTP " + response.code() +
                            (response.message() != null ? " - " + response.message() : "");
                    Log.e(TAG, errorMessage);
                    Sentry.setTag("http_code", String.valueOf(response.code()));
                    Sentry.captureMessage(errorMessage);
                    jobCallback.onError("Unable to load bookings. Please try again.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<TodayJobResponse> call, @NonNull Throwable t) {
                String errorMessage = "Network error: " + t.getMessage();
                Log.e(TAG, errorMessage, t);
                Sentry.captureException(t);
                jobCallback.onError("Network error. Please check your connection.");
            }
        });
    }

    public interface CurrentJobCallback {
        void onSuccess(@NonNull List<TodayBooking> list);
        void onError(@NonNull String error);
    }
}