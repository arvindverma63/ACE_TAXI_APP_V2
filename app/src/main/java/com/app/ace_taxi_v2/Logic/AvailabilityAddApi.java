package com.app.ace_taxi_v2.Logic;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.app.ace_taxi_v2.ApiService.ApiService;
import com.app.ace_taxi_v2.Components.CustomDialog;
import com.app.ace_taxi_v2.Components.CustomToast;
import com.app.ace_taxi_v2.Instance.RetrofitClient;
import com.app.ace_taxi_v2.JobModals.BottomSheetDialogs;
import com.app.ace_taxi_v2.Models.AvailabilityRequest;
import com.app.ace_taxi_v2.Models.AvailabilityResponse;

import java.util.List;

import io.sentry.Sentry;
import io.sentry.protocol.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AvailabilityAddApi {
    private static final String TAG = "AvailabilityAddApi";
    public Context context;

    public AvailabilityAddApi(Context context) {
        this.context = context;
    }

    public void addAvailability(int userId, String date, String from, String to, boolean giveOrTake, int type, String note) {
        SessionManager sessionManager = new SessionManager(context);
        String token = sessionManager.getToken();
        CustomDialog customDialog = new CustomDialog();
        customDialog.showProgressDialog(context);

        // Attach user details to Sentry
        User sentryUser = new User();
        sentryUser.setId(String.valueOf(userId));
        Sentry.setUser(sentryUser);

        AvailabilityRequest availabilityRequest = new AvailabilityRequest(userId, date, from, to, giveOrTake, type, note);
        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);

        apiService.addAvailability(token, availabilityRequest).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code() == 200) {
//                    BottomSheetDialogs bottomSheetDialogs = new BottomSheetDialogs(context);
//                    bottomSheetDialogs.addAvail();
                    CustomToast customToast = new CustomToast(context);
                    customToast.showCustomToast("Availability Updated");
                    customDialog.dismissProgressDialog();
                } else {
                    String errorMessage = "Availability API Error: HTTP " + response.code() + " - " + response.message();
                    Log.e(TAG, errorMessage);
                    Sentry.captureMessage(errorMessage);
                    BottomSheetDialogs bottomSheetDialogs = new BottomSheetDialogs(context);
                    bottomSheetDialogs.alreadyAdd();
                    customDialog.dismissProgressDialog();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                String failureMessage = "Availability API Call Failed: " + t.getMessage();
                Log.e(TAG, failureMessage, t);
                Sentry.captureException(t);
                customDialog.dismissProgressDialog();
            }
        });
    }
}
