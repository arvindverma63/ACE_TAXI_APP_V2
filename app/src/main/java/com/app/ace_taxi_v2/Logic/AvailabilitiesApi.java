package com.app.ace_taxi_v2.Logic;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.ace_taxi_v2.ApiService.ApiService;
import com.app.ace_taxi_v2.Fragments.Adapters.AvailablitiesAdapter;
import com.app.ace_taxi_v2.Instance.RetrofitClient;
import com.app.ace_taxi_v2.Models.AvailabilityResponse;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;

import io.sentry.Sentry;
import io.sentry.protocol.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AvailabilitiesApi {
    private static final String TAG = "AvailabilitiesApi";
    private final Context context;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public AvailabilitiesApi(Context context) {
        this.context = context;
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
                        List<AvailabilityResponse.Driver> list = response.body().getDrivers();
                        recyclerView.setLayoutManager(new LinearLayoutManager(context));
                        recyclerView.setAdapter(new AvailablitiesAdapter(list, context));
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

    // Ensure Toast runs on the main UI thread
    private void showToast(String message) {
        mainHandler.post(() -> Toast.makeText(context, message, Toast.LENGTH_LONG).show());
    }
}
