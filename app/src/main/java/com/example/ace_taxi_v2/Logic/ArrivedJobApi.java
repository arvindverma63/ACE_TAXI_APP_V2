package com.example.ace_taxi_v2.Logic;

import android.content.Context;
import android.widget.Toast;

import com.example.ace_taxi_v2.ApiService.ApiService;
import com.example.ace_taxi_v2.Instance.RetrofitClient;
import com.example.ace_taxi_v2.Models.Jobs.ArrivedResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArrivedJobApi {
    public Context context;

    public ArrivedJobApi(Context context){
        this.context = context;
    }

    public void updateStatus(int bookingId){
        SessionManager sessionManager = new SessionManager(context);
        String token = sessionManager.getToken();

        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        apiService.arrivedStatusUpdate(token,bookingId).enqueue(new Callback<ArrivedResponse>() {
            @Override
            public void onResponse(Call<ArrivedResponse> call, Response<ArrivedResponse> response) {
                Toast.makeText(context,"Status Updated Successfully",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<ArrivedResponse> call, Throwable t) {
                Toast.makeText(context,"Server Response error",Toast.LENGTH_LONG).show();
            }
        });
    }
}
