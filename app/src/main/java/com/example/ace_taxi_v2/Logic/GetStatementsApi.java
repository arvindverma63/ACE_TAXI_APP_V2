package com.example.ace_taxi_v2.Logic;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ace_taxi_v2.ApiService.ApiService;
import com.example.ace_taxi_v2.Fragments.Adapters.StatementAdapter;
import com.example.ace_taxi_v2.Instance.RetrofitClient;
import com.example.ace_taxi_v2.Models.Reports.StatementItem;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GetStatementsApi {
    private final Context context;

    public GetStatementsApi(Context context) {
        this.context = context;
    }

    public void getStatements(View view, RecyclerView recyclerView,statementListener listener) {
        SessionManager sessionManager = new SessionManager(context);
        String token = sessionManager.getToken();


        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        apiService.getStatements(token).enqueue(new Callback<List<StatementItem>>() {
            @Override
            public void onResponse(Call<List<StatementItem>> call, Response<List<StatementItem>> response) {
                // Hide progress bar (if used)
                // progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    Log.d("Statement Respoinse : ",""+response.body());
                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    recyclerView.setAdapter(new StatementAdapter(context, response.body()));
                    listener.onSuccess(response.body());
                } else {
                    Toast.makeText(context, "Error: " + response.message(), Toast.LENGTH_LONG).show();
                    Log.e("GetStatementsApi", "API Response Error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<StatementItem>> call, Throwable t) {
                // Hide progress bar (if used)
                // progressBar.setVisibility(View.GONE);

                Toast.makeText(context, "Failed to load statements. Check your internet connection.", Toast.LENGTH_LONG).show();
                Log.e("GetStatementsApi", "API Request Failed", t);
                listener.onFail("error from api");
            }
        });
    }

    public interface statementListener{
        void onSuccess(List<StatementItem> item);
        void onFail(String error);
    }
}
