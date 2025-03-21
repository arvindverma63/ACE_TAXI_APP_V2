package com.app.ace_taxi_v2.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.ace_taxi_v2.Activity.LoginActivity;
import com.app.ace_taxi_v2.Fragments.Adapters.ViewPagerAdapterJob;
import com.app.ace_taxi_v2.Logic.SessionManager;
import com.app.ace_taxi_v2.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class JobFragment extends Fragment {

    TabLayout tabLayout;
    ViewPager2 viewPager2;
    ViewPagerAdapterJob viewPagerAdapterJob;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_job, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SessionManager sessionManager = new SessionManager(getContext());
        if(!sessionManager.isLoggedIn()){
            Intent intent = new Intent(getContext(), LoginActivity.class);
            startActivity(intent);
        }

        if (tabLayout != null) {
            ViewGroup tabStrip = (ViewGroup) tabLayout.getChildAt(0);
            for (int i = 0; i < tabStrip.getChildCount(); i++) {
                View tabView = tabStrip.getChildAt(i);
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) tabView.getLayoutParams();
                params.setMargins(8, 0, 8, 0); // Adds space between tabs
                tabView.requestLayout();
            }
        }

        tabLayout = view.findViewById(R.id.job_tab_layout);
        viewPager2 = view.findViewById(R.id.pageView);

        // Initialize adapter with the correct context
        viewPagerAdapterJob = new ViewPagerAdapterJob(requireActivity());
        viewPager2.setAdapter(viewPagerAdapterJob);

        // Use TabLayoutMediator to link TabLayout and ViewPager2
        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            // Set tab titles based on position
            switch (position) {
                case 0:
                    tab.setText("Today");
                    break;
                case 1:
                    tab.setText("Future");
                    break;
                case 2:
                    tab.setText("History");
                    break;
            }
        }).attach();
    }
}
