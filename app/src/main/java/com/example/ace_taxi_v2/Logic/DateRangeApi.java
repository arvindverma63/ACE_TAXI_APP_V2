package com.example.ace_taxi_v2.Logic;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.ace_taxi_v2.ApiService.ApiService;
import com.example.ace_taxi_v2.Instance.RetrofitClient;
import com.example.ace_taxi_v2.Models.Jobs.DateRangeList;
import com.example.ace_taxi_v2.Models.Jobs.DateRangeRequest;
import com.example.ace_taxi_v2.Models.Jobs.DateRangeResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DateRangeApi {
    private final Context context;

    public DateRangeApi(Context context) {
        this.context = context;
    }

    public void getData(String from, String to, BookingCallback callback) {
        SessionManager sessionManager = new SessionManager(context);
        String token = sessionManager.getToken();

        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        DateRangeRequest dateRangeRequest = new DateRangeRequest(from, to);

        apiService.dateRangeResponse(token, dateRangeRequest).enqueue(new Callback<DateRangeList>() {
            @Override
            public void onResponse(Call<DateRangeList> call, Response<DateRangeList> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<DateRangeResponse> list = response.body().getBookings();

                    if (list != null && !list.isEmpty()) {
                        callback.onSuccess(list);
                        Log.e("Date range response : ",""+list);

                    } else {
                        callback.onFail("No bookings found for the selected date range.");
                    }
                } else {
                    callback.onFail("Server error: " + response.message());
                    Log.e("DateRangeApi", "API response error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<DateRangeList> call, Throwable t) {
                Log.e("DateRangeApi", "API call failed", t);
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
