package com.app.ace_taxi_v2.Activity.HomeActivityHelper;

import android.content.Intent;
import android.content.res.ColorStateList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.app.ace_taxi_v2.Activity.LoginActivity;
import com.app.ace_taxi_v2.Fragments.AvailabilityFragment;
import com.app.ace_taxi_v2.Fragments.HomeFragment;
import com.app.ace_taxi_v2.Fragments.JobFragment;
import com.app.ace_taxi_v2.Fragments.ProfileFragment;
import com.app.ace_taxi_v2.Fragments.ReportPageFragment;
import com.app.ace_taxi_v2.Fragments.SchedularFragment;
import com.app.ace_taxi_v2.Fragments.UserProfileFragment;
import com.app.ace_taxi_v2.Logic.SessionManager;
import com.app.ace_taxi_v2.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.view.MenuItem;

public class NavigationHandler {
    private final AppCompatActivity activity;
    private final BottomNavigationView bottomNavigationView;

    public NavigationHandler(AppCompatActivity activity, BottomNavigationView bottomNavigationView) {
        this.activity = activity;
        this.bottomNavigationView = bottomNavigationView;

        setupNavigation();
        setActiveItemColor(); // Call the color setup method
    }

    private void setupNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            handleNavigationItemSelected(item);
            return true;
        });
    }

    private void handleNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        int itemId = item.getItemId();

        if (itemId == R.id.nav_home) {
            fragment = new HomeFragment();
        } else if (itemId == R.id.nav_job) {
            fragment = new JobFragment();
        } else if (itemId == R.id.nav_availability) {
            fragment = new AvailabilityFragment();
        } else if (itemId == R.id.nav_profile) {
            fragment = new UserProfileFragment();
        }else if(itemId == R.id.nav_shedular){
            fragment = new SchedularFragment();
        }

        if (fragment != null) {
            loadFragment(fragment);
        }
    }

    public void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    public void handleLogout() {
        SessionManager sessionManager = new SessionManager(activity);
        sessionManager.clearSession();
        Intent intent = new Intent(activity, LoginActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }

    public void setSelectedItem(int itemId) {
        bottomNavigationView.setSelectedItemId(itemId);
    }

    private void setActiveItemColor() {
        ColorStateList colorStateList = new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_checked},  // Active state
                        new int[]{-android.R.attr.state_checked}  // Inactive state
                },
                new int[]{
                        activity.getResources().getColor(R.color.red),  // Active color
                        activity.getResources().getColor(R.color.gray)            // Inactive color
                }
        );

        bottomNavigationView.setItemIconTintList(colorStateList);
        bottomNavigationView.setItemTextColor(colorStateList);
    }
}