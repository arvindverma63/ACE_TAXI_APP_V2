package com.app.ace_taxi_v2.Logic.JobApi;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.app.ace_taxi_v2.ApiService.ApiService;
import com.app.ace_taxi_v2.Components.BookingStartStatus;
import com.app.ace_taxi_v2.Components.JobStatusModal;
import com.app.ace_taxi_v2.Fragments.Adapters.JobAdapters.TodayJobAdapter;
import com.app.ace_taxi_v2.Instance.RetrofitClient;
import com.app.ace_taxi_v2.JobModals.JobModal;
import com.app.ace_taxi_v2.JobModals.JobViewDialog;
import com.app.ace_taxi_v2.Logic.GetBookingInfoApi;
import com.app.ace_taxi_v2.Logic.SessionManager;
import com.app.ace_taxi_v2.Models.Jobs.GetBookingInfo;
import com.app.ace_taxi_v2.Models.Jobs.TodayBooking;
import com.app.ace_taxi_v2.Models.Jobs.TodayJobResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TodayJobManager {
    private final Context context;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView noBookingTextView;

    public TodayJobManager(Context context, SwipeRefreshLayout swipeRefreshLayout,TextView noBookingTextView) {
        this.context = context;
        this.swipeRefreshLayout = swipeRefreshLayout;
        this.noBookingTextView = noBookingTextView;
    }

    public void getTodayJobs(View view, RecyclerView recyclerView) {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(true);
        }

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
        apiService.todayJobs(token).enqueue(new Callback<TodayJobResponse>() {
            @Override
            public void onResponse(Call<TodayJobResponse> call, Response<TodayJobResponse> response) {
                if (swipeRefreshLayout != null) {
                    swipeRefreshLayout.setRefreshing(false);
                }

                if (response.isSuccessful() && response.body() != null) {
                    List<TodayBooking> bookingList = response.body().getBookings();

                    if (bookingList == null || bookingList.isEmpty()) {
                        recyclerView.setVisibility(View.GONE);
                        noBookingTextView.setVisibility(View.VISIBLE);
                        return;
                    }
                    BookingStartStatus bookingStartStatus = new BookingStartStatus(context);
                    int bookingId = 0;

                    try {
                        bookingId = Integer.parseInt(bookingStartStatus.getBookingId());
                    } catch (NumberFormatException e) {
                        bookingId = -1;
                    }
                    boolean bookingExists = false;

                    Log.d("bookingId check start ","booking Id start : "+bookingId);
                    for (TodayBooking booking : bookingList) {
                        if (booking.getBookingId() == bookingId) {
                            bookingExists = true;
                            break;
                        }
                    }
                    Log.d("bookingId check start ","booking Id start : "+bookingId);
                    if (!bookingExists) {
                        bookingStartStatus.clearBookingId();
                        Log.d("bookingId clear start","bookingId clear");
                    }
                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setAdapter(new TodayJobAdapter(context, bookingList, new TodayJobAdapter.OnItemClickListener() {
                        @Override
                        public void onViewClick(TodayBooking booking) {
                            fetchBookingDetails(String.valueOf(booking.getBookingId()));
                        }

                        @Override
                        public void onStartClick(TodayBooking booking) {
                            handleJobStart(booking);
                        }
                    }));

                } else {
                    Toast.makeText(context, "Error fetching jobs: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TodayJobResponse> call, Throwable t) {
                if (swipeRefreshLayout != null) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                Toast.makeText(context, "Failed to fetch jobs. Check internet connection.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchBookingDetails(String bookingId) {
        GetBookingInfoApi getBookingInfoApi = new GetBookingInfoApi(context);
        getBookingInfoApi.getInfo(Integer.parseInt(bookingId), new GetBookingInfoApi.BookingCallback() {
            @Override
            public void onSuccess(GetBookingInfo bookingInfo) {
                JobModal jobModal = new JobModal(context);
                if (bookingInfo.getStatus() == null) {
                    jobModal.notJobStartedYetModal();
                } else {
                    JobViewDialog jobViewDialog = new JobViewDialog(context);
                    Log.d(TAG, "Booking id job View dialog today job manager: " + bookingInfo.getBookingId());
                    jobViewDialog.JobViewForTodayJob(
                            bookingInfo.getBookingId()
                    );
                }
            }

            @Override
            public void onfailer(String error) {
                Toast.makeText(context, "Server error", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void handleJobStart(TodayBooking booking) {
        JobStatusModal jobStatusModal = new JobStatusModal(context);
        GetBookingInfoApi getBookingInfoApi = new GetBookingInfoApi(context);
        getBookingInfoApi.getInfo(booking.getBookingId(), new GetBookingInfoApi.BookingCallback() {
            @Override
            public void onSuccess(GetBookingInfo bookingInfo) {
                Log.e("Booking Status", "Status: " + bookingInfo.getStatus());
                swipeRefreshLayout.setRefreshing(true);
                if (bookingInfo.getStatus() == "0") {
                    JobModal jobModal = new JobModal(context);
                    jobModal.jobOfferModalForTodayJob(
                            booking.getBookingId()
                    );
                } else if ("3".equals(bookingInfo.getStatus())) {
                    Toast.makeText(context, "Job already completed", Toast.LENGTH_SHORT).show();
                } else {
                    jobStatusModal.openModal(booking.getBookingId());
                }
            }

            @Override
            public void onfailer(String error) {
                Toast.makeText(context, "Failed to update job status.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}