package com.example.ace_taxi_v2.Logic;

import android.content.Context;

import com.example.ace_taxi_v2.ApiService.ApiService;
import com.example.ace_taxi_v2.Instance.RetrofitClient;
import com.example.ace_taxi_v2.Models.Jobs.GetBookingInfo;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class GetBookingInfoApi {
    public Context context;
    public GetBookingInfoApi(Context context){
        this.context = context;
    }

    public void getInfo(int bookingId,BookingCallback callback){
        SessionManager sessionManager = new SessionManager(context);
        String token = sessionManager.getToken();
        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        apiService.bookingInfo(token,bookingId).enqueue(new Callback<GetBookingInfo>() {
            @Override
            public void onResponse(Call<GetBookingInfo> call, Response<GetBookingInfo> response) {
                callback.onSuccess(response.body());
            }

            @Override
            public void onFailure(Call<GetBookingInfo> call, Throwable t) {
                callback.onfailer("Server not responding");
            }
        });
    }

    public interface BookingCallback{
        void onSuccess(GetBookingInfo bookingInfo);
        void onfailer(String error);
    }
}
