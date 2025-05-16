package com.app.ace_taxi_v2.Logic.JobApi;

import android.content.Context;
import android.util.Log;
import com.app.ace_taxi_v2.ApiService.ApiService;
import com.app.ace_taxi_v2.Components.CustomToast;
import com.app.ace_taxi_v2.Instance.RetrofitClient;
import com.app.ace_taxi_v2.Logic.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateBookingApi {
    private static final String TAG = "CreateBookingApi";
    private final Context context;
    public CustomToast customToast;

    public interface CreateBookingCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }

    public CreateBookingApi(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Context cannot be null");
        }
        this.context = context;
        this.customToast = new CustomToast(context);
    }

    public void createNewBooking(String destination, String destinationPostCode, String name,double price,
                                 CreateBookingCallback callback) {
        // Input validation
        if (destination == null || destination.trim().isEmpty()) {
            Log.e(TAG, "Invalid destination: null or empty");
            callback.onFailure("Destination cannot be empty");
            return;
        }
        if (destinationPostCode == null || destinationPostCode.trim().isEmpty()) {
            Log.e(TAG, "Invalid postcode: null or empty");
            callback.onFailure("Postcode cannot be empty");
            return;
        }
        if (name == null || name.trim().isEmpty()) {
            Log.e(TAG, "Invalid name: null or empty");
            callback.onFailure("Name cannot be empty");
            return;
        }
        if (callback == null) {
            Log.e(TAG, "Callback is null");
            throw new IllegalArgumentException("Callback cannot be null");
        }

        try {
            SessionManager sessionManager = new SessionManager(context);
            String token = sessionManager.getToken();
            int userId = sessionManager.getUserId();

            // Validate session data
            if (token == null || token.trim().isEmpty()) {
                Log.e(TAG, "Invalid token: null or empty");
                callback.onFailure("Authentication token is missing");
                return;
            }
            if (userId <= 0) {
                Log.e(TAG, "Invalid user ID: " + userId);
                callback.onFailure("Invalid user ID");
                return;
            }
            Log.e("request data for create booking"," "+token+"/n "+destination+" "+destinationPostCode+" "+name+" "+userId+" "+price);
            ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
            Call<Void> call = apiService.rankCreate(token, destination, destinationPostCode, name, userId, price);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    Log.e("create booking respone: ",response.body()+"");
                    if (response.code()== 200) {
                        Log.i(TAG, "Booking created successfully");
                        customToast.showCustomToast("Booking created successfully");
                        callback.onSuccess();
                    } else {
                        String errorMessage = handleHttpError(response.code());
                        customToast.showCustomToast(errorMessage);
                        Log.e(TAG, "Booking creation failed with code: " + response.code() +
                                ", message: " + errorMessage);
                        callback.onFailure(errorMessage);
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    String errorMessage = "Network error: " + t.getMessage();
                    Log.e(TAG, "Booking creation failed: " + errorMessage, t);
                    callback.onFailure(errorMessage);
                }
            });
        } catch (SecurityException e) {
            Log.e(TAG, "Security exception while accessing session data", e);
            callback.onFailure("Authentication error: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Unexpected error during booking creation", e);
            callback.onFailure("Unexpected error: " + e.getMessage());
        }
    }

    private String handleHttpError(int code) {
        switch (code) {
            case 400:
                return "Invalid request data";
            case 401:
                return "Unauthorized: Invalid or expired token";
            case 403:
                return "Forbidden: Access denied";
            case 404:
                return "Service not found";
            case 500:
                return "Server error";
            default:
                return "HTTP error " + code;
        }
    }
}