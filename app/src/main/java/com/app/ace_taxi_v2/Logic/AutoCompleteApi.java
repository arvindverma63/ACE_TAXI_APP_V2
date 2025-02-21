package com.app.ace_taxi_v2.Logic;

import android.content.Context;
import android.util.Log;

import com.app.ace_taxi_v2.ApiService.ApiService;
import com.app.ace_taxi_v2.Instance.RetrofitClient;
import com.app.ace_taxi_v2.Models.POI.LocalPOIRequest;
import com.app.ace_taxi_v2.Models.POI.LocalPOIResponse;

import java.util.List;

import io.sentry.Sentry;
import io.sentry.protocol.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AutoCompleteApi {

    private static final String TAG = "AutoCompleteApi";
    private Context context;

    public AutoCompleteApi(Context context) {
        this.context = context;
    }

    public void autoCompleteSearch(String query, AutoCompleteCallback callback) {
        SessionManager sessionManager = new SessionManager(context);
        String token = sessionManager.getToken();
        int userId = sessionManager.getUserId(); // Assuming you can retrieve the user ID

        // Attach user details to Sentry
        User sentryUser = new User();
        sentryUser.setId(String.valueOf(userId));
        Sentry.setUser(sentryUser);

        if (token == null || token.isEmpty()) {
            Log.e(TAG, "Token is missing");
            Sentry.captureMessage("AutoCompleteApi Error: Authentication token missing for user ID: " + userId);
            callback.onFail("Authentication token missing");
            return;
        }

        LocalPOIRequest request = new LocalPOIRequest(query);
        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);

        apiService.autoComplete(token, request).enqueue(new Callback<List<LocalPOIResponse>>() {
            @Override
            public void onResponse(Call<List<LocalPOIResponse>> call, Response<List<LocalPOIResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    String errorMessage = "Failed to retrieve suggestions: HTTP " + response.code() + " - " + response.message();
                    Log.e(TAG, errorMessage);
                    Sentry.captureMessage("AutoCompleteApi Error: " + errorMessage);
                    callback.onFail(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<List<LocalPOIResponse>> call, Throwable t) {
                String failureMessage = "AutoComplete API Call Failed: " + t.getMessage();
                Log.e(TAG, failureMessage, t);
                Sentry.captureException(t);
                callback.onFail(failureMessage);
            }
        });
    }

    public interface AutoCompleteCallback {
        void onSuccess(List<LocalPOIResponse> response);
        void onFail(String error);
    }
}
