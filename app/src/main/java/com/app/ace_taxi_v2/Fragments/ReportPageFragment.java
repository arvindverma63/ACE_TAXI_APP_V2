package com.app.ace_taxi_v2.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.ace_taxi_v2.Activity.HomeActivity;
import com.app.ace_taxi_v2.Activity.LoginActivity;
import com.app.ace_taxi_v2.Logic.SessionManager;
import com.app.ace_taxi_v2.R;
import com.google.android.material.appbar.MaterialToolbar;


public class ReportPageFragment extends Fragment {

    CardView earning_report,statement_report;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_report_page, container, false);
        SessionManager sessionManager = new SessionManager(getContext());
        if(!sessionManager.isLoggedIn()){
            Intent intent = new Intent(getContext(), LoginActivity.class);
            startActivity(intent);
        }

        earning_report = view.findViewById(R.id.earning_report);
        earning_report.setOnClickListener(view1 -> {
            earningRerport(view1);
        });
        statement_report = view.findViewById(R.id.statement_report);
        statement_report.setOnClickListener(view1 -> {
            statementReport(view1);
        });

        // Initialize the toolbar
        MaterialToolbar toolbar = view.findViewById(R.id.header_toolbar);

        // Handle toolbar back button click
        toolbar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(getContext(), HomeActivity.class);
            startActivity(intent);
        });
        return view;
    }

    public void earningRerport(View view){
        Fragment selectedFragment = new ReportFragment();
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container,selectedFragment);
        fragmentTransaction.commit();
    }
    public void statementReport(View view){
        Fragment selectedFragment = new StatementReportFragment();
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container,selectedFragment);
        fragmentTransaction.commit();
    }

}