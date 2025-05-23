package com.app.ace_taxi_v2.Logic;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.app.ace_taxi_v2.ApiService.ApiService;
import com.app.ace_taxi_v2.Components.CustomDialog;
import com.app.ace_taxi_v2.Helper.LogHelperLaravel;
import com.app.ace_taxi_v2.Logic.Service.CurrentBookingSession;
import com.app.ace_taxi_v2.Instance.RetrofitClient;

import io.sentry.Sentry;
import io.sentry.protocol.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JobStatusReply {
    public Context context;

    public JobStatusReply(Context context) {
        this.context = context;
    }

    public void updateStatus(int jobno, int status) {
        SessionManager sessionManager = new SessionManager(context);
        String token = sessionManager.getToken();
        int userId = sessionManager.getUserId(); // Getting user ID for tracking

        // Attach user details to Sentry
        User sentryUser = new User();
        sentryUser.setId(String.valueOf(userId));
        Sentry.setUser(sentryUser);

        CustomDialog customDialog = new CustomDialog();
        customDialog.showProgressDialog(context);

        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        apiService.jobStatus(token, jobno, status).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                LogHelperLaravel.getInstance().e("job stauts modal status ", "response : " + response);

                if (response.code() == 200) {
                    Toast.makeText(context, "Job Updated Successfully", Toast.LENGTH_SHORT).show();
                    CurrentBookingSession currentBookingSession = new CurrentBookingSession(context);
                    currentBookingSession.saveBookingId(jobno);
                    currentBookingSession.saveBookingShift(String.valueOf(status));
                } else {
                    String errorMessage = "JobStatusReply API Error: HTTP " + response.code() + " - " + response.message();
                    Sentry.captureMessage(errorMessage);
                    LogHelperLaravel.getInstance().e("JobStatusReply", errorMessage);
                    Toast.makeText(context, "Failed to update job status", Toast.LENGTH_SHORT).show();
                }

                customDialog.dismissProgressDialog();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                customDialog.dismissProgressDialog();
                String failureMessage = "JobStatusReply API Call Failed: " + t.getMessage();
                LogHelperLaravel.getInstance().e("exception from server", "error" + t);
                Sentry.captureException(t);
                Toast.makeText(context, "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
