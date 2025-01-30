package com.example.ace_taxi_v2.Logic.JobApi;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ace_taxi_v2.ApiService.ApiService;
import com.example.ace_taxi_v2.Fragments.Adapters.JobAdapters.TodayJobAdapter;
import com.example.ace_taxi_v2.Instance.RetrofitClient;
import com.example.ace_taxi_v2.Logic.SessionManager;
import com.example.ace_taxi_v2.Models.Jobs.TodayBooking;
import com.example.ace_taxi_v2.Models.Jobs.TodayJobResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TodayJobManager {
    private final Context context;

    public TodayJobManager(Context context) {
        this.context = context;
    }

    public void getTodayJobs(View view, RecyclerView recyclerView) {
        SessionManager sessionManager = new SessionManager(context);
        String token = sessionManager.getToken();

        if (token == null || token.isEmpty()) {
            Toast.makeText(context, "Authentication error: Token missing", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        apiService.todayJobs(token).enqueue(new Callback<TodayJobResponse>() {
            @Override
            public void onResponse(Call<TodayJobResponse> call, Response<TodayJobResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<TodayBooking> bookingList = response.body().getBookings();

                    if (bookingList == null || bookingList.isEmpty()) {
                        Toast.makeText(context, "No jobs available for today", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setAdapter(new TodayJobAdapter(bookingList, new TodayJobAdapter.OnItemClickListener() {
                        @Override
                        public void onViewClick(TodayBooking booking) {
                            new GetBookingById(context).getBookingDetails(booking.getBookingId());
                        }

                        @Override
                        public void onStartClick(TodayBooking booking) {
                            Toast.makeText(context, "Starting job: " + booking.getBookingId(), Toast.LENGTH_SHORT).show();
                        }
                    }));
                } else {
                    Toast.makeText(context, "Error fetching jobs: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TodayJobResponse> call, Throwable t) {
                Toast.makeText(context, "Failed to fetch jobs. Check internet connection.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
