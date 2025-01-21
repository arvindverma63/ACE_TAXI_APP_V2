package com.example.ace_taxi_v2.Fragments.JobFragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ace_taxi_v2.Fragments.Adapters.JobAdapters.HistoryAdapter;
import com.example.ace_taxi_v2.JobModals.JobModal;
import com.example.ace_taxi_v2.Models.Jobs.HistoryJob;
import com.example.ace_taxi_v2.R;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private HistoryAdapter adapter;
    private List<HistoryJob> historyJobs;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recyclar_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setHasFixedSize(true);

        // Prepare data and adapter
        historyJobs = getDummyHistoryData();
        adapter = new HistoryAdapter(historyJobs, new HistoryAdapter.OnItemClickListener() {
            @Override
            public void onViewClick(HistoryJob job) {
                JobModal jobModal = new JobModal(getContext());
                jobModal.viewJob();
            }

            @Override
            public void onStartClick(HistoryJob job) {
                // Handle Start button click
                // Example: Mark the job as started
            }
        });

        // Set adapter to RecyclerView
        recyclerView.setAdapter(adapter);

        return view;
    }

    private List<HistoryJob> getDummyHistoryData() {
        List<HistoryJob> jobs = new ArrayList<>();
        jobs.add(new HistoryJob("09:30 AM", 2, "BASE, SAXON HOUSE", "Lea Village"));
        jobs.add(new HistoryJob("11:00 AM", 1, "HIGH STREET", "City Center"));
        jobs.add(new HistoryJob("01:30 PM", 3, "UNIVERSITY AREA", "North Campus"));
        jobs.add(new HistoryJob("09:30 AM", 2, "BASE, SAXON HOUSE", "Lea Village"));
        jobs.add(new HistoryJob("11:00 AM", 1, "HIGH STREET", "City Center"));
        jobs.add(new HistoryJob("01:30 PM", 3, "UNIVERSITY AREA", "North Campus"));
        jobs.add(new HistoryJob("09:30 AM", 2, "BASE, SAXON HOUSE", "Lea Village"));
        jobs.add(new HistoryJob("11:00 AM", 1, "HIGH STREET", "City Center"));
        jobs.add(new HistoryJob("01:30 PM", 3, "UNIVERSITY AREA", "North Campus"));
        return jobs;
    }
}
