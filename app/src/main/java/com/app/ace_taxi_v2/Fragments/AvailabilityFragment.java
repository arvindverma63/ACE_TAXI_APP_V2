package com.app.ace_taxi_v2.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.app.ace_taxi_v2.Components.HamMenu;
import com.app.ace_taxi_v2.Fragments.AvailablitiyFragmentAction.AvailabilityActionHandler;
import com.app.ace_taxi_v2.Fragments.AvailablitiyFragmentAction.DateTimeSelector;
import com.app.ace_taxi_v2.Logic.AvailabilitiesApi;
import com.app.ace_taxi_v2.Logic.SessionManager;
import com.app.ace_taxi_v2.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

public class AvailabilityFragment extends Fragment {

    private LinearLayout dateButton;
    private MaterialButton custom_button, am_school_button, pm_school_button, am_pm_school_button, unavailable_button, view_all;
    private TextView dateText;
    private ImageView sideMenu;
    private RecyclerView recyclerView;
    private LinearLayout buttonContainer;
    private SessionManager sessionManager;
    private DateTimeSelector dateTimeSelector;
    private AvailabilityActionHandler actionHandler;
    public ImageView week_prev,week_next;
    public MaterialTextView selectedAvailDate;
    public AvailabilitiesApi availabilitiesApi;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_availability, container, false);

        // Initialize UI Elements
        dateButton = rootView.findViewById(R.id.date_button);
        custom_button = rootView.findViewById(R.id.custom_button);
        am_school_button = rootView.findViewById(R.id.am_school_button);
        pm_school_button = rootView.findViewById(R.id.pm_school_button);
        am_pm_school_button = rootView.findViewById(R.id.am_pm_school_button);
        unavailable_button = rootView.findViewById(R.id.unavailable_button);
        dateText = rootView.findViewById(R.id.dateText);
        view_all = rootView.findViewById(R.id.view_all);
        sideMenu = rootView.findViewById(R.id.sideMenu);
        recyclerView = rootView.findViewById(R.id.recyclar_view);
        buttonContainer = rootView.findViewById(R.id.buttonContainer);
        week_prev = rootView.findViewById(R.id.week_prev);
        week_next = rootView.findViewById(R.id.week_next);
        selectedAvailDate = rootView.findViewById(R.id.selectDate);

        // Initialize helpers
        try {
            sessionManager = new SessionManager(getContext());
            availabilitiesApi = new AvailabilitiesApi(getContext()); // ✅ Moved up
            dateTimeSelector = new DateTimeSelector(
                    getContext(), dateText, dateButton, buttonContainer,
                    week_prev, week_next, selectedAvailDate
            );
            actionHandler = new AvailabilityActionHandler(getContext(), sessionManager, recyclerView, dateTimeSelector);
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error initializing components: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        // Set up listeners
        dateButton.setOnClickListener(v -> dateTimeSelector.showDatePicker());
        pm_school_button.setOnClickListener(v -> actionHandler.pmSchoolOnly());
        am_school_button.setOnClickListener(v -> actionHandler.amSchoolOnly());
        unavailable_button.setOnClickListener(v -> actionHandler.setUnavailable());
        am_pm_school_button.setOnClickListener(v -> actionHandler.bothOnly());

        sideMenu.setOnClickListener(v -> {
            HamMenu hamMenu = new HamMenu(getContext(), getActivity());
            hamMenu.openMenu(sideMenu);
        });

        week_prev.setOnClickListener(v -> dateTimeSelector.updateWeekPrev());
        week_next.setOnClickListener(v -> dateTimeSelector.updateWeekNext());

        view_all.setOnClickListener(v -> navigateToFragment(new ListAvailabillity(), "List"));
        custom_button.setOnClickListener(v -> navigateToFragment(new CustomerForm(), "Customer Form"));



        return rootView;
    }

    private void navigateToFragment(Fragment fragment, String fragmentName) {
        try {
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commitAllowingStateLoss();
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error navigating to " + fragmentName + ": " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}