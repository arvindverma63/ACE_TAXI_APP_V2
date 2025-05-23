package com.app.ace_taxi_v2.Helper;

import android.content.Context;
import android.util.Log;

import com.app.ace_taxi_v2.ApiService.ApiService;
import com.app.ace_taxi_v2.Instance.RetrofitLogs;
import com.app.ace_taxi_v2.Models.Log.LogRequest;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LogHelperLaravel {
    private static LogHelperLaravel instance;
    private static Context appContext;
    private static final String API_URL = "https://logger.avblog.io";
    private static final int MAX_RETRIES = 3;

    private LogHelperLaravel(Context context) {
        appContext = context.getApplicationContext();
    }

    public static void init(Context context) {
        if (instance == null) {
            instance = new LogHelperLaravel(context);
        }
    }

    public static LogHelperLaravel getInstance() {
        if (instance == null) {
            throw new IllegalStateException("LogHelperLaravel is not initialized, call init(context) first.");
        }
        return instance;
    }

    private void sendLogToBackend(LogRequest logRequest, String tag, int retryCount) {
        ApiService apiService = RetrofitLogs.getInstance().create(ApiService.class);
        Call<Void> call = apiService.log(logRequest);

        // Log the request for debugging
        Log.d(tag, "Sending log to backend: type=" + logRequest.getType() +
                ", message=" + logRequest.getMessage() +
                ", source=" + logRequest.getSource());

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(tag, "Log sent successfully: " + logRequest.getMessage());
                } else {
                    String errorMsg = "Failed to send log: HTTP " + response.code();
                    try {
                        errorMsg += ", " + response.errorBody().string();
                    } catch (IOException e) {
                        errorMsg += ", unable to parse error body: " + e.getMessage();
                    }
                    Log.e(tag, errorMsg);
                    if (retryCount < MAX_RETRIES) {
                        Log.d(tag, "Retrying log send, attempt " + (retryCount + 1));
                        sendLogToBackend(logRequest, tag, retryCount + 1);
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                String errorMsg = "Failed to send log: " + t.getMessage();
                Log.e(tag, errorMsg, t);
                if (retryCount < MAX_RETRIES) {
                    Log.d(tag, "Retrying log send, attempt " + (retryCount + 1));
                    sendLogToBackend(logRequest, tag, retryCount + 1);
                }
            }
        });
    }

    public void e(String tag, String message) {
        Log.e(tag, message); // Local logging
        LogRequest logRequest = new LogRequest("error", message, tag);
        sendLogToBackend(logRequest, tag, 0);
    }

    public void d(String tag, String message) {
        Log.d(tag, message); // Local logging
        // Optionally send to backend; comment out if not needed
        LogRequest logRequest = new LogRequest("warn", message, tag);
        sendLogToBackend(logRequest, tag, 0);
    }

    public void i(String tag, String message) {
        Log.i(tag, message); // Local logging
        // Optionally send to backend; comment out if not needed
        LogRequest logRequest = new LogRequest("info", message, tag);
        sendLogToBackend(logRequest, tag, 0);
    }
}