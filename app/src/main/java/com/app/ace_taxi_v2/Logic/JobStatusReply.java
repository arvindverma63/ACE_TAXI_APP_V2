package com.app.ace_taxi_v2.Logic;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.app.ace_taxi_v2.ApiService.ApiService;
import com.app.ace_taxi_v2.Components.CustomDialog;
import com.app.ace_taxi_v2.Components.JobStatusModal;
import com.app.ace_taxi_v2.Instance.RetrofitClient;
import com.app.ace_taxi_v2.Logic.Service.CurrentBookingSession;
import com.app.ace_taxi_v2.Models.JobStatusModel;

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
        apiService.jobStatus(token,jobno,status).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.e("job stauts modal status ","response : "+response);
                if(response.code() == 200){
                    Toast.makeText(context, "Job Updated Successfully", Toast.LENGTH_SHORT).show();
                    CurrentBookingSession currentBookingSession = new CurrentBookingSession(context);
                    currentBookingSession.saveBookingId(jobno);
                    currentBookingSession.saveBookingShift(String.valueOf(status));
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("exception from server","error"+t);
            }
        });
        customDialog.dismissProgressDialog();
    }
}
