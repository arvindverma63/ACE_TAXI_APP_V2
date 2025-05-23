package com.app.ace_taxi_v2.Logic;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.app.ace_taxi_v2.ApiService.ApiService;
import com.app.ace_taxi_v2.Components.CustomToast;
import com.app.ace_taxi_v2.Helper.LogHelperLaravel;
import com.app.ace_taxi_v2.Instance.RetrofitClient;

import io.sentry.Sentry;
import io.sentry.protocol.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeleteAvailbility {
    private static final String TAG = "DeleteAvailbility";
    public Context context;

    public DeleteAvailbility(Context context) {
        this.context = context;
    }

    public void deleteAva(int id) {
        SessionManager sessionManager = new SessionManager(context);
        String token = sessionManager.getToken();
        int userId = sessionManager.getUserId(); // Assuming session manager provides the user ID

        // Attach user details to Sentry
        User sentryUser = new User();
        sentryUser.setId(String.valueOf(userId));
        Sentry.setUser(sentryUser);

        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        apiService.deleteAvailablities(token, id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code() == 200) {
                    CustomToast customToast = new CustomToast(context);
                    customToast.showCustomToast("Availability Deleted");
                    LogHelperLaravel.getInstance().d(TAG, "Availability deleted successfully. ID: " + id);
                } else {
                    String errorMessage = "DeleteAvailbility API Error: HTTP " + response.code() + " - " + response.message();
                    LogHelperLaravel.getInstance().e(TAG, errorMessage);
                    Sentry.captureMessage(errorMessage);
                    Toast.makeText(context, "Failed to delete availability", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                String failureMessage = "DeleteAvailbility API Call Failed: " + t.getMessage();
                LogHelperLaravel.getInstance().e(TAG, failureMessage+ t);
                Sentry.captureException(t);
                Toast.makeText(context, "Something went wrong while deleting", Toast.LENGTH_LONG).show();
            }
        });
    }
}
