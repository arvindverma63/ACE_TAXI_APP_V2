package com.app.ace_taxi_v2.Logic.JobApi;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.app.ace_taxi_v2.ApiService.ApiService;
import com.app.ace_taxi_v2.Components.CustomToast;
import com.app.ace_taxi_v2.Fragments.Adapters.JobAdapters.HistoryAdapter;
import com.app.ace_taxi_v2.Instance.RetrofitClient;
import com.app.ace_taxi_v2.JobModals.JobModal;
import com.app.ace_taxi_v2.JobModals.JobViewDialog;
import com.app.ace_taxi_v2.Logic.SessionManager;
import com.app.ace_taxi_v2.Models.Jobs.HistoryBooking;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryBookingManager {
    private final Context context;
    public SwipeRefreshLayout swipeRefreshLayout;
    public TextView noBookingTextView;
    public CustomToast customToast;

    public HistoryBookingManager(Context context,SwipeRefreshLayout swipeRefreshLayout,TextView noBookingTextView) { // Fixed constructor visibility
        this.context = context;
        this.swipeRefreshLayout = swipeRefreshLayout;
        this.noBookingTextView = noBookingTextView;
        this.customToast = new CustomToast(context);
    }

    public void getHistoryBookings(View view, RecyclerView recyclerView) {
        SessionManager sessionManager = new SessionManager(context);
        String token = sessionManager.getToken();

        if (token == null || token.isEmpty()) {
            Toast.makeText(context, "Authentication error: Token missing", Toast.LENGTH_SHORT).show();
            if (swipeRefreshLayout != null) {
                swipeRefreshLayout.setRefreshing(false);
            }
            return;
        }

        if (token == null || token.isEmpty()) {
            Toast.makeText(context, "Authentication error: Token missing", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        apiService.historyJobs(token).enqueue(new Callback<List<HistoryBooking>>() {
            @Override
            public void onResponse(Call<List<HistoryBooking>> call, Response<List<HistoryBooking>> response) {
                Log.d(TAG, "Response: " + response);
                if (swipeRefreshLayout != null) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                if (response.code() == 200) {
                    List<HistoryBooking> bookingList = response.body();

                    if (bookingList == null || bookingList.isEmpty()) {
                        recyclerView.setVisibility(View.GONE);
                        noBookingTextView.setVisibility(View.VISIBLE);
                        return;
                    }

                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    recyclerView.setHasFixedSize(true); // Performance optimization
                    recyclerView.setAdapter(new HistoryAdapter(bookingList, context,new HistoryAdapter.OnItemClickListener() {
                        @Override
                        public void onViewClick(HistoryBooking booking) {
                            JobViewDialog jobModal = new JobViewDialog(context);
                            jobModal.JobViewForFutureAndHistory(booking.getBookingId());
                        }

                        @Override
                        public void onStartClick(HistoryBooking booking) {

                        }
                    }));
                } else {
                    customToast.showCustomErrorToast("No Internet Connection");
                }
            }

            @Override
            public void onFailure(Call<List<HistoryBooking>> call, Throwable t) {
                customToast.showCustomErrorToast("No Internet Connection");
            }
        });
    }
}
