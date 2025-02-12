package com.app.ace_taxi_v2.Fragments.JobFragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.ace_taxi_v2.Fragments.Adapters.JobAdapters.HistoryAdapter;
import com.app.ace_taxi_v2.JobModals.JobModal;
import com.app.ace_taxi_v2.Logic.JobApi.HistoryBookingManager;
import com.app.ace_taxi_v2.Models.Jobs.HistoryBooking;
import com.app.ace_taxi_v2.Models.Jobs.HistoryJob;
import com.app.ace_taxi_v2.R;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {

    private RecyclerView recyclerView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recyclar_view);

        HistoryBookingManager historyBookingManager = new HistoryBookingManager(getContext());
        historyBookingManager.getHistoryBookings(view,recyclerView);

        return view;
    }

}
