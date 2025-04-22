package com.app.ace_taxi_v2.Logic;

import android.content.Context;
import android.widget.Toast;

import com.app.ace_taxi_v2.ApiService.ApiService;
import com.app.ace_taxi_v2.Components.CustomToast;
import com.app.ace_taxi_v2.Instance.RetrofitClient;
import com.app.ace_taxi_v2.Models.JobResponse;

import io.sentry.Sentry;
import io.sentry.protocol.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JobResponseApi {
    public Context context;
    public SessionManager sessionManager;

    public JobResponseApi(Context context) {
        this.context = context;
        sessionManager = new SessionManager(context);
    }

    public void acceptResponse(int bookingId) {
        String token = sessionManager.getToken();
        int userId = sessionManager.getUserId(); // Get user ID from session

        // Attach user details to Sentry
        User sentryUser = new User();
        sentryUser.setId(String.valueOf(userId));
        Sentry.setUser(sentryUser);

        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        apiService.sendJobResponse(token, bookingId, 2000).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code() == 200) {
                    CustomToast customToast = new CustomToast(context);
                    customToast.showCustomToast("Job Accepted Successfully");
                } else {
                    String errorMessage = "JobResponseApi Accept Error: HTTP " + response.code() + " - " + response.message();
                    Sentry.captureMessage(errorMessage);
                    Toast.makeText(context, "Failed to accept job", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Sentry.captureException(t);
            }
        });
    }

    public void rejectBooking(int bookingId) {
        String token = sessionManager.getToken();
        int userId = sessionManager.getUserId(); // Get user ID from session

        // Attach user details to Sentry
        User sentryUser = new User();
        sentryUser.setId(String.valueOf(userId));
        Sentry.setUser(sentryUser);

        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        apiService.sendJobResponse(token, bookingId, 2001).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code() == 200) {
                    CustomToast customToast = new CustomToast(context);
                    customToast.showCustomToast("Job Rejected Successfully");
                } else {
                    String errorMessage = "JobResponseApi Reject Error: HTTP " + response.code() + " - " + response.message();
                    Sentry.captureMessage(errorMessage);
                    Toast.makeText(context, "Failed to reject job", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Sentry.captureException(t);
                Toast.makeText(context, "Job Reject failed", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void timeOut(int bookingId) {
        String token = sessionManager.getToken();
        int userId = sessionManager.getUserId(); // Get user ID from session

        // Attach user details to Sentry
        User sentryUser = new User();
        sentryUser.setId(String.valueOf(userId));
        Sentry.setUser(sentryUser);

        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        apiService.sendJobResponse(token, bookingId, 2002).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code() == 200) {
                    Toast.makeText(context, "Job TimeOut Successfully", Toast.LENGTH_LONG).show();
                } else {
                    String errorMessage = "JobResponseApi Reject Error: HTTP " + response.code() + " - " + response.message();
                    Sentry.captureMessage(errorMessage);
                    Toast.makeText(context, "Failed to reject job", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Sentry.captureException(t);
                Toast.makeText(context, "Job Reject failed", Toast.LENGTH_LONG).show();
            }
        });
    }
}
