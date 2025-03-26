package com.app.ace_taxi_v2.Fragments.HomeFragmentHelpers;

import android.content.Context;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.app.ace_taxi_v2.Fragments.AvailabilityFragment;
import com.app.ace_taxi_v2.Fragments.JobFragment;
import com.app.ace_taxi_v2.Fragments.MessageFragment;
import com.app.ace_taxi_v2.Fragments.NewExpense;
import com.google.android.material.card.MaterialCardView;

public class ActivitiesHelper {
    private Context context;
    private int containerId; // The ID of the container where fragments will be displayed

    public ActivitiesHelper(Context context, int containerId) {
        this.context = context;
        this.containerId = containerId;
    }

    public void activitiesEvent(MaterialCardView jobBtn,MaterialCardView availBtn,MaterialCardView chatBtn){
        if (!(context instanceof AppCompatActivity)) {
            throw new IllegalArgumentException("Context must be an instance of AppCompatActivity");
        }
        FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();

        // Set click listeners with null checks
        if (jobBtn != null) {
            jobBtn.setOnClickListener(v -> {
                Fragment fragment = new JobFragment();
                replaceFragment(fragmentManager, fragment);
            });
        }

        if (availBtn != null) {
            availBtn.setOnClickListener(v -> {
                Fragment uploadDocumentFragment = new AvailabilityFragment();
                replaceFragment(fragmentManager, uploadDocumentFragment);
            });
        }

        if (chatBtn != null) {
            chatBtn.setOnClickListener(v -> {
                Fragment fragment = new MessageFragment();
                replaceFragment(fragmentManager,fragment);
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
