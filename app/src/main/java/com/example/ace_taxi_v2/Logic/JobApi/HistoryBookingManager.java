package com.example.ace_taxi_v2.Logic.JobApi;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ace_taxi_v2.ApiService.ApiService;
import com.example.ace_taxi_v2.Fragments.Adapters.JobAdapters.HistoryAdapter;
import com.example.ace_taxi_v2.Instance.RetrofitClient;
import com.example.ace_taxi_v2.Logic.SessionManager;
import com.example.ace_taxi_v2.Models.Jobs.HistoryBooking;
import com.example.ace_taxi_v2.Models.Jobs.HistoryJobResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryBookingManager {
    private final Context context;

    public HistoryBookingManager(Context context) { // Fixed constructor visibility
        this.context = context;
    }

    public void getHistoryBookings(View view, RecyclerView recyclerView) {
        SessionManager sessionManager = new SessionManager(context);
        String token = sessionManager.getToken();

        if (token == null || token.isEmpty()) {
            Toast.makeText(context, "Authentication error: Token missing", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        apiService.historyJobs(token).enqueue(new Callback<HistoryJobResponse>() {
            @Override
            public void onResponse(Call<HistoryJobResponse> call, Response<HistoryJobResponse> response) {
                Log.d(TAG, "Response: " + response);

                if (response.isSuccessful() && response.body() != null) {
                    List<HistoryBooking> bookingList = response.body().getBookings();

                    if (bookingList == null || bookingList.isEmpty()) {
                        Toast.makeText(context, "No past bookings available", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    recyclerView.setHasFixedSize(true); // Performance optimization
                    recyclerView.setAdapter(new HistoryAdapter(bookingList, new HistoryAdapter.OnItemClickListener() {
                        @Override
                        public void onViewClick(HistoryBooking booking) {
                            new GetBookingById(context).getBookingDetails(booking.getBookingId());
                        }

                        @Override
                        public void onStartClick(HistoryBooking booking) {
                            Toast.makeText(context, "Revisiting job: " + booking.getBookingId(), Toast.LENGTH_SHORT).show();
                        }
                    }));
                } else {
                    Toast.makeText(context, "Error fetching history: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<HistoryJobResponse> call, Throwable t) {
                Toast.makeText(context, "Failed to fetch jobs: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error: " + t);
            }
        });
    }
}
