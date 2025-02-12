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
    public ViewPagerAdapterJob(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new TodayFragment();
            case 1:
                return new FutureFragment();
            case 2: // Corrected to 2 instead of 3
                return new HistoryFragment();
            default:
                throw new IllegalArgumentException("Invalid position: " + position);
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
