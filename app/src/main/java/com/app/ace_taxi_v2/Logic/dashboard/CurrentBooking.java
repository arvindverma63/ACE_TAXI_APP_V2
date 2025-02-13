package com.app.ace_taxi_v2.Logic.dashboard;

import android.content.Context;
import android.widget.Toast;

import com.app.ace_taxi_v2.ApiService.ApiService;
import com.app.ace_taxi_v2.Instance.RetrofitClient;
import com.app.ace_taxi_v2.Logic.SessionManager;
import com.app.ace_taxi_v2.Models.Jobs.TodayBooking;
import com.app.ace_taxi_v2.Models.Jobs.TodayJobResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CurrentBooking {
    public Context context;

    public CurrentBooking(Context context){
        this.context = context;
    }

    public void getCurrentBooking(CurrentJobCallback jobCallback){
        SessionManager sessionManager = new SessionManager(context);
        String token = sessionManager.getToken();

        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        apiService.todayJobs(token).enqueue(new Callback<TodayJobResponse>() {
            @Override
            public void onResponse(Call<TodayJobResponse> call, Response<TodayJobResponse> response) {
                if(response.code() == 200){
                    jobCallback.onSuccess(response.body().getBookings());
                    Toast.makeText(context,"success code 200 ok",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<TodayJobResponse> call, Throwable t) {

            }
        });
    }

    public interface CurrentJobCallback{
        void onSuccess(List<TodayBooking> list);
        void onError(String error);
    }
}
