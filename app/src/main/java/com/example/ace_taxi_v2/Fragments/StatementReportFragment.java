package com.example.ace_taxi_v2.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ace_taxi_v2.Fragments.Adapters.StatementAdapter;
import com.example.ace_taxi_v2.Models.Reports.StatementItem;
import com.example.ace_taxi_v2.R;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

public class StatementReportFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_statement_report, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Find RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);

        // Set LayoutManager
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Create sample data
        List<StatementItem> statementList = new ArrayList<>();
        statementList.add(new StatementItem("11442", "25/02/25", "£121.00"));
        statementList.add(new StatementItem("11443", "26/02/25", "£150.00"));
        statementList.add(new StatementItem("11444", "27/02/25", "£200.00"));

        // Set Adapter
        StatementAdapter adapter = new StatementAdapter(requireContext(), statementList);
        recyclerView.setAdapter(adapter);

        MaterialToolbar toolbar = view.findViewById(R.id.header_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment selectedFragment = new ReportPageFragment();
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container,selectedFragment);
                fragmentTransaction.commit();
            }
        });
    }
}
