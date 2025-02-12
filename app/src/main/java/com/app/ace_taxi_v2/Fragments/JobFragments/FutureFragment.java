package com.app.ace_taxi_v2.Fragments.JobFragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.ace_taxi_v2.Fragments.Adapters.JobAdapters.FutureJobAdapter;
import com.app.ace_taxi_v2.Logic.JobApi.FutureJobManager;
import com.app.ace_taxi_v2.Models.Jobs.FutureJobResponse;
import com.app.ace_taxi_v2.R;

import java.util.List;

public class FutureFragment extends Fragment {

    private RecyclerView recyclerView;
    private FutureJobAdapter adapter;
    private List<FutureJobResponse> futureJobs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_future, container, false);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recyclar_view);

        FutureJobManager jobManager = new FutureJobManager(getContext());
        jobManager.fetchJob(view,recyclerView);
        return view;
    }


}