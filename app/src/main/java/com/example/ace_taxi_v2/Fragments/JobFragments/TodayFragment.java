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

import com.example.ace_taxi_v2.Fragments.Adapters.JobAdapters.TodayJobAdapter;
import com.example.ace_taxi_v2.JobModals.JobModal;
import com.example.ace_taxi_v2.Models.Jobs.JobItem;
import com.example.ace_taxi_v2.R;

import java.util.ArrayList;
import java.util.List;

public class TodayFragment extends Fragment {

    private RecyclerView recyclerView;
    private TodayJobAdapter adapter;
    private List<JobItem> jobList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_today, container, false);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recyclar_view);

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setHasFixedSize(true);

        // Initialize job list and adapter
        jobList = getDummyJobs();
        adapter = new TodayJobAdapter(jobList, new TodayJobAdapter.OnItemClickListener() {
            @Override
            public void onViewClick(JobItem job) {
                JobModal jobModal = new JobModal(getContext());
                jobModal.jobDetails();

            }

            @Override
            public void onStartClick(JobItem job) {
                // Handle Start button click
                // Add your logic here, e.g., mark job as started
            }
        });

        recyclerView.setAdapter(adapter);

        return view;
    }

    // Dummy data for testing
    private List<JobItem> getDummyJobs() {
        List<JobItem> jobs = new ArrayList<>();
        jobs.add(new JobItem("09:56", 1, "BASE, SAXON HOUSE", "Lea Village"));
        jobs.add(new JobItem("10:30", 2, "CENTRAL PARK", "Downtown"));
        jobs.add(new JobItem("11:15", 1, "HIGH STREET", "City Center"));
        jobs.add(new JobItem("12:00", 3, "UNIVERSITY AREA", "North Campus"));
        jobs.add(new JobItem("09:56", 1, "BASE, SAXON HOUSE", "Lea Village"));
        jobs.add(new JobItem("10:30", 2, "CENTRAL PARK", "Downtown"));
        jobs.add(new JobItem("11:15", 1, "HIGH STREET", "City Center"));
        jobs.add(new JobItem("12:00", 3, "UNIVERSITY AREA", "North Campus"));
        jobs.add(new JobItem("09:56", 1, "BASE, SAXON HOUSE", "Lea Village"));
        jobs.add(new JobItem("11:15", 1, "HIGH STREET", "City Center"));
        jobs.add(new JobItem("12:00", 3, "UNIVERSITY AREA", "North Campus"));
        jobs.add(new JobItem("09:56", 1, "BASE, SAXON HOUSE", "Lea Village"));
        jobs.add(new JobItem("10:30", 2, "CENTRAL PARK", "Downtown"));
        jobs.add(new JobItem("11:15", 1, "HIGH STREET", "City Center"));
        jobs.add(new JobItem("12:00", 3, "UNIVERSITY AREA", "North Campus"));
        jobs.add(new JobItem("09:56", 1, "BASE, SAXON HOUSE", "Lea Village"));
        return jobs;
    }
}
