package com.app.ace_taxi_v2.Logic;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.app.ace_taxi_v2.ApiService.ApiService;
import com.app.ace_taxi_v2.Instance.RetrofitClient;
import com.app.ace_taxi_v2.Models.Jobs.ArrivedResponse;

import io.sentry.Sentry;
import io.sentry.protocol.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArrivedJobApi {
    private Context context;

    public ArrivedJobApi(Context context) {
        this.context = context;
    }

    public void updateStatus(int bookingId) {
        SessionManager sessionManager = new SessionManager(context);
        String token = sessionManager.getToken();
        int userId = sessionManager.getUserId(); // Assuming you can get the user ID


        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        apiService.arrivedStatusUpdate(token, bookingId).enqueue(new Callback<ArrivedResponse>() {
            @Override
            public void onResponse(Call<ArrivedResponse> call, Response<ArrivedResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context, "Status Updated Successfully", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, "Failed to update status", Toast.LENGTH_LONG).show();

                    // Log non-successful API response to Sentry
                    Sentry.captureMessage("ArrivedJobApi Error: HTTP " + response.code() + " - " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ArrivedResponse> call, Throwable t) {
                Toast.makeText(context, "Server Response Error", Toast.LENGTH_LONG).show();

                // Capture the network failure or exception in Sentry
                Sentry.captureException(t);
            }
        });
    }
}
