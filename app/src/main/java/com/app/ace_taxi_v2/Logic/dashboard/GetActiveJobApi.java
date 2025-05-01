package com.app.ace_taxi_v2.Logic.dashboard;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.app.ace_taxi_v2.ApiService.ApiService;
import com.app.ace_taxi_v2.Instance.RetrofitClient;
import com.app.ace_taxi_v2.Logic.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GetActiveJobApi {
    private static final String TAG = "GetActiveJobApi";
    private static final int HTTP_SUCCESS = 200;

    private final Context context;
    private final ApiService apiService;

    public GetActiveJobApi(@NonNull Context context) {
        this.context = context.getApplicationContext();
        this.apiService = RetrofitClient.getInstance().create(ApiService.class);
    }

    public void getActiveJob(@NonNull GetActiveJobCallback callback) {
        if (callback == null) {
            throw new IllegalArgumentException("Callback cannot be null");
        }

        SessionManager sessionManager = new SessionManager(context);
        String token = sessionManager.getToken();

        if (token == null || token.isEmpty()) {
            Log.e(TAG, "Invalid or missing session token");
            callback.onFailure(new IllegalStateException("User not authenticated"));
            return;
        }

        Call<Integer> call = apiService.getActiveJob(token);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Integer> call, @NonNull Response<Integer> response) {
                if (response.code() == HTTP_SUCCESS && response.body() != null) {
                    int jobId = response.body();
                    Log.d(TAG, "Active job fetched successfully: bookingId=" + jobId);
                    callback.onSuccess(jobId);
                } else {
                    String errorMsg = "Failed to fetch active job: HTTP " + response.code() +
                            (response.message() != null ? " - " + response.message() : "");
                    Log.e(TAG, errorMsg);
                    callback.onFailure(new Exception(errorMsg));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Integer> call, @NonNull Throwable t) {
                Log.e(TAG, "API call failed: " + t.getMessage(), t);
                callback.onFailure(t);
            }
        });
    }

    public interface GetActiveJobCallback {
        void onSuccess(int jobId);
        void onFailure(@NonNull Throwable t);
    }
}