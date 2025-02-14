package com.app.ace_taxi_v2.Logic;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.ace_taxi_v2.ApiService.ApiService;
import com.app.ace_taxi_v2.Fragments.Adapters.AvailablitiesAdapter;
import com.app.ace_taxi_v2.Instance.RetrofitClient;
import com.app.ace_taxi_v2.Models.AvailabilityResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AvailabilitiesApi {
    public Context context;

    public AvailabilitiesApi(Context context){
        this.context = context;
    }

    public void getAvailablities(RecyclerView recyclerView){
        SessionManager sessionManager = new SessionManager(context);
        String token = sessionManager.getToken();

        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        apiService.getAva(token).enqueue(new Callback<AvailabilityResponse>() {
            @Override
            public void onResponse(Call<AvailabilityResponse> call, Response<AvailabilityResponse> response) {
                if(response.isSuccessful()){
                    List<AvailabilityResponse.Driver> list = response.body().getDrivers();
                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    recyclerView.setAdapter(new AvailablitiesAdapter(list,context));
                }
            }

            @Override
            public void onFailure(Call<AvailabilityResponse> call, Throwable t) {
                Toast.makeText(context,"Something wrong from server",Toast.LENGTH_LONG).show();
            }
        });
    }
}
