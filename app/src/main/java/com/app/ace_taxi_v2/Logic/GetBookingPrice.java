package com.app.ace_taxi_v2.Logic;

import android.content.Context;
import android.util.Log;

import com.app.ace_taxi_v2.ApiService.ApiService;
import com.app.ace_taxi_v2.Helper.LogHelperLaravel;
import com.app.ace_taxi_v2.Instance.RetrofitClient;
import com.app.ace_taxi_v2.Models.QuotesRequest;
import com.app.ace_taxi_v2.Models.QuotesResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GetBookingPrice {
    public Context context;
    public GetBookingPrice(Context context) {
        this.context = context;
    }

    public void getBookingPrince(String pickupPostcode, String destinationPostcode, String pickupDatetime, int passengers, BookingPriceCallback callback) {
        try {
            SessionManager sessionManager = new SessionManager(context);
            String token = sessionManager.getToken();
            if (token == null || token.isEmpty()) {
                callback.onError("Authentication token is missing. Please log in again.");
                return;
            }

            List<String> vias = new ArrayList<>();
            QuotesRequest quotesRequest = new QuotesRequest(pickupPostcode, destinationPostcode, pickupDatetime, passengers, true);

            ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
            apiService.getQuotes(token, quotesRequest).enqueue(new Callback<QuotesResponse>() {
                @Override
                public void onResponse(Call<QuotesResponse> call, Response<QuotesResponse> response) {
                    LogHelperLaravel.getInstance().e("get booking price",response.body()+" /n"+response.code());
                    try {
                        if (response.isSuccessful() && response.body() != null) {
                            callback.onSuccess(response.body());
                        } else {
                            callback.onError("Failed to fetch booking price. Error code: " + response.code());
                        }
                    } catch (Exception e) {
                        LogHelperLaravel.getInstance().e("GetBookingPrice", "Error parsing response"+ e);
                        callback.onError("Error processing server response.");
                    }
                }

                @Override
                public void onFailure(Call<QuotesResponse> call, Throwable t) {
                    LogHelperLaravel.getInstance().e("GetBookingPrice", "Network request failed"+ t);
                    callback.onError("Network error occurred. Please check your connection.");
                }
            });
        } catch (Exception e) {
            Log.e("GetBookingPrice", "Unexpected error occurred", e);
            callback.onError("An unexpected error occurred. Please try again later.");
        }
    }

    public interface BookingPriceCallback {
        void onSuccess(QuotesResponse response);
        void onError(String error);
    }
}
