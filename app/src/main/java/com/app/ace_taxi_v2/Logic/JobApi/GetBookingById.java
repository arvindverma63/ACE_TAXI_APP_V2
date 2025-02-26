package com.app.ace_taxi_v2.Logic.JobApi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.app.ace_taxi_v2.Activity.JobOfferDialogActivity;
import com.app.ace_taxi_v2.ApiService.ApiService;
import com.app.ace_taxi_v2.Components.CustomDialog;
import com.app.ace_taxi_v2.Instance.RetrofitClient;
import com.app.ace_taxi_v2.JobModals.JobModal;
import com.app.ace_taxi_v2.Logic.SessionManager;
import com.app.ace_taxi_v2.Models.Jobs.Booking;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GetBookingById {
    private final Context context;

    public GetBookingById(Context context){
        this.context = context;
    }

    public void getBookingDetails(int bookingId) {
        SessionManager sessionManager = new SessionManager(context);
        String token = sessionManager.getToken();

        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        apiService.getBookingById(token, bookingId).enqueue(new Callback<Booking>() {
            @Override
            public void onResponse(Call<Booking> call, Response<Booking> response) {
                if (response.body() != null) {
                    Booking booking = response.body();

                    Intent intent = new Intent(context, JobOfferDialogActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  // Important for starting activity from a service

                    // Pass booking details to activity
                    intent.putExtra("pickupAddress", booking.getPickupAddress());
                    intent.putExtra("destinationAddress", booking.getDestinationAddress());
                    intent.putExtra("price", booking.getPrice());
                    intent.putExtra("pickupDate", booking.getPickupDateTime());
                    intent.putExtra("passengerName", booking.getPassengerName());
                    intent.putExtra("bookingId", bookingId);

                    context.startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<Booking> call, Throwable t) {
                Log.e("GetBookingById", "Failed to fetch booking details", t);
            }
        });
    }

}
