package com.app.ace_taxi_v2.Logic.JobApi;

import android.content.Context;
import android.se.omapi.Session;
import android.util.Log;

import com.app.ace_taxi_v2.ApiService.ApiService;
import com.app.ace_taxi_v2.Instance.RetrofitClient;
import com.app.ace_taxi_v2.Logic.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SetActiveJob {
    public Context context;

    public SetActiveJob(Context context) {
        this.context = context;
    }

    public void setActiveJob(int bookingId){
        SessionManager sessionManager = new SessionManager(context);
        String token = sessionManager.getToken();

        try {
            ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
            apiService.setActiveJob(token, bookingId).enqueue(new Callback<Void>() {

                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    Log.d("set active job response : ", "Job set active successfully"+response.code());
                    if(response.code() == 200){
                        Log.d("Success", "Job set active successfully");
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.d("set active job failded : ", "Job set active fail"+t);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
