package com.example.ace_taxi_v2.Logic;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.ace_taxi_v2.ApiService.ApiService;
import com.example.ace_taxi_v2.Instance.RetrofitClient;
import com.example.ace_taxi_v2.Models.ImageUploadResponse;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadDocumentApi {
    public Context context;

    public UploadDocumentApi(Context context) {
        this.context = context;
    }

    public void uploadDoc(MultipartBody.Part body, int type) {
        SessionManager sessionManager = new SessionManager(context);
        String token = sessionManager.getToken();

        // Ensure the token is correctly formatted
        if (token != null && !token.startsWith("Bearer ")) {
            token = "Bearer " + token;
        }

        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        apiService.uploadDoc(token, type, body).enqueue(new Callback<ImageUploadResponse>() {
            @Override
            public void onResponse(Call<ImageUploadResponse> call, Response<ImageUploadResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(context, "Upload Successful!", Toast.LENGTH_SHORT).show();
                    Log.d("UploadDocumentApi", "Upload successful: " + response.body());
                } else {
                    Toast.makeText(context, "Upload Failed: " + response.message(), Toast.LENGTH_SHORT).show();
                    Log.e("UploadDocumentApi", "Upload failed: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<ImageUploadResponse> call, Throwable t) {
                Log.e("UploadDocumentApi", "Error uploading file", t);
            }
        });
    }
}
