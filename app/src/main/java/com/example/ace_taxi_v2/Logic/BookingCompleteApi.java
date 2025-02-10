package com.example.ace_taxi_v2.Logic;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.ace_taxi_v2.ApiService.ApiService;
import com.example.ace_taxi_v2.Instance.RetrofitClient;
import com.example.ace_taxi_v2.Models.BookingRequest.BookingCompleteRequest;
import com.example.ace_taxi_v2.Models.BookingRequest.BookingCompleteResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingCompleteApi {
    public Context context;

    public BookingCompleteApi(Context context){
        this.context = context;
    }

    public void complete(int bookingId,int bookingTime,int parking,double drivePrice,double accountPrice){
        SessionManager sessionManager = new SessionManager(context);
        String token = sessionManager.getToken();

        BookingCompleteRequest bookingCompleteRequest = new BookingCompleteRequest(bookingId,bookingTime,parking,drivePrice,accountPrice);
        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        apiService.completeBooking(token,bookingCompleteRequest).enqueue(new Callback<BookingCompleteResponse>() {
            @Override
            public void onResponse(Call<BookingCompleteResponse> call, Response<BookingCompleteResponse> response) {
                Toast.makeText(context,"Booking Completed",Toast.LENGTH_LONG).show();
                Log.i("complete booking status","complete booking status : "+response);
            }

            @Override
            public void onFailure(Call<BookingCompleteResponse> call, Throwable t) {

            }
        });
    }
}
