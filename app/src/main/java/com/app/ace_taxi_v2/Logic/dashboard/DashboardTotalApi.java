package com.app.ace_taxi_v2.Logic.dashboard;

import android.content.Context;
import android.util.Log;

import com.app.ace_taxi_v2.ApiService.ApiService;
import com.app.ace_taxi_v2.Instance.RetrofitClient;
import com.app.ace_taxi_v2.Logic.SessionManager;
import com.app.ace_taxi_v2.Models.Dashtotal;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardTotalApi {
    private final Context context;

    public DashboardTotalApi(Context context) {
        this.context = context;
    }

    public void getData(DashCallback callback) {
        SessionManager sessionManager = new SessionManager(context);
        String token = sessionManager.getToken();

        if (token == null || token.isEmpty()) {
            callback.onError("Authentication error: Token missing");
            return;
        }

        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        apiService.getDashTotal(token).enqueue(new Callback<Dashtotal>() {
            @Override
            public void onResponse(Call<Dashtotal> call, Response<Dashtotal> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to load dashboard data: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Dashtotal> call, Throwable t) {
                Log.e("DashboardTotalApi", "API call failed", t);
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public interface DashCallback {
        void onSuccess(Dashtotal dashtotal);
        void onError(String error);
    }
}
