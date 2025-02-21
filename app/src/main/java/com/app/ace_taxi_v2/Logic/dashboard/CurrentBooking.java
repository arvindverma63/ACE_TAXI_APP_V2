package com.app.ace_taxi_v2.Logic.dashboard;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

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
    public Context context;

    public CurrentBooking(Context context){
        this.context = context;
    }

    public void getCurrentBooking(CurrentJobCallback jobCallback){
        SessionManager sessionManager = new SessionManager(context);
        String token = sessionManager.getToken();
        int userId = sessionManager.getUserId(); // Get user ID for tracking

        if (token == null || token.isEmpty()) {
            Log.e("CurrentBooking", "Token is null or empty!");
            Sentry.captureMessage("CurrentBooking Error: Token is null or empty for user ID: " + userId);
            jobCallback.onError("Authentication failed. Please log in again.");
            return;
        }

        // Attach user details to Sentry
        User sentryUser = new User();
        sentryUser.setId(String.valueOf(userId));
        Sentry.setUser(sentryUser);

        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        apiService.todayJobs(token).enqueue(new Callback<TodayJobResponse>() {
            @Override
            public void onResponse(Call<TodayJobResponse> call, Response<TodayJobResponse> response) {
                if(response.code() == 200 && response.body() != null){
                    jobCallback.onSuccess(response.body().getBookings());
                    Toast.makeText(context,"success code 200 ok",Toast.LENGTH_LONG).show();
                } else {
                    String errorMessage = "CurrentBooking API Error: HTTP " + response.code() + " - " + response.message();
                    Log.e("CurrentBooking", errorMessage);
                    Sentry.captureMessage(errorMessage);
                    jobCallback.onError("Failed to fetch current bookings.");
                }
            }

            @Override
            public void onFailure(Call<TodayJobResponse> call, Throwable t) {
                Log.e("CurrentBooking", "API Call Failed: " + t.getMessage(), t);
                Sentry.captureException(t);
                jobCallback.onError("Network error. Please check your connection.");
            }
        });
    }

    public interface CurrentJobCallback{
        void onSuccess(List<TodayBooking> list);
        void onError(String error);
    }
}
