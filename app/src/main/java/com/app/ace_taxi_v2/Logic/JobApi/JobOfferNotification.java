package com.app.ace_taxi_v2.Logic.JobApi;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;

import com.app.ace_taxi_v2.ApiService.ApiService;
import com.app.ace_taxi_v2.Instance.RetrofitClient;
import com.app.ace_taxi_v2.Logic.SessionManager;
import com.app.ace_taxi_v2.Models.JobOfferNoticationResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JobOfferNotification {
    public Context context;
    public JobOfferNotification(Context context){
        this.context = context;
    }

    public void getResponse(String guid,jobOfferCallback callback){
        SessionManager sessionManager = new SessionManager(context);
        String token = sessionManager.getToken();

        try{
            ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
            apiService.getJobOffer(token,guid).enqueue(new Callback<JobOfferNoticationResponse>() {
                @Override
                public void onResponse(Call<JobOfferNoticationResponse> call, Response<JobOfferNoticationResponse> response) {
                    if(response.code() == 200){
                        callback.onJobOfferResponse(response.body());
                    }
                }

                @Override
                public void onFailure(Call<JobOfferNoticationResponse> call, Throwable t) {
                    callback.onError(t.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG,"expecption in Job offer notification api : "+e.getMessage());
        }
    }

    public interface jobOfferCallback{
        void onJobOfferResponse(JobOfferNoticationResponse response);
        void onError(String errorMessage);
    }
}
