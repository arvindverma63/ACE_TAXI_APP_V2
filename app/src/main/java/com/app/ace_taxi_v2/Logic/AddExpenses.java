package com.app.ace_taxi_v2.Logic;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.app.ace_taxi_v2.ApiService.ApiService;
import com.app.ace_taxi_v2.Components.CustomDialog;
import com.app.ace_taxi_v2.Components.CustomToast;
import com.app.ace_taxi_v2.Helper.LogHelperLaravel;
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

    public void addExpnses(String date, String description, double amount, int category,ExpensesCallback expensesCallback) {
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
                LogHelperLaravel.getInstance().d(TAG, "Response code: " + response.code());

                if (response.code() == 200) {
                    new CustomToast(context).showCustomToast("Added Successfully");
                    expensesCallback.onSuccess(true);
                } else {
                    new CustomToast(context).showCustomErrorToast("Failed to add expense");

                    // Log non-successful API responses to Sentry
                    LogHelperLaravel.getInstance().e("API Error: ",+ response.code() + " - " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "Failed to add expense", Toast.LENGTH_LONG).show();
                new CustomToast(context).showCustomErrorToast("Failed to add expense");

                // Capture exception in Sentry
                Sentry.captureException(t);
            }
        });

        customDialog.dismissProgressDialog();
    }

    public interface ExpensesCallback{
        void onSuccess(Boolean ok);
        void onError(String error);
    }
}
