package com.app.ace_taxi_v2.Logic;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.app.ace_taxi_v2.ApiService.ApiService;
import com.app.ace_taxi_v2.Components.CustomDialog;
import com.app.ace_taxi_v2.Instance.RetrofitClient;
import com.app.ace_taxi_v2.Models.Reports.StatementItem;

import java.util.List;

import io.sentry.Sentry;
import io.sentry.protocol.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GetStatementsApi {
    private static final String TAG = "GetStatementsApi";
    private final Context context;

    public String from , to;
    public int userId;
    public GetStatementsApi(Context context,String from,String to,int UserId) {
        this.context = context;
        this.from = from;
        this.to = to;
        this.userId = userId;
    }

    public void getStatements(statementListener listener) {
        SessionManager sessionManager = new SessionManager(context);
        String token = sessionManager.getToken();
        int userId = sessionManager.getUserId();

        if (token == null || token.isEmpty()) {
            Toast.makeText(context, "Authentication token missing", Toast.LENGTH_SHORT).show();
            Sentry.captureMessage("GetStatementsApi Error: Missing authentication token for user ID: " + userId);
            listener.onFail("Missing authentication token");
            return;
        }

        User sentryUser = new User();
        sentryUser.setId(String.valueOf(userId));
        Sentry.setUser(sentryUser);

        CustomDialog customDialog = new CustomDialog();
        customDialog.showProgressDialog(context);

        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        apiService.getStatements(token,from,to,userId).enqueue(new Callback<List<StatementItem>>() {
            @Override
            public void onResponse(Call<List<StatementItem>> call, Response<List<StatementItem>> response) {
                customDialog.dismissProgressDialog();

                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Statement Response: " + response.body());
                    listener.onSuccess(response.body());
                } else {
                    String errorMessage = "GetStatementsApi Error: HTTP " + response.code() + " - " + response.message();
                    Log.e(TAG, errorMessage);
                    Sentry.captureMessage(errorMessage);
                    Toast.makeText(context, "Error: " + response.message(), Toast.LENGTH_LONG).show();
                    listener.onFail(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<List<StatementItem>> call, Throwable t) {
                customDialog.dismissProgressDialog();

                String failureMessage = "GetStatementsApi API Call Failed: " + t.getMessage();
                Log.e(TAG, failureMessage, t);
                Sentry.captureException(t);
                Toast.makeText(context, "Failed to load statements. Check your internet connection.", Toast.LENGTH_LONG).show();
                listener.onFail(failureMessage);
            }
        });
    }

    public interface statementListener {
        void onSuccess(List<StatementItem> item);
        void onFail(String error);
    }
}