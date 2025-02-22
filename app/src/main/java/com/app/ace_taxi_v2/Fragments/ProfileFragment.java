package com.app.ace_taxi_v2.Fragments;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.app.ace_taxi_v2.Activity.LoginActivity;
import com.app.ace_taxi_v2.Logic.SessionManager;
import com.app.ace_taxi_v2.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ProfileFragment extends Fragment {

    private CardView profileActivityCard, uploadDocumentCard, newExpenses, viewExpenses;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        SessionManager sessionManager = new SessionManager(requireContext());
        if (!sessionManager.isLoggedIn()) {
            Intent intent = new Intent(getContext(), LoginActivity.class);
            startActivity(intent);
            requireActivity().finish();
            return view;
        }

        profileActivityCard = view.findViewById(R.id.profile_activity);
        uploadDocumentCard = view.findViewById(R.id.upload_document);
        newExpenses = view.findViewById(R.id.new_expenses);
        viewExpenses = view.findViewById(R.id.view_expenses);


        profileActivityCard.setOnClickListener(v -> replaceFragment(new UserProfileFragment()));
        uploadDocumentCard.setOnClickListener(v -> replaceFragment(new UploadDocumentFragment()));
        newExpenses.setOnClickListener(v -> replaceFragment(new NewExpense()));
        viewExpenses.setOnClickListener(v -> replaceFragment(new ViewExpenses()));

        MaterialToolbar toolbar = view.findViewById(R.id.toolbar_header);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(view1 -> replaceFragment(new HomeFragment()));
        }


        return view;
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


}
