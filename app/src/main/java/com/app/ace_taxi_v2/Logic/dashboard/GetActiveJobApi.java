package com.app.ace_taxi_v2.Logic.dashboard;

import android.content.Context;

import com.app.ace_taxi_v2.ApiService.ApiService;
import com.app.ace_taxi_v2.Instance.RetrofitClient;
import com.app.ace_taxi_v2.Logic.SessionManager;
import com.app.ace_taxi_v2.Models.GetActiveJobResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GetActiveJobApi {
    public Context context;
    public GetActiveJobApi(Context context){
        this.context = context;
    }

    public void getActiviteJob(GetActiveJobCallback getActiveJobCallback){
        SessionManager sessionManager = new SessionManager(context);
        String token = sessionManager.getToken();
        try {
            ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
            apiService.getActiveJob(token).enqueue(new Callback<GetActiveJobResponse>() {
                @Override
                public void onResponse(Call<GetActiveJobResponse> call, Response<GetActiveJobResponse> response) {
                    if(response.code() == 200){
                        getActiveJobCallback.onSuccess(response.body());
                    }
                }

                @Override
                public void onFailure(Call<GetActiveJobResponse> call, Throwable t) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public interface GetActiveJobCallback{
        void onSuccess(GetActiveJobResponse response);
        void onFailure(Throwable t);
    }
}
