package com.app.ace_taxi_v2.Logic;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.app.ace_taxi_v2.Activity.LoginActivity;
import com.app.ace_taxi_v2.ApiService.ApiService;
import com.app.ace_taxi_v2.Components.CustomToast;
import com.app.ace_taxi_v2.Instance.RetrofitClient;
import com.app.ace_taxi_v2.Models.FcmRequest;
import com.app.ace_taxi_v2.Models.FcmResponse;

import java.io.IOException;

import io.sentry.Sentry;
import io.sentry.protocol.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateFCMApi {
    private static final String TAG = "UpdateFCMApi";
    private final Context context;

    public UpdateFCMApi(Context context) {
        this.context = context.getApplicationContext();
    }

    public void updateFcm() {
        SessionManager sessionManager = new SessionManager(context);
        String jwtToken = sessionManager.getToken();
        int userId = sessionManager.getUserId(); // Get user ID for tracking

        FCMTokenManager tokenManager = new FCMTokenManager(context);
        String fcm = tokenManager.getToken();

        Log.e(TAG, "FCM Token : " + fcm);

        if (jwtToken == null || jwtToken.isEmpty() || fcm == null || fcm.isEmpty()) {
            Log.e(TAG, "JWT token or FCM token is null/empty");
            Sentry.captureMessage("UpdateFCMApi Error: JWT token or FCM token is null/empty for user ID: " + userId);
            Toast.makeText(context, "Token error. Please try again.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Attach user details to Sentry
        User sentryUser = new User();
        sentryUser.setId(String.valueOf(userId));
        Sentry.setUser(sentryUser);

        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        FcmRequest request = new FcmRequest(fcm);

        apiService.updateFcm("Bearer " + jwtToken, request).enqueue(new Callback<FcmResponse>() {

            @Override
            public void onResponse(Call<FcmResponse> call, Response<FcmResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "FCM updated successfully: " + response.body());
                    new CustomToast(context).showCustomToast("FCM updated successfully:");
                }
                else if(response.code() == 423){
                    new CustomToast(context).showCustomErrorToast("User Locked Out");
                    new SessionManager(context).clearSession();
                    Sentry.setUser(null);  // Clear user info from Sentry
                    Intent intent = new Intent(context, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    context.startActivity(intent);
                }
                else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error details";
                        Log.e(TAG, "Failed to update FCM: " + response.code() + " - " + errorBody);
                        Sentry.captureMessage("UpdateFCMApi Error: HTTP " + response.code() + " - " + errorBody);
                    } catch (IOException e) {
                        Log.e(TAG, "Error reading error body", e);
                        Sentry.captureException(e);
                    }
                    Toast.makeText(context, "Failed to update FCM. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<FcmResponse> call, Throwable t) {
                Log.e(TAG, "Error updating FCM: " + t.getMessage());
                Sentry.captureException(t);
                Toast.makeText(context, "Network error. Please check your connection.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
