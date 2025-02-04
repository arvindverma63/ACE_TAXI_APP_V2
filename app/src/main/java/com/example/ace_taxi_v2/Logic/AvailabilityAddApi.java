package com.example.ace_taxi_v2.Logic;

import android.content.Context;
import android.widget.Toast;

import com.example.ace_taxi_v2.ApiService.ApiService;
import com.example.ace_taxi_v2.Instance.RetrofitClient;
import com.example.ace_taxi_v2.Models.AvailabilityRequest;
import com.example.ace_taxi_v2.Models.AvailabilityResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AvailabilityAddApi {
    public Context context;

    public AvailabilityAddApi(Context context){
        this.context = context;
    }

    public void addAvailability(int userId,String date,String from,String to,boolean giveOrTake,int type,String note){
        SessionManager sessionManager = new SessionManager(context);
        String token = sessionManager.getToken();

        AvailabilityRequest availabilityRequest = new AvailabilityRequest(userId,date,from,to,giveOrTake,type,note);
        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        apiService.addAvailability(token,availabilityRequest).enqueue(new Callback<List<AvailabilityResponse>>() {
            @Override
            public void onResponse(Call<List<AvailabilityResponse>> call, Response<List<AvailabilityResponse>> response) {

                if(response.isSuccessful()){
                    Toast.makeText(context,"Added Successfully",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<AvailabilityResponse>> call, Throwable t) {
                Toast.makeText(context,"Added Successfully",Toast.LENGTH_LONG).show();
            }
        });
    }

}
