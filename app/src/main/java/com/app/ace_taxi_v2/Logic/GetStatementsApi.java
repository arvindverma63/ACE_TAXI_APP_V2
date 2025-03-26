package com.app.ace_taxi_v2.Logic;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.ace_taxi_v2.ApiService.ApiService;
import com.app.ace_taxi_v2.Components.CustomDialog;
import com.app.ace_taxi_v2.Fragments.Adapters.StatementAdapter;
import com.app.ace_taxi_v2.Instance.RetrofitClient;
import com.app.ace_taxi_v2.Models.Reports.StatementItem;

import java.util.List;

import io.sentry.Sentry;
import io.sentry.protocol.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GetStatementsApi {
    private final Context context;

    public GetStatementsApi(Context context) {
        this.context = context;
    }

    public void getStatements(View view, RecyclerView recyclerView, statementListener listener) {
        SessionManager sessionManager = new SessionManager(context);
        String token = sessionManager.getToken();
        int userId = sessionManager.getUserId();
        User sentryUser = new User();
        sentryUser.setId(String.valueOf(userId));
        Sentry.setUser(sentryUser);

        CustomDialog customDialog = new CustomDialog();
        customDialog.showProgressDialog(context);

        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        apiService.getStatements(token).enqueue(new Callback<List<StatementItem>>() {
            @Override
            public void onResponse(Call<List<StatementItem>> call, Response<List<StatementItem>> response) {
                customDialog.dismissProgressDialog(); // Hide dialog on response

                if (response.isSuccessful() && response.body() != null) {
                    Log.d("Statement Response : ", "" + response.body());
                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    recyclerView.setAdapter(new StatementAdapter(context, response.body()));
                    listener.onSuccess(response.body());
                } else {
                    String errorMessage = "GetStatementsApi Error: HTTP " + response.code() + " - " + response.message();
                    Toast.makeText(context, "Error: " + response.message(), Toast.LENGTH_LONG).show();
                    Log.e("GetStatementsApi", errorMessage);
                    Sentry.captureMessage(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<List<StatementItem>> call, Throwable t) {
                customDialog.dismissProgressDialog(); // Hide dialog on failure

                String failureMessage = "GetStatementsApi API Call Failed: " + t.getMessage();
                Log.e("GetStatementsApi", failureMessage, t);
                Sentry.captureException(t);

                Toast.makeText(context, "Failed to load statements. Check your internet connection.", Toast.LENGTH_LONG).show();
                listener.onFail("error from api");
            }
        });
    }

    public interface statementListener {
        void onSuccess(List<StatementItem> item);
        void onFail(String error);
    }
}
