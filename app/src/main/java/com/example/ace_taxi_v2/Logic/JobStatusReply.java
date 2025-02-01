package com.example.ace_taxi_v2.Logic;

import android.content.Context;
import android.widget.Toast;

import com.example.ace_taxi_v2.ApiService.ApiService;
import com.example.ace_taxi_v2.Components.CustomDialog;
import com.example.ace_taxi_v2.Components.JobStatusModal;
import com.example.ace_taxi_v2.Instance.RetrofitClient;
import com.example.ace_taxi_v2.Models.JobStatusModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JobStatusReply {
    public Context context;

    public JobStatusReply(Context context){
        this.context = context;
    }

    public void updateStatus(int jobno,int status){
        SessionManager sessionManager = new SessionManager(context);
        String token = sessionManager.getToken();
        CustomDialog customDialog = new CustomDialog();
        customDialog.showProgressDialog(context);

        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        apiService.jobStatus(token,jobno,status).enqueue(new Callback<JobStatusModel>() {
            @Override
            public void onResponse(Call<JobStatusModel> call, Response<JobStatusModel> response) {
                if(response.isSuccessful()){
                    Toast.makeText(context, "Job Updated Successfully", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JobStatusModel> call, Throwable t) {
                Toast.makeText(context, "Nothing get from server", Toast.LENGTH_SHORT).show();
            }
        });
        customDialog.dismissProgressDialog();
    }
}
