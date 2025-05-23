package com.app.ace_taxi_v2.Logic;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.app.ace_taxi_v2.ApiService.ApiService;
import com.app.ace_taxi_v2.Helper.LogHelperLaravel;
import com.app.ace_taxi_v2.Instance.RetrofitClient;
import com.app.ace_taxi_v2.Models.Jobs.DateRangeList;
import com.app.ace_taxi_v2.Models.Jobs.DateRangeRequest;
import com.app.ace_taxi_v2.Models.Jobs.DateRangeResponse;

import java.util.List;

import io.sentry.Sentry;
import io.sentry.protocol.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DateRangeApi {
    private static final String TAG = "DateRangeApi";
    private final Context context;

    public DateRangeApi(Context context) {
        this.context = context;
    }

    public void getData(String from, String to, BookingCallback callback) {
        SessionManager sessionManager = new SessionManager(context);
        String token = sessionManager.getToken();
        int userId = sessionManager.getUserId(); // Assuming session manager provides the user ID

        // Attach user details to Sentry
        User sentryUser = new User();
        sentryUser.setId(String.valueOf(userId));
        Sentry.setUser(sentryUser);

        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        DateRangeRequest dateRangeRequest = new DateRangeRequest(from, to);

        apiService.dateRangeResponse(token, dateRangeRequest).enqueue(new Callback<DateRangeList>() {
            @Override
            public void onResponse(Call<DateRangeList> call, Response<DateRangeList> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<DateRangeResponse> list = response.body().getBookings();

                    if (list != null && !list.isEmpty()) {
                        callback.onSuccess(list);
                        LogHelperLaravel.getInstance().d(TAG, "Date range response received: " + list);
                    } else {
                        String noBookingsMessage = "No bookings found for the selected date range.";
                        callback.onFail(noBookingsMessage);
                        Sentry.captureMessage("DateRangeApi: " + noBookingsMessage);
                    }
                } else {
                    String errorMessage = "Server error: HTTP " + response.code() + " - " + response.message();
                    callback.onFail(errorMessage);
                    LogHelperLaravel.getInstance().e(TAG, "API response error: " + response.message());
                    Sentry.captureMessage("DateRangeApi Error: " + errorMessage);
                }
            }

            @Override
            public void onFailure(Call<DateRangeList> call, Throwable t) {
                String failureMessage = "DateRange API Call Failed: " + t.getMessage();
                LogHelperLaravel.getInstance().e(TAG, failureMessage+ t);
                Sentry.captureException(t);
                callback.onFail("Network error: " + t.getMessage());
                Toast.makeText(context, "Failed to fetch data. Please check your connection.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public interface BookingCallback {
        void onSuccess(List<DateRangeResponse> list);
        void onFail(String error);
    }
}
