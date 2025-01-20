package com.example.ace_taxi_v2.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ace_taxi_v2.Activity.LoginActivity;
import com.example.ace_taxi_v2.Logic.SessionManager;
import com.example.ace_taxi_v2.R;
import com.google.android.material.appbar.MaterialToolbar;

public class ProfileFragment extends Fragment {

    private CardView profileActivityCard;
    private CardView uploadDocumentCard;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        SessionManager sessionManager = new SessionManager(getContext());
        if(!sessionManager.isLoggedIn()){
            Intent intent = new Intent(getContext(), LoginActivity.class);
            startActivity(intent);
        }

        // Initialize CardViews
        profileActivityCard = view.findViewById(R.id.profile_activity);
        uploadDocumentCard = view.findViewById(R.id.upload_document);

        // Set Click Listeners
        profileActivityCard.setOnClickListener(v -> openProfileActivity());
        uploadDocumentCard.setOnClickListener(v -> openUploadDocumentActivity());

        MaterialToolbar toolbar = view.findViewById(R.id.toolbar_header);
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
        return view;
    }

    private void openProfileActivity() {
        Fragment selectedFragment = new UserProfileFragment();
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container,selectedFragment);
        fragmentTransaction.commit();
    }

    private void openUploadDocumentActivity() {
        Fragment selectedFragment = new UploadDocumentFragment();
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container,selectedFragment);
        fragmentTransaction.commit();
    }
}
