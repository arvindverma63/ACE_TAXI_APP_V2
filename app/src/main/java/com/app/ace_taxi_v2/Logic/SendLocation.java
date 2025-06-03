package com.app.ace_taxi_v2.Logic;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;

import com.app.ace_taxi_v2.ApiService.ApiService;
import com.app.ace_taxi_v2.Helper.LogHelperLaravel;
import com.app.ace_taxi_v2.Instance.RetrofitClient;
import com.app.ace_taxi_v2.Models.GPSRequest;
import com.app.ace_taxi_v2.Models.GPSResponse;

import io.sentry.Sentry;
import io.sentry.protocol.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SendLocation {
    private double latitude;
    private double longitude;
    private double speed;
    private double heading;
    private Context context;

    public SendLocation(Context context, double latitude, double longitude, double speed, double heading) {
        this.context = context;
        this.latitude = latitude;
        this.longitude = longitude;
        this.speed = speed;
        this.heading = heading;
    }

    public void sendLocation() {
        try {
            // Retrieve session details
            SessionManager sessionManager = new SessionManager(context);
            String token = sessionManager.getToken();
            int userId = sessionManager.getUserId();

            double longtitude = longitude;

            if (token == null || userId == -1) {
                LogHelperLaravel.getInstance().e("SendLocation", "User is not logged in or token is invalid");
                Sentry.captureMessage("SendLocation Error: User is not logged in or token is invalid.");
                return;
            }

            Log.d("SendLocation", "Preparing to send GPS data");

            // Attach user details to Sentry
            User sentryUser = new User();
            sentryUser.setId(String.valueOf(userId));
            Sentry.setUser(sentryUser);

            // Prepare the API call
            ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
            GPSRequest gpsRequest = new GPSRequest(userId, latitude, longtitude, heading, speed);

            apiService.updateGps("Bearer " + token, gpsRequest).enqueue(new Callback<GPSResponse>() {
                @Override
                public void onResponse(Call<GPSResponse> call, Response<GPSResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                    } else {
                        Log.e("SendLocation", "Failed to update GPS");
                        try {
                            if (response.errorBody() != null) {
                                String errorBody = response.errorBody().string();
                                Log.e("SendLocation", "Error Body: " + errorBody);
                                Sentry.captureMessage("SendLocation API Error: " + errorBody);
                            }
                        } catch (Exception e) {
                            Log.e("SendLocation", "Error parsing error body: " + e.getMessage());
                            Sentry.captureException(e);
                        }
                    }
                }

                @Override
                public void onFailure(Call<GPSResponse> call, Throwable t) {

                    Sentry.captureException(t);
                }
            });
        } catch (Exception e) {
            LogHelperLaravel.getInstance().e(TAG,e+"");
        }
    }
}
