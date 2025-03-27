package com.app.ace_taxi_v2.Logic;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.app.ace_taxi_v2.ApiService.ApiService;
import com.app.ace_taxi_v2.Components.CustomDialog;
import com.app.ace_taxi_v2.Instance.RetrofitClient;
import com.app.ace_taxi_v2.Models.ExpensesRequest;
import com.app.ace_taxi_v2.Models.ExpensesResponse;

import io.sentry.Sentry;
import io.sentry.protocol.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddExpenses {
    private Context context;

    public AddExpenses(Context context) {
        this.context = context;
    }

    public void addExpnses(String date, String description, double amount, int category) {
        // Get session data
        SessionManager sessionManager = new SessionManager(context);
        String token = sessionManager.getToken();
        int userId = sessionManager.getUserId();
        // Attach user details to Sentry
        User sentryUser = new User();
        sentryUser.setId(String.valueOf(userId));
        Sentry.setUser(sentryUser);

        CustomDialog customDialog = new CustomDialog();
        customDialog.showProgressDialog(context);

        // Create the request model
        ExpensesRequest expensesRequest = new ExpensesRequest(userId, date, description, amount, category);

        // Create the API service instance and make the call
        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        apiService.expenses(token, expensesRequest).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.d(TAG, "Response code: " + response.code());

                if (response.isSuccessful()) {
                    Toast.makeText(context, "Added Successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();

                    // Log non-successful API responses to Sentry
                    Sentry.captureMessage("API Error: " + response.code() + " - " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "Failed to add expense", Toast.LENGTH_LONG).show();

                // Capture exception in Sentry
                Sentry.captureException(t);
            }
        });

        customDialog.dismissProgressDialog();
    }
}
