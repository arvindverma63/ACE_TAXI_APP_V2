package com.app.ace_taxi_v2.Logic.Worker;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.app.ace_taxi_v2.ApiService.ApiService;
import com.app.ace_taxi_v2.Instance.RetrofitClient;
import com.app.ace_taxi_v2.Logic.SessionManager;
import com.app.ace_taxi_v2.Models.AvailabilityResponse;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Locale;

import io.sentry.Sentry;
import io.sentry.protocol.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AvailabiltiesApiResponse {
    private static final String TAG = "AvailabilitiesApi";
    private final Context context;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public AvailabiltiesApiResponse(Context context) {
        this.context = context;
        attachUserToSentry();
    }

    private void attachUserToSentry() {
        SessionManager sessionManager = new SessionManager(context);
        int userId = sessionManager.getUserId();
        User sentryUser = new User();
        sentryUser.setId(String.valueOf(userId));
        Sentry.setUser(sentryUser);
    }

    public void getAvailabilities(AvailabilityCallback callback) {
        SessionManager sessionManager = new SessionManager(context);
        String token = sessionManager.getToken();

        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        apiService.getAva(token).enqueue(new Callback<AvailabilityResponse>() {
            @Override
            public void onResponse(Call<AvailabilityResponse> call, Response<AvailabilityResponse> response) {
                mainHandler.post(() -> {
                    try {
                        if (response.isSuccessful() && response.body() != null) {
                            List<AvailabilityResponse.Driver> drivers = response.body().getDrivers();
                            callback.onSuccess(drivers);
                        } else {
                            String errorMessage = "Failed to fetch drivers: HTTP " + response.code() + " - " + response.message();
                            Log.e(TAG, errorMessage);
                            Sentry.captureMessage("AvailabilitiesApi Error: " + errorMessage);
                            callback.onError(errorMessage);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Unexpected error in API response handling", e);
                        Sentry.captureException(e);
                        callback.onError("Unexpected error in API response handling");
                    }
                });
            }

            @Override
            public void onFailure(Call<AvailabilityResponse> call, Throwable t) {
                mainHandler.post(() -> {
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
                        callback.onError(failureMessage);
                    } catch (Exception e) {
                        Log.e(TAG, "Error handling API failure", e);
                        Sentry.captureException(e);
                        callback.onError("Error handling API failure");
                    }
                });
            }
        });
    }

    public interface AvailabilityCallback {
        void onSuccess(List<AvailabilityResponse.Driver> drivers);
        void onError(String errorMessage);
    }
}
