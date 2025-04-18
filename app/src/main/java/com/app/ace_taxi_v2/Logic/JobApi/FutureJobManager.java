package com.app.ace_taxi_v2.Logic.JobApi;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.app.ace_taxi_v2.ApiService.ApiService;
import com.app.ace_taxi_v2.Fragments.Adapters.JobAdapters.FutureJobAdapter;
import com.app.ace_taxi_v2.Instance.RetrofitClient;
import com.app.ace_taxi_v2.JobModals.JobModal;
import com.app.ace_taxi_v2.JobModals.JobViewDialog;
import com.app.ace_taxi_v2.Logic.SessionManager;
import com.app.ace_taxi_v2.Models.Jobs.Booking;
import com.app.ace_taxi_v2.Models.Jobs.FutureJobResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FutureJobManager {
    private final Context context;
    private final SwipeRefreshLayout swipeRefreshLayout;
    private final SessionManager sessionManager;
    private FutureJobAdapter adapter;

    public FutureJobManager(Context context, SwipeRefreshLayout swipeRefreshLayout) {
        this.context = context;
        this.swipeRefreshLayout = swipeRefreshLayout;
        this.sessionManager = new SessionManager(context);
    }

    public void fetchJob(View view, RecyclerView recyclerView) {
        String token = sessionManager.getToken();

        if (token == null || token.isEmpty()) {
            showToast("Authentication error: Token missing");
            stopRefreshing();
            return;
        }

        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        apiService.futureJobs(token).enqueue(new Callback<FutureJobResponse>() {
            @Override
            public void onResponse(Call<FutureJobResponse> call, Response<FutureJobResponse> response) {
                stopRefreshing();
                Log.d(TAG, "Response: " + response);

                if (!response.isSuccessful() || response.body() == null) {
                    showToast("Error fetching jobs: " + response.code());
                    return;
                }

                List<Booking> bookingList = response.body().getBookings();

                if (bookingList == null || bookingList.isEmpty()) {
//                    showToast("No future jobs available");
                    return;
                }

                // If adapter is already set, just update the list
                if (adapter == null) {
                    adapter = new FutureJobAdapter(bookingList, context, new FutureJobAdapter.OnItemClickListener() {
                        @Override
                        public void onViewClick(Booking booking) {
                            JobViewDialog jobModal = new JobViewDialog(context);
                            jobModal.JobViewForFutureAndHistory(booking.getBookingId());
                        }

                        @Override
                        public void onStartClick(Booking booking) {
                            // Handle start click (if required)
                        }
                    });

                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    recyclerView.setAdapter(adapter);
                } else {
                    adapter.notifyDataSetChanged(); // Refresh only data instead of recreating adapter
                }
            }

            @Override
            public void onFailure(Call<FutureJobResponse> call, Throwable t) {
                stopRefreshing();
                showToast("Network error: Please check your internet connection.");
                Log.e(TAG, "API Call Failed", t);
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    private void stopRefreshing() {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }
}
