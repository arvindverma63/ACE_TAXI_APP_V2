package com.example.ace_taxi_v2.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ace_taxi_v2.Logic.LoginManager;
import com.example.ace_taxi_v2.Models.UserProfileResponse;
import com.example.ace_taxi_v2.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;

public class UserProfileFragment extends Fragment {

    public TextInputEditText fullname,email,phoneNumber,vehicle_model,vehicle_reg,vehicle_color,fcm;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        fullname = view.findViewById(R.id.fullname);
        email = view.findViewById(R.id.email);
        phoneNumber = view.findViewById(R.id.telephone);
        vehicle_model = view.findViewById(R.id.vehicle_model);
        vehicle_reg = view.findViewById(R.id.vehicle_reg);
        vehicle_color = view.findViewById(R.id.vehicle_color);
        fcm = view.findViewById(R.id.fcm);
        setDetails();

        MaterialToolbar toolbar = view.findViewById(R.id.toolbar_header);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment selectedFragment = new ProfileFragment();
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container,selectedFragment);
                fragmentTransaction.commit();
            }
        });

        return view;
    }

    public void setDetails(){
        LoginManager loginManager = new LoginManager(getContext());
        loginManager.getProfile(new LoginManager.ProfileCallback() {
            @Override
            public void onSuccess(UserProfileResponse userProfileResponse) {
                fullname.setText(userProfileResponse.getFullname());
                email.setText(userProfileResponse.getEmail());
                phoneNumber.setText(userProfileResponse.getTelephone());
                vehicle_model.setText(userProfileResponse.getVehicleModel());
                vehicle_reg.setText(userProfileResponse.getVehicleReg());
                vehicle_color.setText(userProfileResponse.getColorCode());
                fcm.setText(userProfileResponse.getFcm());
            }

            @Override
            public void onFailure(String errorMessage) {

            }
        });
    }
}