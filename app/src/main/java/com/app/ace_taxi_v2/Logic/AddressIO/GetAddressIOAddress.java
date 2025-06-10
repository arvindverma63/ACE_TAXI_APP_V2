package com.app.ace_taxi_v2.Logic.AddressIO;

import static android.content.ContentValues.TAG;

import com.app.ace_taxi_v2.ApiService.ApiService;
import com.app.ace_taxi_v2.Helper.LogHelperLaravel;
import com.app.ace_taxi_v2.Instance.RetrofitAddressIO;
import com.app.ace_taxi_v2.Models.AddressIO.AutocompleteResponse;
import com.app.ace_taxi_v2.Models.AddressIO.GetAddressLocationRequest;
import com.app.ace_taxi_v2.Models.AddressIO.PostcodeResponse;
import com.app.ace_taxi_v2.Models.AddressIO.Suggestion;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GetAddressIOAddress {
    private static final String API_KEY = "jfLGB-3SeE-asi_xmTyAAA44072"; // Ideally, move to BuildConfig

    public GetAddressIOAddress() {
    }

    public void getAddressId(String text, AddressCallback callback) {
        ApiService apiService = RetrofitAddressIO.getInstance().create(ApiService.class);
        GetAddressLocationRequest getAddressLocationRequest = new GetAddressLocationRequest(51.0388, -2.2799);
        apiService.getAutocompleteResults(text, API_KEY, getAddressLocationRequest).enqueue(new Callback<AutocompleteResponse>() { // Fixed type
            @Override
            public void onResponse(Call<AutocompleteResponse> call, Response<AutocompleteResponse> response) {
                if (response.code() == 200) {
                    AutocompleteResponse autocompleteResponse = response.body();
                    List<Suggestion> suggestions = autocompleteResponse.getSuggestions();
                    if (suggestions != null && !suggestions.isEmpty()) {
                        callback.onSuccess(suggestions); // Return all suggestions
                    } else {
                        callback.onError("No suggestions found");
                    }
                } else {
//                    callback.onError("Failed to fetch suggestions: " + response.message());
                    LogHelperLaravel.getInstance().d(TAG, "Failed to fetch suggestions: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<AutocompleteResponse> call, Throwable t) {
                callback.onError("API call failed: " + t.getMessage());
            }
        });
    }

    public void getPostcode(String id, PostcodeCallback callback) {
        ApiService apiService = RetrofitAddressIO.getInstance().create(ApiService.class);
        apiService.getPostcodeOnly(id, API_KEY).enqueue(new Callback<PostcodeResponse>() {
            @Override
            public void onResponse(Call<PostcodeResponse> call, Response<PostcodeResponse> response) {
                if (response.code() == 200) {
                    callback.onSuccess(response.body());
                } else {
                    LogHelperLaravel.getInstance().d(TAG, "Failed to fetch suggestions: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<PostcodeResponse> call, Throwable t) {
//                callback.onError("API call failed: " + t.getMessage());
                LogHelperLaravel.getInstance().e(TAG, "API call failed: " + t.getMessage());
            }
        });
    }

    public interface AddressCallback {
        void onSuccess(List<Suggestion> suggestions); // Updated to handle List<Suggestion>
        void onError(String error);
    }

    public interface PostcodeCallback {
        void onSuccess(PostcodeResponse postcodeResponse);
        void onError(String error);
    }
}