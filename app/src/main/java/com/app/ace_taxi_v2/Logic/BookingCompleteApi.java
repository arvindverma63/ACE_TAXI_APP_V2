package com.app.ace_taxi_v2.Logic;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.app.ace_taxi_v2.ApiService.ApiService;
import com.app.ace_taxi_v2.Components.BookingStartStatus;
import com.app.ace_taxi_v2.Helper.LogHelperLaravel;
import com.app.ace_taxi_v2.Instance.RetrofitClient;
import com.app.ace_taxi_v2.Models.BookingRequest.BookingCompleteRequest;

import io.sentry.Sentry;
import io.sentry.protocol.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingCompleteApi {
    private static final String TAG = "BookingCompleteApi";
    public Context context;

    public BookingCompleteApi(Context context) {
        this.context = context;
    }

    public void complete(int bookingId, int bookingTime, int parking, double drivePrice, double accountPrice,double tip) {
        SessionManager sessionManager = new SessionManager(context);
        String token = sessionManager.getToken();
        int userId = sessionManager.getUserId(); // Assuming session manager provides the user ID

        // Attach user details to Sentry
        User sentryUser = new User();
        sentryUser.setId(String.valueOf(userId));
        Sentry.setUser(sentryUser);

        BookingCompleteRequest bookingCompleteRequest = new BookingCompleteRequest(bookingId, bookingTime, parking, drivePrice, accountPrice,tip);
        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);

        apiService.completeBooking(token, bookingCompleteRequest).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code() == 200) {
                    Toast.makeText(context, "Booking Completed", Toast.LENGTH_LONG).show();
                    BookingStartStatus bookingStartStatus = new BookingStartStatus(context);
                    bookingStartStatus.clearBookingId();

                } else {
                    String errorMessage = "BookingCompleteApi Error: HTTP " + response.code() + " - " + response.message();
                    LogHelperLaravel.getInstance().e(TAG, errorMessage);
                    Sentry.captureMessage(errorMessage);
                    Toast.makeText(context, "Booking completion failed", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                String failureMessage = "BookingComplete API Call Failed: " + t.getMessage();
                LogHelperLaravel.getInstance().e(TAG, failureMessage+ t);
                Sentry.captureException(t);
                Toast.makeText(context, "Something went wrong while completing the booking", Toast.LENGTH_LONG).show();
            }
        });
    }
}
