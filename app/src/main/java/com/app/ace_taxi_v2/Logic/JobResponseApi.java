package com.app.ace_taxi_v2.Logic;

import android.content.Context;
import android.widget.Toast;

import com.app.ace_taxi_v2.ApiService.ApiService;
import com.app.ace_taxi_v2.Instance.RetrofitClient;
import com.app.ace_taxi_v2.Models.JobResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JobResponseApi {
    public Context context;
    public SessionManager sessionManager;

    public JobResponseApi(Context context){
        this.context = context;
        sessionManager = new SessionManager(context);
    }

    public void acceptResponse(int bookingId){
        String token = sessionManager.getToken();

        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        apiService.sendJobResponse(token,bookingId,2000).enqueue(new Callback<JobResponse>() {
            @Override
            public void onResponse(Call<JobResponse> call, Response<JobResponse> response) {
                Toast.makeText(context,"Job Accept Successfully",Toast.LENGTH_LONG);
            }

            @Override
            public void onFailure(Call<JobResponse> call, Throwable t) {
                Toast.makeText(context,"Job Accept failed",Toast.LENGTH_LONG);
            }
        });
    }
    public void rejectBooking(int bookingId){
        String token = sessionManager.getToken();

        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        apiService.sendJobResponse(token,bookingId,2001).enqueue(new Callback<JobResponse>() {
            @Override
            public void onResponse(Call<JobResponse> call, Response<JobResponse> response) {
                Toast.makeText(context,"Job Accept Successfully",Toast.LENGTH_LONG);
            }

            @Override
            public void onFailure(Call<JobResponse> call, Throwable t) {
                Toast.makeText(context,"Job Accept failed",Toast.LENGTH_LONG);
            }
        });
    }
}
