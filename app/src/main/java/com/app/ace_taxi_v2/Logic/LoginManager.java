package com.app.ace_taxi_v2.Logic;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.app.ace_taxi_v2.Activity.HomeActivity;
import com.app.ace_taxi_v2.ApiService.ApiService;
import com.app.ace_taxi_v2.Components.CustomDialog;
import com.app.ace_taxi_v2.Components.CustomToast;
import com.app.ace_taxi_v2.Helper.LogHelperLaravel;
import com.app.ace_taxi_v2.Instance.RetrofitClient;
import com.app.ace_taxi_v2.Models.LoginRequest;
import com.app.ace_taxi_v2.Models.LoginResponse;
import com.app.ace_taxi_v2.Models.UserProfileResponse;

import io.sentry.Sentry;
import io.sentry.protocol.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginManager {
    private Context context;
    public CustomToast customToast;

    public LoginManager(Context context) {
        this.context = context;
        this.customToast = new CustomToast(context);
    }

    public void login(String username, String password) {
        CustomDialog progressDialog = new CustomDialog();
        progressDialog.showProgressDialog(context);
        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        LoginRequest loginRequest = new LoginRequest(username, password);

        apiService.loginUser(loginRequest).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String token = response.body().getToken();
                    int userId = response.body().getUserId();
                    String username = response.body().getUsername();
                    LogHelperLaravel.getInstance().i("User Login", "Login Successfull "+userId+" "+username);
                    // Attach user details to Sentry
                    User sentryUser = new User();
                    sentryUser.setId(String.valueOf(userId));
                    sentryUser.setUsername(username);
                    Sentry.setUser(sentryUser);

                    SessionManager sessionManager = new SessionManager(context);
                    sessionManager.saveSession(token, userId, username);

                    customToast.showCustomToast("Login Successfull");
                    progressDialog.dismissProgressDialog();

                    UpdateFCMApi updateFCMApi = new UpdateFCMApi(context);
                    updateFCMApi.updateFcm();

                    Intent intent = new Intent(context, HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    context.startActivity(intent);
                } else {
                    String errorMessage = "Login Failed: HTTP " + response.code() + " - " + response.message();
                    LogHelperLaravel.getInstance().e("LoginManager", errorMessage);
                    Sentry.captureMessage(errorMessage);
                    customToast.showCustomErrorToast("Incorrect Username Or Password");
                    progressDialog.dismissProgressDialog();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                String failureMessage = "LoginManager API Call Failed: " + t.getMessage();
                Log.e("LoginManager", failureMessage, t);
                Sentry.captureException(t);
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismissProgressDialog();
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
                                String errorMessage = "Profile Fetch Failed: HTTP " + response.code() + " - " + response.message();
                                LogHelperLaravel.getInstance().e("LoginManager", errorMessage);
                                Sentry.captureMessage(errorMessage);
                                callback.onFailure("Invalid response from server");
                            }
                        }

                        @Override
                        public void onFailure(Call<UserProfileResponse> call, Throwable t) {
                            String failureMessage = "Profile Fetch API Call Failed: " + t.getMessage();
                            LogHelperLaravel.getInstance().e("LoginManager", failureMessage+"  "+ t);
                            Sentry.captureException(t);
                            callback.onFailure(t.getMessage());
                        }
                    });
        } catch (Exception e) {
            Sentry.captureException(e);
            throw new RuntimeException(e);
        }
    }

    public interface ProfileCallback {
        void onSuccess(UserProfileResponse userProfileResponse);

        void onFailure(String errorMessage);
    }
}
