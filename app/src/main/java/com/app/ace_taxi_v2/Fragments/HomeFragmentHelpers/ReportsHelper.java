package com.app.ace_taxi_v2.Fragments.HomeFragmentHelpers;

import android.content.Context;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.app.ace_taxi_v2.Fragments.AvailabilityFragment;
import com.app.ace_taxi_v2.Fragments.JobFragment;
import com.app.ace_taxi_v2.Fragments.ReportFragment;
import com.app.ace_taxi_v2.Fragments.StatementReportFragment;
import com.google.android.material.card.MaterialCardView;

public class ReportsHelper {
    private Context context;
    private int containerId; // The ID of the container where fragments will be displayed

    public ReportsHelper(Context context, int containerId) {
        this.context = context;
        this.containerId = containerId;
    }

    public void reportEvent(MaterialCardView earningBtn,MaterialCardView statementBtn){
        if (!(context instanceof AppCompatActivity)) {
            throw new IllegalArgumentException("Context must be an instance of AppCompatActivity");
        }
        FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();

        // Set click listeners with null checks
        if (earningBtn != null) {
            earningBtn.setOnClickListener(v -> {
                Fragment fragment = new ReportFragment();
                replaceFragment(fragmentManager, fragment);
            });
        }

        if (statementBtn != null) {
            statementBtn.setOnClickListener(v -> {
                Fragment uploadDocumentFragment = new StatementReportFragment();
                replaceFragment(fragmentManager, uploadDocumentFragment);
            });
        }
    }
    private void replaceFragment(FragmentManager fragmentManager, Fragment fragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(containerId, fragment);
        transaction.addToBackStack(null); // Optional: adds fragment to back stack
        transaction.commit();
    }
}
