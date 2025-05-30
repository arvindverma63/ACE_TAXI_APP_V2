package com.app.ace_taxi_v2.Logic;

import android.content.Context;
import android.widget.Toast;

import com.app.ace_taxi_v2.ApiService.ApiService;
import com.app.ace_taxi_v2.Components.CustomToast;
import com.app.ace_taxi_v2.Helper.LogHelperLaravel;
import com.app.ace_taxi_v2.Instance.RetrofitClient;
import com.app.ace_taxi_v2.Models.Jobs.ArrivedResponse;

import io.sentry.Sentry;
import io.sentry.protocol.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArrivedJobApi {
    private final Context context;

    public ArrivedJobApi(Context context) {
        this.context = context.getApplicationContext();
    }

    public void updateStatus(int bookingId) {
        SessionManager sessionManager = new SessionManager(context);
        String token = sessionManager.getToken();
        int userId = sessionManager.getUserId();

        // Input validation
        if (token == null || token.isEmpty()) {
            new CustomToast(context).showCustomToast("Invalid session. Please log in again.");
            return;
        }
        if (bookingId <= 0) {
            new CustomToast(context).showCustomToast("Invalid booking ID");
            return;
        }

        // Set Sentry user context
        User sentryUser = new User();
        sentryUser.setId(String.valueOf(userId));
        Sentry.setUser(sentryUser);

        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        apiService.arrivedStatusUpdate(token, bookingId).enqueue(new Callback<ArrivedResponse>() {
            @Override
            public void onResponse(Call<ArrivedResponse> call, Response<ArrivedResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    new CustomToast(context).showCustomToast("Arrival updated successfully");
                    LogHelperLaravel.getInstance().i("ArrivedJobApi Success: ", bookingId + ":bookingId userId " + userId);
                } else if (response.code() == 401) {
                    new CustomToast(context).showCustomErrorToast("Unauthorized: Please log in again");
                    LogHelperLaravel.getInstance().e("ArrivedJobApi Error: ", "HTTP 401 Unauthorized");
                } else {
                    new CustomToast(context).showCustomErrorToast("Error: " + response.message()+"404 from server");
                    LogHelperLaravel.getInstance().e("ArrivedJobApi Error: ", "HTTP " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ArrivedResponse> call, Throwable t) {
                new CustomToast(context).showCustomErrorToast("Network error: " + t.getMessage());
                LogHelperLaravel.getInstance().e("ArrivedJobApi Error: ", t.getMessage());
                Sentry.captureException(t);
            }
        });
    }
}