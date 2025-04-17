package com.app.ace_taxi_v2.Logic.JobApi;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

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

    public GetBookingById(Context context) {
        this.context = (Context) context;
    }

    public void getBookingDetails(int bookingId) {
        SessionManager sessionManager = new SessionManager(context);
        String token = sessionManager.getToken();

        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        apiService.getBookingById(token, bookingId).enqueue(new Callback<Booking>() {
            @Override
            public void onResponse(Call<Booking> call, Response<Booking> response) {
                CustomDialog customDialog = new CustomDialog();
                customDialog.showProgressDialog(context);

                try {
                    if (!response.isSuccessful()) {
                        Log.e("GetBookingById", "Error: " + response.code() + " - " + response.message());
                        Toast.makeText(context, "Error fetching booking details: " + response.message(), Toast.LENGTH_LONG).show();
                        return;
                    }

                    Booking booking = response.body();
                    if (booking == null) {
                        Log.e("GetBookingById", "Error: Booking response is null");
                        Toast.makeText(context, "Booking details not found", Toast.LENGTH_LONG).show();
                        return;
                    }

                    Log.d("opening job modal : ","opening job dialog : bookingId: "+bookingId);
                    JobModal jobModal = new JobModal(context);
                    jobModal.jobOfferModal(
                            booking.getPickupAddress(),
                            booking.getDestinationAddress(),
                            booking.getPrice(),
                            booking.getPickupDateTime(),
                            booking.getPassengerName(),
                            bookingId
                    );

                } catch (Exception e) {
                    Log.e("GetBookingById", "Exception occurred: " + e.getMessage(), e);
                    Toast.makeText(context, "An unexpected error occurred", Toast.LENGTH_LONG).show();
                } finally {
                    customDialog.dismissProgressDialog();
                }
            }

            @Override
            public void onFailure(Call<Booking> call, Throwable t) {
                Log.e("GetBookingById", "API Call Failed: " + t.getMessage(), t);
                Toast.makeText(context, "Network error! Please try again.", Toast.LENGTH_LONG).show();
            }
        });
    }
}
