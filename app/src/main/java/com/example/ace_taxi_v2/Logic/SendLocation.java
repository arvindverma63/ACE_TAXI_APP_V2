package com.example.ace_taxi_v2.Logic;


import android.content.Context;
import android.util.Log;


import com.example.ace_taxi_v2.ApiService.ApiService;
import com.example.ace_taxi_v2.Instance.RetrofitClient;
import com.example.ace_taxi_v2.Models.GPSRequest;
import com.example.ace_taxi_v2.Models.GPSResponse;

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
        // Retrieve session details
        SessionManager sessionManager = new SessionManager(context);
        String token = sessionManager.getToken();
        int userId = sessionManager.getUserId();

        double longtitude = longitude;

        if (token == null || userId == -1) {
            Log.e("SendLocation", "User is not logged in or token is invalid");
            return;
        }

        // Log debug information
        Log.d("SendLocation", "Preparing to send GPS data");

        // Prepare the API call
        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        GPSRequest gpsRequest = new GPSRequest(userId, latitude, longtitude, heading, speed);

        apiService.updateGps("Bearer " + token, gpsRequest).enqueue(new Callback<GPSResponse>() {
            @Override
            public void onResponse(Call<GPSResponse> call, Response<GPSResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("SendLocation", "GPS updated successfully: " + response.body());
                } else {
                    Log.e("SendLocation", "Failed to update GPS: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<GPSResponse> call, Throwable t) {
                Log.e("SendLocation", "Error updating GPS: " + t.getMessage());
            }
        });
    }
}


