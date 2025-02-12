package com.app.ace_taxi_v2.Logic;

import android.content.Context;
import android.util.Log;
import com.app.ace_taxi_v2.ApiService.ApiService;
import com.app.ace_taxi_v2.Instance.RetrofitClient;
import com.app.ace_taxi_v2.Models.POI.LocalPOIRequest;
import com.app.ace_taxi_v2.Models.POI.LocalPOIResponse;
import java.util.List;
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

        if (token == null || token.isEmpty()) {
            Log.e(TAG, "Token is missing");
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
                    Log.e(TAG, "API Response Failed: " + response.message());
                    callback.onFail("Failed to retrieve suggestions: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<LocalPOIResponse>> call, Throwable t) {
                Log.e(TAG, "API Call Failed", t);
                callback.onFail("Error: " + t.getMessage());
            }
        });
    }

    public interface AutoCompleteCallback {
        void onSuccess(List<LocalPOIResponse> response);
        void onFail(String error);
    }
}
