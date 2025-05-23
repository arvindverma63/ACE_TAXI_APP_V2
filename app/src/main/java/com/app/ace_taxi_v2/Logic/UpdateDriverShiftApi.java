package com.app.ace_taxi_v2.Logic;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.app.ace_taxi_v2.ApiService.ApiService;
import com.app.ace_taxi_v2.Components.CustomToast;
import com.app.ace_taxi_v2.Helper.LogHelperLaravel;
import com.app.ace_taxi_v2.Instance.RetrofitClient;
import com.app.ace_taxi_v2.Logic.Service.CurrentShiftStatus;

import org.checkerframework.checker.units.qual.C;

import io.sentry.Sentry;
import io.sentry.protocol.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateDriverShiftApi {
    private final Context context;
    public CustomToast customToast;

    public UpdateDriverShiftApi(Context context) {
        this.context = context;
        this.customToast = new CustomToast(context);
    }

    public void updateStatus(int status) {
        SessionManager sessionManager = new SessionManager(context);
        String token = sessionManager.getToken();
        int userId = sessionManager.getUserId();

        if (token == null || token.isEmpty()) {
            LogHelperLaravel.getInstance().e("DriverShiftAPI", "Token is null or empty!");
            Sentry.captureMessage("DriverShiftAPI Error: Token is null or empty for user ID: " + userId);
            customToast.showCustomErrorToast("Authentication failed. Please log in again.");
            return;
        }

        // Attach user details to Sentry
        User sentryUser = new User();
        sentryUser.setId(String.valueOf(userId));
        Sentry.setUser(sentryUser);

        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        apiService.driverShift(token, userId, status).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                LogHelperLaravel.getInstance().d("DriverShiftAPI", "Response Code: " + response.code());

                if (response.code() == 204) { // No content
                    LogHelperLaravel.getInstance().d("DriverShiftAPI", "No content received but request succeeded");
                    customToast.showCustomErrorToast("No content received but request succeeded");
                    updateCurrentStatus(status);
                    return;
                }

                if (response.isSuccessful()) {
                    customToast.showCustomToast("Status updated successfully");
                    updateCurrentStatus(status);
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                        LogHelperLaravel.getInstance().e("DriverShiftAPI", "API Error Response: " + errorBody);
                        Sentry.captureMessage("DriverShiftAPI Error: HTTP " + response.code() + " - " + errorBody);
                    } catch (Exception e) {
                        LogHelperLaravel.getInstance().e("DriverShiftAPI", "Error reading errorBody"+ e);
                        Sentry.captureException(e);
                    }
                    customToast.showCustomErrorToast("Network error. Please try again.");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                LogHelperLaravel.getInstance().e("DriverShiftAPI", "API Call Failed: " + t.getMessage());
                Sentry.captureException(t);
                customToast.showCustomErrorToast("Network error. Please try again.");
            }
        });
    }

    public void updateCurrentStatus(int status) {
        CurrentShiftStatus currentShiftStatus = new CurrentShiftStatus(context);
        switch (status) {
            case 1000:
                currentShiftStatus.saveStatus("onShift");
                break;
            case 1001:
                currentShiftStatus.saveStatus("onFinish");
                break;
            case 1002:
                currentShiftStatus.saveStatus("onBreak");
                break;
            case 1003:
                currentShiftStatus.saveStatus("onBreakFinish");
                break;
            default:
                Log.w("DriverShiftAPI", "Unknown status received: " + status);
                Sentry.captureMessage("DriverShiftAPI Warning: Unknown status received: " + status);
                break;
        }
    }
}
