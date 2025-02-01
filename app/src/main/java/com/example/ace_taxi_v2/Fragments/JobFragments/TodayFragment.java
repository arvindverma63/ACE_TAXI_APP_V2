package com.example.ace_taxi_v2.Fragments.JobFragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ace_taxi_v2.Fragments.Adapters.JobAdapters.TodayJobAdapter;
import com.example.ace_taxi_v2.JobModals.JobModal;
import com.example.ace_taxi_v2.Logic.GetBookingInfoApi;
import com.example.ace_taxi_v2.Logic.JobApi.TodayJobManager;
import com.example.ace_taxi_v2.Models.Jobs.GetBookingInfo;
import com.example.ace_taxi_v2.Models.Jobs.JobItem;
import com.example.ace_taxi_v2.Models.Jobs.TodayBooking;
import com.example.ace_taxi_v2.R;

import java.util.ArrayList;
import java.util.List;

public class TodayFragment extends Fragment {

    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_today, container, false);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recyclar_view);

        TodayJobManager todayJobManager = new TodayJobManager(getContext(), getActivity().getSupportFragmentManager());
        todayJobManager.getTodayJobs(view,recyclerView);


        return view;
    }

}
