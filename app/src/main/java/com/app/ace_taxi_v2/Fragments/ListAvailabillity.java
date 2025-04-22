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
import android.widget.Button;
import android.widget.LinearLayout;

import com.app.ace_taxi_v2.Logic.AvailabilitiesApi;
import com.app.ace_taxi_v2.R;
import com.google.android.material.appbar.MaterialToolbar;


public class ListAvailabillity extends Fragment {


    public RecyclerView recyclerView;
    public Button closeBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list_availabillity, container, false);

        recyclerView = view.findViewById(R.id.recyclar_view);
        MaterialToolbar toolbar = view.findViewById(R.id.toolbar_header);
        toolbar.setNavigationOnClickListener(v -> {
            Fragment selectedFragment = new AvailabilityFragment();
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, selectedFragment);
            fragmentTransaction.commit();
        });

        // Pass 'view' instead of 'getView()'
        AvailabilitiesApi availabilitiesApi = new AvailabilitiesApi(getContext());
        availabilitiesApi.getAvailabilities(recyclerView, view);

        return view;
    }
}