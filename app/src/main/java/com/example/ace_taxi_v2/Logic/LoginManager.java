package com.example.ace_taxi_v2.Logic;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;


import com.example.ace_taxi_v2.Activity.HomeActivity;
import com.example.ace_taxi_v2.ApiService.ApiService;
import com.example.ace_taxi_v2.Components.CustomDialog;
import com.example.ace_taxi_v2.Instance.RetrofitClient;
import com.example.ace_taxi_v2.Models.LoginRequest;
import com.example.ace_taxi_v2.Models.LoginResponse;
import com.example.ace_taxi_v2.Models.UserProfileResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginManager {
    private Context context;

    public LoginManager(Context context) {
        this.context = context;
    }

    public void login(String username, String password) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.show();
        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        LoginRequest loginRequest = new LoginRequest(username, password);

        apiService.loginUser(loginRequest).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String token = response.body().getToken();
                    int userId = response.body().getUserId();
                    String username = response.body().getUsername();

                    SessionManager sessionManager = new SessionManager(context);
                    sessionManager.saveSession(token, userId, username);

                    Toast.makeText(context, "Login Successful!", Toast.LENGTH_SHORT).show();
                    progressDialog.hide();

//                    UpdateFCMApi updateFCMApi = new UpdateFCMApi(context);
//                    updateFCMApi.updateFcm();

                    Intent intent = new Intent(context, HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    context.startActivity(intent);
                } else {
                    Toast.makeText(context, "Incorrect Username Or Password: " + response.message(), Toast.LENGTH_SHORT).show();
                    progressDialog.hide();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("error", "" + t.getMessage());
                progressDialog.hide();
            }
        });
    }

    public void getProfile(ProfileCallback callback) {
        SessionManager sessionManager = new SessionManager(context);
        String jwtToken = sessionManager.getToken();

        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);

        try {
            apiService.userProfile(sessionManager.getUsername(), "Bearer " + jwtToken)
                    .enqueue(new Callback<UserProfileResponse>() {
                        @Override
                        public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                callback.onSuccess(response.body());
                            } else {
                                callback.onFailure("Invalid response from server");
                            }
                        }

                        @Override
                        public void onFailure(Call<UserProfileResponse> call, Throwable t) {
                            callback.onFailure(t.getMessage());
                        }
                    });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public interface ProfileCallback {
        void onSuccess(UserProfileResponse userProfileResponse);

        void onFailure(String errorMessage);
    }
}