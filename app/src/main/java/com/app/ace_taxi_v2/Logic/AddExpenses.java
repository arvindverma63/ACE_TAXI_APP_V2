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

        CustomDialog customDialog = new CustomDialog();
        customDialog.showProgressDialog(context);
        // Create the request model
        ExpensesRequest expensesRequest = new ExpensesRequest(userId, date, description, amount, category);

        // Create the API service instance and make the call
        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        apiService.expenses(token, expensesRequest).enqueue(new Callback<ExpensesResponse>() {
            @Override
            public void onResponse(Call<ExpensesResponse> call, Response<ExpensesResponse> response) {
                Log.d(TAG,"Response code : "+response.code());
                if (response.isSuccessful()) {
                    Toast.makeText(context, "Added Successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ExpensesResponse> call, Throwable t) {
                Toast.makeText(context,"Added",Toast.LENGTH_LONG).show();
            }
        });
        customDialog.dismissProgressDialog();
    }

}
