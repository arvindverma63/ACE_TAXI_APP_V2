package com.app.ace_taxi_v2.Fragments.JobFragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.ace_taxi_v2.Fragments.Adapters.JobAdapters.FutureJobAdapter;
import com.app.ace_taxi_v2.Logic.JobApi.FutureJobManager;
import com.app.ace_taxi_v2.R;


public class FutureFragment extends Fragment {

    private RecyclerView recyclerView;
    private FutureJobManager futureJobs;
    private SwipeRefreshLayout swipeRefreshLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_future, container, false);

        recyclerView = view.findViewById(R.id.recyclar_view);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        futureJobs = new FutureJobManager(getContext(), swipeRefreshLayout);

        // Initial load
        futureJobs.fetchJob(view, recyclerView);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            futureJobs.fetchJob(view, recyclerView);
        });

        return view;
    }

}