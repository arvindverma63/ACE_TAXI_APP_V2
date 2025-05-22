package com.app.ace_taxi_v2.Logic.AddressIO;

import com.app.ace_taxi_v2.ApiService.ApiService;
import com.app.ace_taxi_v2.Instance.RetrofitAddressIO;
import com.app.ace_taxi_v2.Models.AddressIO.AutocompleteResponse;
import com.app.ace_taxi_v2.Models.AddressIO.PostcodeResponse;
import com.app.ace_taxi_v2.Models.AddressIO.Suggestion;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GetAddressIOAddress {
    private static final String API_KEY = "jfLGB-3SeE-asi_xmTyAAA44072"; // Ideally, move to BuildConfig

    // Context removed as it's unused; re-add if needed for other logic
    public GetAddressIOAddress() {
    }

    public void getAddressId(String text, AddressCallback callback) {
        ApiService apiService = RetrofitAddressIO.getInstance().create(ApiService.class);
        apiService.getAutocompleteResults(text, API_KEY).enqueue(new Callback<AutocompleteResponse>() {
            @Override
            public void onResponse(Call<AutocompleteResponse> call, Response<AutocompleteResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AutocompleteResponse autocompleteResponse = response.body();
                    if (autocompleteResponse.getSuggestions() != null && !autocompleteResponse.getSuggestions().isEmpty()) {
                        Suggestion suggestion = autocompleteResponse.getSuggestions().get(0);
                        callback.onSuccess(suggestion);
                    } else {
                        callback.onError("No suggestions found");
                    }
                } else {
                    callback.onError("Failed to fetch suggestions: " + response.message());
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
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to fetch postcode: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<PostcodeResponse> call, Throwable t) {
                callback.onError("API call failed: " + t.getMessage());
            }
        });
    }

    public interface AddressCallback {
        void onSuccess(Suggestion suggestion);
        void onError(String error);
    }

    public interface PostcodeCallback {
        void onSuccess(PostcodeResponse postcodeResponse);
        void onError(String error);
    }
}