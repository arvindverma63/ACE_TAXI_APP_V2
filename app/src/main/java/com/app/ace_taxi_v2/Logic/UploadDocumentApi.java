package com.app.ace_taxi_v2.Logic;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.app.ace_taxi_v2.ApiService.ApiService;
import com.app.ace_taxi_v2.Components.CustomDialog;
import com.app.ace_taxi_v2.Components.CustomToast;
import com.app.ace_taxi_v2.Helper.LogHelperLaravel;
import com.app.ace_taxi_v2.Instance.RetrofitClient;
import com.app.ace_taxi_v2.Models.ImageUploadResponse;
import com.google.android.gms.maps.model.LatLng;

import io.sentry.Sentry;
import io.sentry.protocol.User;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadDocumentApi {
    public Context context;
    public CustomDialog customDialog;

    public UploadDocumentApi(Context context) {
        this.context = context;
        this.customDialog = new CustomDialog();
    }

    public void uploadDoc(MultipartBody.Part body, int type) {
        SessionManager sessionManager = new SessionManager(context);
        String token = sessionManager.getToken();
        int userId = sessionManager.getUserId(); // Get user ID for tracking
        customDialog.showProgressDialog(context);
        // Attach user details to Sentry
        User sentryUser = new User();
        sentryUser.setId(String.valueOf(userId));
        Sentry.setUser(sentryUser);

        // Ensure the token is correctly formatted
        if (token != null && !token.startsWith("Bearer ")) {
            token = "Bearer " + token;
        }

        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        apiService.uploadDoc(token, type, body).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code() == 200) {
                    customDialog.dismissProgressDialog();
                    new CustomToast(context).showCustomToast("uploaded Successfull");
                    LogHelperLaravel.getInstance().d("UploadDocumentApi", "Upload successful: " + response.body());
                } else {
                    customDialog.dismissProgressDialog();
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error details";
                        LogHelperLaravel.getInstance().e("UploadDocumentApi", "Upload failed: " + response.message() + " - " + errorBody);
                        Sentry.captureMessage("UploadDocumentApi Error: HTTP " + response.code() + " - " + errorBody);
                    } catch (Exception e) {
                        LogHelperLaravel.getInstance().e("UploadDocumentApi", "Error reading error body"+ e);
                        Sentry.captureException(e);
                    }
                    Toast.makeText(context, "Upload Failed: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                LogHelperLaravel.getInstance().e("UploadDocumentApi", "Error uploading file"+ t);
                Sentry.captureException(t);
                customDialog.dismissProgressDialog();
                Toast.makeText(context, "Upload failed. Please check your connection.", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
