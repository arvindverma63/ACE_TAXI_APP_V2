package com.app.ace_taxi_v2.Fragments.JobFragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.ace_taxi_v2.Fragments.Adapters.JobAdapters.TodayJobAdapter;
import com.app.ace_taxi_v2.JobModals.JobModal;
import com.app.ace_taxi_v2.Logic.GetBookingInfoApi;
import com.app.ace_taxi_v2.Logic.JobApi.TodayJobManager;
import com.app.ace_taxi_v2.Models.Jobs.GetBookingInfo;
import com.app.ace_taxi_v2.Models.Jobs.JobItem;
import com.app.ace_taxi_v2.Models.Jobs.TodayBooking;
import com.app.ace_taxi_v2.R;

import java.util.ArrayList;
import java.util.List;

public class TodayFragment extends Fragment {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TodayJobManager todayJobManager;
    private View fragmentView; // Store the view for later use

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_today, container, false);
        fragmentView = view; // Store the view

        recyclerView = view.findViewById(R.id.recyclar_view);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        todayJobManager = new TodayJobManager(getContext(), getActivity().getSupportFragmentManager(), swipeRefreshLayout);

        // Initial load
        todayJobManager.getTodayJobs(view, recyclerView);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            todayJobManager.getTodayJobs(view, recyclerView);
        });

        return view;
    }

    // Public method to refresh data when switching fragments
    public void refreshData() {
        if (fragmentView != null && recyclerView != null && todayJobManager != null) {
            todayJobManager.getTodayJobs(fragmentView, recyclerView);
        }
    }
}