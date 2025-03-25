package com.app.ace_taxi_v2.Fragments.Adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.app.ace_taxi_v2.Fragments.JobFragments.FutureFragment;
import com.app.ace_taxi_v2.Fragments.JobFragments.HistoryFragment;
import com.app.ace_taxi_v2.Fragments.JobFragments.TodayFragment;

public class ViewPagerAdapterJob extends FragmentStateAdapter {
    private static final int TAB_COUNT = 3;
    private Fragment[] fragments = new Fragment[TAB_COUNT];

    public ViewPagerAdapterJob(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                if (fragments[0] == null) {
                    fragments[0] = new TodayFragment();
                }
                return fragments[0];
            case 1:
                if (fragments[1] == null) {
                    fragments[1] = new FutureFragment();
                }
                return fragments[1];
            case 2:
                if (fragments[2] == null) {
                    fragments[2] = new HistoryFragment();
                }
                return fragments[2];
            default:
                throw new IllegalArgumentException("Invalid position: " + position);
        }
    }

    @Override
    public int getItemCount() {
        return TAB_COUNT;
    }

    // Method to get existing fragment instance
    public Fragment getFragment(int position) {
        if (position < 0 || position >= TAB_COUNT) {
            throw new IllegalArgumentException("Invalid position: " + position);
        }
        // Create fragment if it doesn't exist
        if (fragments[position] == null) {
            createFragment(position);
        }
        return fragments[position];
    }
}