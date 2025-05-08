package com.app.ace_taxi_v2.Logic.availability;

import android.content.Context;
import android.util.Log;

import com.app.ace_taxi_v2.ApiService.ApiService;
import com.app.ace_taxi_v2.Instance.RetrofitClient;
import com.app.ace_taxi_v2.Logic.SessionManager;
import com.app.ace_taxi_v2.Models.AllDriverAvailabilityResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AllDriverAvailApi {
    private final Context context;
    private static final String TAG = "AllDriverAvailApi"; // Tag for logging

    public AllDriverAvailApi(Context context) {
        this.context = context;
    }

    public void getResponse(String date, AllDriverAvailCallback callback) {
        Log.d(TAG, "Starting API request for driver availability with date: " + date);

        SessionManager sessionManager = new SessionManager(context);
        String token = sessionManager.getToken();
        Log.d(TAG, "Using token: " + token);

        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        Call<List<AllDriverAvailabilityResponse>> call = apiService.getAllDriverAvailablity(token, date);
        Log.d(TAG, "API call enqueued");

        call.enqueue(new Callback<List<AllDriverAvailabilityResponse>>() {
            @Override
            public void onResponse(Call<List<AllDriverAvailabilityResponse>> call, Response<List<AllDriverAvailabilityResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "API call successful. Received " + response.body().size() + " driver availability records");
                    callback.onResponse(response.body());
                } else {
                    String errorMsg = "Response unsuccessful or body is null: " + response.code();
                    Log.e(TAG, errorMsg);
                    callback.onFailure(new Throwable(errorMsg));
                }
            }

            @Override
            public void onFailure(Call<List<AllDriverAvailabilityResponse>> call, Throwable t) {
                Log.e(TAG, "API call failed: " + t.getMessage(), t);
                callback.onFailure(t);
            }
        });
    }

    public interface AllDriverAvailCallback {
        void onResponse(List<AllDriverAvailabilityResponse> response);
        void onFailure(Throwable t);
    }
}