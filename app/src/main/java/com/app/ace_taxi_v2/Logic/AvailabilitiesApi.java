package com.app.ace_taxi_v2.Logic;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.ace_taxi_v2.ApiService.ApiService;
import com.app.ace_taxi_v2.Fragments.Adapters.AvailablitiesAdapter;
import com.app.ace_taxi_v2.Instance.RetrofitClient;
import com.app.ace_taxi_v2.Models.AvailabilityResponse;

import java.util.List;

import io.sentry.Sentry;
import io.sentry.protocol.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AvailabilitiesApi {
    private static final String TAG = "AvailabilitiesApi";
    public Context context;

    public AvailabilitiesApi(Context context) {
        this.context = context;
    }

    public void getAvailablities(RecyclerView recyclerView) {
        SessionManager sessionManager = new SessionManager(context);
        String token = sessionManager.getToken();
        int userId = sessionManager.getUserId(); // Assuming session manager provides the user ID

        // Attach user details to Sentry
        User sentryUser = new User();
        sentryUser.setId(String.valueOf(userId));
        Sentry.setUser(sentryUser);

        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        apiService.getAva(token).enqueue(new Callback<AvailabilityResponse>() {
            @Override
            public void onResponse(Call<AvailabilityResponse> call, Response<AvailabilityResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<AvailabilityResponse.Driver> list = response.body().getDrivers();
                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    recyclerView.setAdapter(new AvailablitiesAdapter(list, context));
                } else {
                    String errorMessage = "Failed to fetch drivers: HTTP " + response.code() + " - " + response.message();
                    Log.e(TAG, errorMessage);
                    Sentry.captureMessage("AvailabilitiesApi Error: " + errorMessage);
                    Toast.makeText(context, "Failed to load data", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<AvailabilityResponse> call, Throwable t) {
                String failureMessage = "Availabilities API Call Failed: " + t.getMessage();
                Log.e(TAG, failureMessage, t);
                Sentry.captureException(t);
                Toast.makeText(context, "Something went wrong with the server", Toast.LENGTH_LONG).show();
            }
        });
    }
}
