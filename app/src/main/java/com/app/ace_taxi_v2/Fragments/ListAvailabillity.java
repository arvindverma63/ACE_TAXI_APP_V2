package com.app.ace_taxi_v2.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.app.ace_taxi_v2.Logic.AvailabilitiesApi;
import com.app.ace_taxi_v2.R;


public class ListAvailabillity extends Fragment {


    public RecyclerView recyclerView;
    private LinearLayout btnback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list_availabillity, container, false);

        recyclerView = view.findViewById(R.id.recyclar_view);
        btnback = view.findViewById(R.id.btnBack);

        btnback.setOnClickListener(v -> {
            Fragment fragment = new AvailabilityFragment();
            FragmentManager fragmentManager = ((FragmentActivity) getContext()).getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

        AvailabilitiesApi availabilitiesApi = new AvailabilitiesApi(getContext());
        availabilitiesApi.getAvailabilities(recyclerView);

        return view;
    }
}