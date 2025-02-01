package com.example.ace_taxi_v2.Logic.JobApi;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ace_taxi_v2.ApiService.ApiService;
import com.example.ace_taxi_v2.Components.JobStatusModal;
import com.example.ace_taxi_v2.Components.UpdateButtonText;
import com.example.ace_taxi_v2.Fragments.Adapters.JobAdapters.TodayJobAdapter;
import com.example.ace_taxi_v2.Instance.RetrofitClient;
import com.example.ace_taxi_v2.JobModals.JobModal;
import com.example.ace_taxi_v2.Logic.GetBookingInfoApi;
import com.example.ace_taxi_v2.Logic.SessionManager;
import com.example.ace_taxi_v2.Models.Jobs.GetBookingInfo;
import com.example.ace_taxi_v2.Models.Jobs.TodayBooking;
import com.example.ace_taxi_v2.Models.Jobs.TodayJobResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TodayJobManager {
    private final Context context;
    private FragmentManager fragmentManager;

    public TodayJobManager(Context context,FragmentManager fragmentManager) {
        this.context = context;
        this.fragmentManager = fragmentManager;
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
                            GetBookingInfoApi getBookingInfoApi = new GetBookingInfoApi(context);
                            getBookingInfoApi.getInfo(booking.getBookingId(), new GetBookingInfoApi.BookingCallback() {
                                @Override
                                public void onSuccess(GetBookingInfo bookingInfo) {
                                    JobModal jobModal = new JobModal(context);
                                    if(bookingInfo.getStatus() == null){
                                        jobModal.notJobStartedYetModal();
                                    }
                                    else{
                                        jobModal.JobViewForTodayJob(bookingInfo.getPickupAddress(),bookingInfo.getDestinationAddress()
                                                ,bookingInfo.getPickupDateTime(),bookingInfo.getPassengerName(),bookingInfo.getBookingId(),bookingInfo.getStatus(),bookingInfo.getPrice());
                                    }
                                }

                                @Override
                                public void onfailer(String error) {
                                    Toast.makeText(context,"Server error",Toast.LENGTH_LONG).show();
                                }
                            });
                        }

                        @Override
                        public void onStartClick(TodayBooking booking) {
                            JobStatusModal jobStatusModal = new JobStatusModal(context,fragmentManager);
                            GetBookingInfoApi getBookingInfoApi = new GetBookingInfoApi(context);
                            getBookingInfoApi.getInfo(booking.getBookingId(), new GetBookingInfoApi.BookingCallback() {
                                @Override
                                public void onSuccess(GetBookingInfo bookingInfo) {
                                    UpdateButtonText updateButtonText = new UpdateButtonText(context);
                                    updateButtonText.todayJobButtonStatus(bookingInfo.getStatus());
                                    Log.e("booking status : ",""+bookingInfo.getStatus());
                                    if(bookingInfo.getStatus() == null){
                                        JobModal jobModal = new JobModal(context);
                                        jobModal.jobOfferModalForTodayJob(bookingInfo.getPickupAddress(),bookingInfo.getDestinationAddress(),bookingInfo.getPrice(),
                                                bookingInfo.getPickupDateTime(),bookingInfo.getPassengerName(),bookingInfo.getBookingId());
                                    }
                                    else{
                                        jobStatusModal.openModal(booking.getBookingId());
                                    }
                                }

                                @Override
                                public void onfailer(String error) {

                                }
                            });
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
