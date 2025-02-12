package com.app.ace_taxi_v2.Logic.JobApi;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.ace_taxi_v2.ApiService.ApiService;
import com.app.ace_taxi_v2.Fragments.Adapters.JobAdapters.FutureJobAdapter;
import com.app.ace_taxi_v2.Instance.RetrofitClient;
import com.app.ace_taxi_v2.Logic.SessionManager;
import com.app.ace_taxi_v2.Models.Jobs.Booking;
import com.app.ace_taxi_v2.Models.Jobs.FutureJobResponse;
import com.app.ace_taxi_v2.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FutureJobManager {
    private Context context;

    public FutureJobManager(Context context) {
        this.context = context;
    }

    public void fetchJob(View view, RecyclerView recyclerView) {
        SessionManager sessionManager = new SessionManager(context);
        String token = sessionManager.getToken();

        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        apiService.futureJobs(token).enqueue(new Callback<FutureJobResponse>() {
            @Override
            public void onResponse(Call<FutureJobResponse> call, Response<FutureJobResponse> response) {
                Log.d(TAG, "Response: " + response);

                if (response.isSuccessful() && response.body() != null) {
                    List<Booking> bookingList = response.body().getBookings();

                    if (bookingList == null || bookingList.isEmpty()) {
                        Toast.makeText(context, "No future jobs available", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    recyclerView.setAdapter(new FutureJobAdapter(bookingList, new FutureJobAdapter.OnItemClickListener() {
                        @Override
                        public void onViewClick(Booking booking) {

                        }

                        @Override
                        public void onStartClick(Booking booking) {
                            // Handle start click
                        }
                    }));
                } else {
                    Toast.makeText(context, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<FutureJobResponse> call, Throwable t) {
                Toast.makeText(context, "Failed to fetch jobs: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error: " + t);
            }
        });
    }
}
