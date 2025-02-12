package com.app.ace_taxi_v2.Logic;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import com.app.ace_taxi_v2.ApiService.ApiService;
import com.app.ace_taxi_v2.Instance.RetrofitClient;
import com.app.ace_taxi_v2.Models.DriverShiftResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateDriverShiftApi {
    private final Context context;

    public UpdateDriverShiftApi(Context context) {
        this.context = context;
    }

    public void updateStatus(int status) {
        SessionManager sessionManager = new SessionManager(context);
        String token = sessionManager.getToken();
        int userId = sessionManager.getUserId();

        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        apiService.driverShift(token, userId, status).enqueue(new Callback<DriverShiftResponse>() {
            @Override
            public void onResponse(Call<DriverShiftResponse> call, Response<DriverShiftResponse> response) {
                Log.d("DriverShiftAPI", "Response Code: " + response.code());

                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Toast.makeText(context, "Status updated successfully", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(context, "Status updated, but no response body", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(context, "API Error: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<DriverShiftResponse> call, Throwable t) {
                Log.e("DriverShiftAPI", "API Call Failed: " + t.getMessage());
                Toast.makeText(context, "Network Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
