package com.app.ace_taxi_v2.Logic;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.app.ace_taxi_v2.ApiService.ApiService;
import com.app.ace_taxi_v2.Instance.RetrofitClient;
import com.app.ace_taxi_v2.Models.AvailabilityRequest;
import com.app.ace_taxi_v2.Models.AvailabilityResponse;

import java.util.List;

import io.sentry.Sentry;
import io.sentry.protocol.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AvailabilityAddApi {
    private static final String TAG = "AvailabilityAddApi";
    public Context context;

    public AvailabilityAddApi(Context context) {
        this.context = context;
    }

    public void addAvailability(int userId, String date, String from, String to, boolean giveOrTake, int type, String note) {
        SessionManager sessionManager = new SessionManager(context);
        String token = sessionManager.getToken();

        // Attach user details to Sentry
        User sentryUser = new User();
        sentryUser.setId(String.valueOf(userId));
        Sentry.setUser(sentryUser);

        AvailabilityRequest availabilityRequest = new AvailabilityRequest(userId, date, from, to, giveOrTake, type, note);
        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);

        apiService.addAvailability(token, availabilityRequest).enqueue(new Callback<List<AvailabilityResponse>>() {
            @Override
            public void onResponse(Call<List<AvailabilityResponse>> call, Response<List<AvailabilityResponse>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context, "Added Successfully", Toast.LENGTH_LONG).show();
                } else {
                    String errorMessage = "Availability API Error: HTTP " + response.code() + " - " + response.message();
                    Log.e(TAG, errorMessage);
                    Sentry.captureMessage(errorMessage);
                    Toast.makeText(context, "Failed to add availability", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<AvailabilityResponse>> call, Throwable t) {
                String failureMessage = "Availability API Call Failed: " + t.getMessage();
                Log.e(TAG, failureMessage, t);
                Sentry.captureException(t);
                Toast.makeText(context, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        });
    }
}
