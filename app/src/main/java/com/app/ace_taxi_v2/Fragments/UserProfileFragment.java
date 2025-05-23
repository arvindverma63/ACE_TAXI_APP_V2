package com.app.ace_taxi_v2.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.app.ace_taxi_v2.Components.HamMenu;
import com.app.ace_taxi_v2.Logic.LoginManager;
import com.app.ace_taxi_v2.Models.UserProfileResponse;
import com.app.ace_taxi_v2.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class UserProfileFragment extends Fragment {

    public TextInputEditText fullname,email,phoneNumber,vehicle_model,vehicle_reg,vehicle_color;
    public MaterialButton upload_document,view_expenses;
    public ImageView sideMenu;
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
        upload_document = view.findViewById(R.id.upload_document);
        view_expenses = view.findViewById(R.id.view_expenses);
        sideMenu = view.findViewById(R.id.sideMenu);
        setDetails();



        sideMenu.setOnClickListener(v -> {
            HamMenu hamMenu = new HamMenu(getContext(),getActivity());
            hamMenu.openMenu(sideMenu);

        });
        upload_document.setOnClickListener(v -> {
            replaceFragment(new UploadDocumentFragment());
        });
        view_expenses.setOnClickListener(v -> {
            replaceFragment(new ViewExpenses());
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
            }

            @Override
            public void onFailure(String errorMessage) {

            }
        });
    }
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}