package com.app.ace_taxi_v2.Logic;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.app.ace_taxi_v2.ApiService.ApiService;
import com.app.ace_taxi_v2.Components.CustomToast;
import com.app.ace_taxi_v2.Helper.LogHelperLaravel;
import com.app.ace_taxi_v2.Instance.RetrofitClient;
import com.app.ace_taxi_v2.Models.Guid;

import io.sentry.Sentry;
import io.sentry.protocol.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JobResponseApi {
    private final Context context;
    private final SessionManager sessionManager;
    private final Guid guidGenerator;

    public JobResponseApi(Context context) {
        this.context = context;
        this.sessionManager = new SessionManager(context);
        this.guidGenerator = new Guid(context);

        // Initialize Sentry user
        User sentryUser = new User();
        sentryUser.setId(String.valueOf(sessionManager.getUserId()));
        Sentry.setUser(sentryUser);
    }

    public void acceptResponse(int bookingId) {
        sendJobResponse(bookingId, 2000, "Job Accepted Successfully", "Failed to accept job");
    }

    public void rejectBooking(int bookingId) {
        sendJobResponse(bookingId, 2001, "Job Rejected Successfully", "Failed to reject job");
    }

    public void timeOut(int bookingId) {
        sendJobResponse(bookingId, 2002, "Job Timed Out Successfully", "Failed to timeout job");
    }

    private void sendJobResponse(int bookingId, int statusCode, String successMessage, String failureMessage) {
        String token = sessionManager.getToken();
        if (token == null || token.isEmpty()) {
            Toast.makeText(context, "Session invalid. Please log in.", Toast.LENGTH_LONG).show();
            return;
        }

        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        Call<Void> call = apiService.sendJobResponse(token, bookingId, statusCode, guidGenerator.getGuid());
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                LogHelperLaravel.getInstance().d("Job response api: ",response.body()+" & rseponse code : "+response.code()+" guid"+guidGenerator.getGuid());
                if (response.code() == 200) {
                    new CustomToast(context).showCustomToast(successMessage);
                } else {
                    String errorMessage = String.format("JobResponseApi Error: HTTP %d - %s, Booking ID: %d",
                            response.code(), response.message(), bookingId);
                    Sentry.captureMessage(errorMessage);
                    if (response.code() == 401) {
                        Toast.makeText(context, "Session expired. Please log in again.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(context, failureMessage, Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Sentry.captureException(t);
                Toast.makeText(context, failureMessage, Toast.LENGTH_LONG).show();
            }
        });
    }
}