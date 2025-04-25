package com.app.ace_taxi_v2.Logic;

import android.content.Context;
import android.util.Log;

import com.app.ace_taxi_v2.ApiService.ApiService;
import com.app.ace_taxi_v2.Instance.RetrofitClient;
import com.app.ace_taxi_v2.Models.Expense;

import java.util.List;

import io.sentry.Sentry;
import io.sentry.protocol.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExpensesResponseApi {
    private static final String TAG = "ExpensesResponseApi";
    private Context context;

    public ExpensesResponseApi(Context context) {
        this.context = context;
    }

    public void getExpenses(String startDate, String endDate, OnExpensesFetchedListener listener) {
        SessionManager sessionManager = new SessionManager(context);
        String token = sessionManager.getToken();
        int userId = sessionManager.getUserId();

        // Attach user details to Sentry
        User sentryUser = new User();
        sentryUser.setId(String.valueOf(userId));
        Sentry.setUser(sentryUser);

        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);

        apiService.getExpenses(token, userId, startDate, endDate).enqueue(new Callback<List<Expense>>() {
            @Override
            public void onResponse(Call<List<Expense>> call, Response<List<Expense>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Expense> expenses = response.body();
                    Log.d(TAG, "Expenses fetched: " + expenses.size());
                    listener.onExpensesFetched(expenses);
                } else {
                    String errorMessage = "Expenses API Error: HTTP " + response.code() + " - " + response.message();
                    Log.e(TAG, errorMessage);
                    Sentry.captureMessage(errorMessage);
                    listener.onExpensesFetched(null);
                }
            }

            @Override
            public void onFailure(Call<List<Expense>> call, Throwable t) {
                String failureMessage = "ExpensesResponse API Call Failed: " + t.getMessage();
                Log.e(TAG, failureMessage, t);
                Sentry.captureException(t);
                listener.onExpensesFetched(null);
            }
        });
    }

    public interface OnExpensesFetchedListener {
        void onExpensesFetched(List<Expense> expenses);
    }
}