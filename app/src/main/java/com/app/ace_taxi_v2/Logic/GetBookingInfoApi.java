package com.app.ace_taxi_v2.Logic;

import android.content.Context;
import android.util.Log;

import com.app.ace_taxi_v2.ApiService.ApiService;
import com.app.ace_taxi_v2.Instance.RetrofitClient;
import com.app.ace_taxi_v2.Models.Jobs.GetBookingInfo;

import io.sentry.Sentry;
import io.sentry.protocol.User;
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
        int userId = sessionManager.getUserId();

        // Attach user details to Sentry
        User sentryUser = new User();
        sentryUser.setId(String.valueOf(userId));
        Sentry.setUser(sentryUser);

        try {
            ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
            apiService.bookingInfo(token,bookingId).enqueue(new Callback<GetBookingInfo>() {
                @Override
                public void onResponse(Call<GetBookingInfo> call, Response<GetBookingInfo> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        callback.onSuccess(response.body());
                    } else {
                        Log.e("GetBookingInfoApi", "API Response Error: " + response.code() + " - " + response.message());
                        Sentry.captureMessage("GetBookingInfoApi Error: HTTP " + response.code() + " - " + response.message());
                        callback.onfailer("Failed to fetch booking info. Server error.");
                    }
                }

                @Override
                public void onFailure(Call<GetBookingInfo> call, Throwable t) {
                    Log.e("GetBookingInfoApi", "API Call Failed", t);
                    Sentry.captureException(t);
                    callback.onfailer("Server not responding");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public interface BookingCallback{
        void onSuccess(GetBookingInfo bookingInfo);
        void onfailer(String error);
    }
}
