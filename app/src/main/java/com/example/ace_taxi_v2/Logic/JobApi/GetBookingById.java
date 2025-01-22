package com.example.ace_taxi_v2.Logic.JobApi;

import android.content.Context;
import android.util.Log;

import com.example.ace_taxi_v2.ApiService.ApiService;
import com.example.ace_taxi_v2.Components.CustomDialog;
import com.example.ace_taxi_v2.Instance.RetrofitClient;
import com.example.ace_taxi_v2.JobModals.JobModal;
import com.example.ace_taxi_v2.Logic.SessionManager;
import com.example.ace_taxi_v2.Models.Jobs.Booking;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GetBookingById {
    private final Context context;

    public GetBookingById(Context context){
        this.context = context;
    }

    public void getBookingDetails(int bookingId){
        SessionManager sessionManager = new SessionManager(context);
        String token = sessionManager.getToken();

        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        apiService.getBookingById(token,bookingId).enqueue(new Callback<Booking>() {
            @Override
            public void onResponse(Call<Booking> call, Response<Booking> response) {
                CustomDialog customDialog = new CustomDialog();
                customDialog.showProgressDialog(context);
                JobModal jobModal = new JobModal(context);
                jobModal.futureJobView(response.body().getPickupAddress(),
                        response.body().getDestinationAddress(),
                        response.body().getPrice(),
                        response.body().getPickupDateTime(),
                        response.body().getPassengerName());
                customDialog.dismissProgressDialog();
            }

            @Override
            public void onFailure(Call<Booking> call, Throwable t) {

            }
        });

    }
}
