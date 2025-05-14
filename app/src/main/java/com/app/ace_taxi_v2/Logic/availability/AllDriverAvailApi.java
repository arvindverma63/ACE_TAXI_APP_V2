package com.app.ace_taxi_v2.Logic.availability;

import android.content.Context;
import android.util.Log;

import com.app.ace_taxi_v2.ApiService.ApiService;
import com.app.ace_taxi_v2.Components.CustomToast;
import com.app.ace_taxi_v2.Instance.RetrofitClient;
import com.app.ace_taxi_v2.Logic.SessionManager;
import com.app.ace_taxi_v2.Models.AllDriverAvailabilityResponse;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AllDriverAvailApi {
    private final Context context;
    private static final String TAG = "AllDriverAvailApi"; // Tag for logging
    SimpleDateFormat apiFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    public CustomToast customToast;

    public AllDriverAvailApi(Context context) {
        this.context = context;
        this.customToast = new CustomToast(context);
    }

    public void getResponse(String date, AllDriverAvailCallback callback) {
        Log.d(TAG, "Starting API request for driver availability with date: " + date);

        try {
            // Parse the input date (dd/MM/yyyy) to a Date object
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date parsedDate = inputFormat.parse(date);

            // Format the date for the API (yyyy-MM-dd)
            String apiDate = apiFormat.format(parsedDate);
            Log.d(TAG, "Formatted API date: " + apiDate);

            SessionManager sessionManager = new SessionManager(context);
            String token = sessionManager.getToken();
            Log.d(TAG, "Using token: " + token);

            ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
            Call<List<AllDriverAvailabilityResponse>> call = apiService.getAllDriverAvailablity(token, apiDate);
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
        } catch (Exception e) {
            Log.e(TAG, "Date parsing failed: " + e.getMessage(), e);
            callback.onFailure(e);
        }
    }



    public interface AllDriverAvailCallback {
        void onResponse(List<AllDriverAvailabilityResponse> response);
        void onFailure(Throwable t);
    }
}