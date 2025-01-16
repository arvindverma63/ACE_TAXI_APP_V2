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

import com.example.ace_taxi_v2.Fragments.Adapters.JobAdapters.FutureJobAdapter;
import com.example.ace_taxi_v2.Models.Jobs.FutureJob;
import com.example.ace_taxi_v2.R;

import java.util.ArrayList;
import java.util.List;

public class FutureFragment extends Fragment {

    private RecyclerView recyclerView;
    private FutureJobAdapter adapter;
    private List<FutureJob> futureJobs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_future, container, false);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recyclar_view);

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setHasFixedSize(true);

        // Initialize futureJobs and adapter
        futureJobs = getDummyFutureJobs();
        adapter = new FutureJobAdapter(futureJobs, new FutureJobAdapter.OnItemClickListener() {
            @Override
            public void onViewClick(FutureJob job) {
                // Handle "View" button click
            }

            @Override
            public void onStartClick(FutureJob job) {
                // Handle "Start" button click
            }
        });

        recyclerView.setAdapter(adapter);

        return view;
    }

    // Dummy data for testing
    private List<FutureJob> getDummyFutureJobs() {
        List<FutureJob> jobs = new ArrayList<>();
        jobs.add(new FutureJob("10:00 AM", 1, "BASE, SAXON HOUSE", "Lea Village"));
        jobs.add(new FutureJob("11:30 AM", 2, "CENTRAL PARK", "Downtown"));
        jobs.add(new FutureJob("01:00 PM", 3, "HIGH STREET", "City Center"));
        jobs.add(new FutureJob("10:00 AM", 1, "BASE, SAXON HOUSE", "Lea Village"));
        jobs.add(new FutureJob("11:30 AM", 2, "CENTRAL PARK", "Downtown"));
        jobs.add(new FutureJob("01:00 PM", 3, "HIGH STREET", "City Center"));
        jobs.add(new FutureJob("10:00 AM", 1, "BASE, SAXON HOUSE", "Lea Village"));
        jobs.add(new FutureJob("11:30 AM", 2, "CENTRAL PARK", "Downtown"));
        jobs.add(new FutureJob("01:00 PM", 3, "HIGH STREET", "City Center"));

        return jobs;
    }
}
