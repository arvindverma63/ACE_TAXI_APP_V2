package com.example.ace_taxi_v2.Logic;

import android.content.Context;

import com.example.ace_taxi_v2.ApiService.ApiService;
import com.example.ace_taxi_v2.Instance.RetrofitClient;
import com.example.ace_taxi_v2.Models.ExpensesRequest;
import com.example.ace_taxi_v2.Models.ExpensesResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddExpenses {
    public Context context;
    public AddExpenses(Context context){
        this.context = context;
    }

    public void addExpnses(String date,String description,double amount,int category){
        SessionManager sessionManager = new SessionManager(context);
        String token = sessionManager.getToken();
        int userId = sessionManager.getUserId();

        ExpensesRequest expensesRequest = new ExpensesRequest(userId,date,description,amount,category);
        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        apiService.expenses(token,expensesRequest).enqueue(new Callback<ExpensesResponse>() {
            @Override
            public void onResponse(Call<ExpensesResponse> call, Response<ExpensesResponse> response) {

            }

            @Override
            public void onFailure(Call<ExpensesResponse> call, Throwable t) {

            }
        });
    }
}
