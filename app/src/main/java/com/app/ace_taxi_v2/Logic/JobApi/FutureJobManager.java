package com.app.ace_taxi_v2.Logic.JobApi;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.app.ace_taxi_v2.ApiService.ApiService;
import com.app.ace_taxi_v2.Fragments.Adapters.JobAdapters.FutureJobAdapter;
import com.app.ace_taxi_v2.Instance.RetrofitClient;
import com.app.ace_taxi_v2.JobModals.JobModal;
import com.app.ace_taxi_v2.Logic.SessionManager;
import com.app.ace_taxi_v2.Models.Jobs.Booking;
import com.app.ace_taxi_v2.Models.Jobs.FutureJobResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FutureJobManager {
    private Context context;
    SwipeRefreshLayout swipeRefreshLayout;

    public FutureJobManager(Context context,SwipeRefreshLayout swipeRefreshLayout) {
        this.context = context;
        this.swipeRefreshLayout = swipeRefreshLayout;
    }

    public void fetchJob(View view, RecyclerView recyclerView) {
        SessionManager sessionManager = new SessionManager(context);
        String token = sessionManager.getToken();

        if (token == null || token.isEmpty()) {
            Toast.makeText(context, "Authentication error: Token missing", Toast.LENGTH_SHORT).show();
            if (swipeRefreshLayout != null) {
                swipeRefreshLayout.setRefreshing(false);
            }
            return;
        }

        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        apiService.futureJobs(token).enqueue(new Callback<FutureJobResponse>() {
            @Override
            public void onResponse(Call<FutureJobResponse> call, Response<FutureJobResponse> response) {
                Log.d(TAG, "Response: " + response);
                if (swipeRefreshLayout != null) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                if (response.isSuccessful() && response.body() != null) {
                    List<Booking> bookingList = response.body().getBookings();

                    if (bookingList == null || bookingList.isEmpty()) {
                        Toast.makeText(context, "No future jobs available", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    recyclerView.setAdapter(new FutureJobAdapter(bookingList,context, new FutureJobAdapter.OnItemClickListener() {
                        @Override
                        public void onViewClick(Booking booking) {
                            JobModal jobModal = new JobModal(context);
                            jobModal.JobViewForFutureAndHistory(booking.getBookingId());
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
                if (swipeRefreshLayout != null) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                Toast.makeText(context, "Failed to fetch jobs. Check internet connection.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
