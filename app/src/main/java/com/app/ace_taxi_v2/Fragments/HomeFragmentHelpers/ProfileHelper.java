package com.app.ace_taxi_v2.Fragments.HomeFragmentHelpers;

import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.app.ace_taxi_v2.Fragments.NewExpense;
import com.app.ace_taxi_v2.Fragments.ProfileFragment;
import com.app.ace_taxi_v2.Fragments.UploadDocumentFragment;
import com.app.ace_taxi_v2.Fragments.UserProfileFragment;
import com.app.ace_taxi_v2.Fragments.ViewExpenses;
import com.google.android.material.card.MaterialCardView;

public class ProfileHelper {
    private Context context;
    private int containerId; // The ID of the container where fragments will be displayed

    public ProfileHelper(Context context, int containerId) {
        this.context = context;
        this.containerId = containerId;
    }

    public void profileEvent(MaterialCardView profileBtn, MaterialCardView uploadDocument,
                             MaterialCardView addExpenses, MaterialCardView viewExpenses) {
        // Check if context is an Activity and get FragmentManager
        if (!(context instanceof AppCompatActivity)) {
            throw new IllegalArgumentException("Context must be an instance of AppCompatActivity");
        }
        FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();

        // Set click listeners with null checks
        if (profileBtn != null) {
            profileBtn.setOnClickListener(v -> {
                Fragment fragment = new UserProfileFragment();
                replaceFragment(fragmentManager, fragment);
            });
        }

        if (uploadDocument != null) {
            uploadDocument.setOnClickListener(v -> {
                Fragment uploadDocumentFragment = new UploadDocumentFragment();
                replaceFragment(fragmentManager, uploadDocumentFragment);
            });
        }

        if (addExpenses != null) {
            addExpenses.setOnClickListener(v -> {
                Fragment newExpense = new NewExpense();
                replaceFragment(fragmentManager, newExpense);
            });
        }

        if (viewExpenses != null) {
            viewExpenses.setOnClickListener(v -> {
                Fragment viewExpensesFragment = new ViewExpenses();
                replaceFragment(fragmentManager, viewExpensesFragment);
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