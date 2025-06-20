package com.app.ace_taxi_v2.Logic.AddressIO;

import android.content.Context;
import android.util.Log;

import com.app.ace_taxi_v2.ApiService.ApiService;
import com.app.ace_taxi_v2.Helper.LogHelperLaravel;
import com.app.ace_taxi_v2.Instance.RetrofitAddressIO;
import com.app.ace_taxi_v2.Models.AddressIO.AddressIOLocationResponse;
import com.app.ace_taxi_v2.Models.AddressIO.PostcodeResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddressIOGeocodeToLocation {

    private static final String TAG = "AddressIOGeocodeToLoc";
    private static final String API_KEY = "jfLGB-3SeE-asi_xmTyAAA44072";

    private final Context context;

    public AddressIOGeocodeToLocation(Context context) {
        this.context = context;
    }

    public void getLocation(double lat, double lng, PickupAddressPostcode pickupAddressPostcode) {
        ApiService apiService = RetrofitAddressIO.getInstance().create(ApiService.class);


        apiService.getAddressResponse(lat, lng, API_KEY).enqueue(new Callback<AddressIOLocationResponse>() {
            @Override
            public void onResponse(Call<AddressIOLocationResponse> call, Response<AddressIOLocationResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AddressIOLocationResponse locationResponse = response.body();
                    if (locationResponse.getSuggestions() != null && !locationResponse.getSuggestions().isEmpty()) {
                        AddressIOLocationResponse.Suggestion firstSuggestion = locationResponse.getSuggestions().get(0);

                        LogHelperLaravel.getInstance().d(TAG, "Suggestion found: "
                                + "Distance: " + firstSuggestion.getDistance()
                                + ", Location: " + firstSuggestion.getLocation()
                                + ", URL: " + firstSuggestion.getUrl()
                                + ", ID: " + firstSuggestion.getId());

                        // Chain second API call to get postcode
                        apiService.getPostcodeOnly(firstSuggestion.getId(), API_KEY).enqueue(new Callback<PostcodeResponse>() {
                            @Override
                            public void onResponse(Call<PostcodeResponse> call, Response<PostcodeResponse> response2) {
                                if (response2.isSuccessful() && response2.body() != null) {
                                    pickupAddressPostcode.onSuccess(firstSuggestion.getLocation(), response2.body().getPostcode());
                                } else {
                                    String err = "Failed to get postcode: " + response2.code() + " " + response2.message();
                                    LogHelperLaravel.getInstance().e(TAG, err);
                                    pickupAddressPostcode.onError(err);
                                }
                            }

                            @Override
                            public void onFailure(Call<PostcodeResponse> call, Throwable t) {
                                LogHelperLaravel.getInstance().e(TAG, "Postcode API call failed: " + t.getMessage());
                                pickupAddressPostcode.onError(t.getMessage());
                            }
                        });

                    } else {
                        String err = "No suggestions found.";
                        LogHelperLaravel.getInstance().d(TAG, err);
                        pickupAddressPostcode.onError(err);
                    }
                } else {
                    String err = "Address API failed: " + response.code() + " " + response.message();
                    LogHelperLaravel.getInstance().e(TAG, err);
                    pickupAddressPostcode.onError(err);
                }
            }

            @Override
            public void onFailure(Call<AddressIOLocationResponse> call, Throwable t) {
                LogHelperLaravel.getInstance().e(TAG, "Address API call failed: " + t.getMessage());
                pickupAddressPostcode.onError(t.getMessage());
            }
        });
    }

    public interface PickupAddressPostcode {
        void onSuccess(String pickupAddress, String postCode);
        void onError(String error);
    }
}
