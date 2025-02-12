package com.app.ace_taxi_v2.Logic;

import android.content.Context;
import android.util.Log;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.ace_taxi_v2.ApiService.ApiService;
import com.app.ace_taxi_v2.Fragments.Adapters.ExpenseAdapter;
import com.app.ace_taxi_v2.Instance.RetrofitClient;
import com.app.ace_taxi_v2.Models.Expense;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExpensesResponseApi {
    private Context context;

    public ExpensesResponseApi(Context context){
        this.context = context;
    }

    public void getExpenses(String startDate, String endDate, RecyclerView recyclerView, OnExpensesFetchedListener listener) {
        SessionManager sessionManager = new SessionManager(context);
        String token = sessionManager.getToken();
        int userId = sessionManager.getUserId();

        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);

        // ✅ Fix: Change to Call<List<Expense>> instead of Call<ExpensesResponse>
        apiService.getExpenses(token, userId, startDate, endDate).enqueue(new Callback<List<Expense>>() {
            @Override
            public void onResponse(Call<List<Expense>> call, Response<List<Expense>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Expense> expenses = response.body(); // ✅ Directly get the list

                    if (expenses != null && !expenses.isEmpty()) {
                        recyclerView.setLayoutManager(new LinearLayoutManager(context));
                        recyclerView.setAdapter(new ExpenseAdapter(context, expenses)); // ✅ Pass correct data
                        listener.onExpensesFetched(response.body());
                    } else {
                        Log.w("ExpensesResponseApi", "No expenses found.");
                    }
                } else {
                    Log.e("ExpensesResponseApi", "API Response Error: " + response.code() + " - " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Expense>> call, Throwable t) {
                Log.e("ExpensesResponseApi", "API Call Failed", t);
            }
        });
    }
    public interface OnExpensesFetchedListener {
        void onExpensesFetched(List<Expense> expenses);
    }

}
