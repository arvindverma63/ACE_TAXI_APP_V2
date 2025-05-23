package com.app.ace_taxi_v2.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.app.ace_taxi_v2.Components.HamMenu;
import com.app.ace_taxi_v2.Fragments.AvailablitiyFragmentAction.AvailabilityActionHandler;
import com.app.ace_taxi_v2.Fragments.AvailablitiyFragmentAction.CustomAvailability;
import com.app.ace_taxi_v2.Fragments.AvailablitiyFragmentAction.DateTimeSelector;
import com.app.ace_taxi_v2.Logic.AvailabilitiesApi;
import com.app.ace_taxi_v2.Logic.SessionManager;
import com.app.ace_taxi_v2.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

public class AvailabilityFragment extends Fragment {

    private LinearLayout dateButton;
    private MaterialButton customButton, amSchoolButton, pmSchoolButton, amPmSchoolButton, unavailableButton,cancel_button;
    private TextView dateText;
    private ImageView sideMenu;
    private RecyclerView recyclerView,recycler_view_all_driver;
    private LinearLayout buttonContainer;
    private SessionManager sessionManager;
    private DateTimeSelector dateTimeSelector;
    private AvailabilityActionHandler actionHandler;
    private CustomAvailability customAvailability;
    public ImageView weekPrev, weekNext;
    public MaterialTextView selectedAvailDate;
    public AvailabilitiesApi availabilitiesApi;
    public MaterialCardView customForm;
    public LinearLayout my_avail_layout,all_driver_layout;
    public MaterialCardView my_avail_card,all_driver_card;
    public TextView my_avail_text,all_driver_text,all_driver_selectDate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_availability, container, false);

        // Initialize UI Elements
        dateButton = rootView.findViewById(R.id.date_button);
        customButton = rootView.findViewById(R.id.custom_button);
        amSchoolButton = rootView.findViewById(R.id.am_school_button);
        pmSchoolButton = rootView.findViewById(R.id.pm_school_button);
        amPmSchoolButton = rootView.findViewById(R.id.am_pm_school_button);
        unavailableButton = rootView.findViewById(R.id.unavailable_button);
        dateText = rootView.findViewById(R.id.dateText);
        sideMenu = rootView.findViewById(R.id.sideMenu);
        recyclerView = rootView.findViewById(R.id.recyclar_view);
        buttonContainer = rootView.findViewById(R.id.buttonContainer);
        weekPrev = rootView.findViewById(R.id.week_prev);
        weekNext = rootView.findViewById(R.id.week_next);
        cancel_button = rootView.findViewById(R.id.cancel_button);
        selectedAvailDate = rootView.findViewById(R.id.selectDate);
        customForm = rootView.findViewById(R.id.custom_form);
        my_avail_layout = rootView.findViewById(R.id.my_avail_layout);
        all_driver_layout = rootView.findViewById(R.id.all_driver_layout);
        my_avail_card = rootView.findViewById(R.id.my_avail_card);
        all_driver_card = rootView.findViewById(R.id.all_driver_card);
        my_avail_text = rootView.findViewById(R.id.my_avail_text);
        all_driver_text = rootView.findViewById(R.id.all_driver_text);
        recycler_view_all_driver = rootView.findViewById(R.id.recycler_view_all_driver);
        all_driver_selectDate = rootView.findViewById(R.id.all_driver_selectDate);

        try {
            sessionManager = new SessionManager(requireContext());
            availabilitiesApi = new AvailabilitiesApi(requireContext());
            dateTimeSelector = new DateTimeSelector();
            customAvailability = new CustomAvailability(requireContext());

            // Set the CustomAvailability as the listener
            dateTimeSelector.init(
                    requireContext(),
                    dateText,
                    dateButton,
                    buttonContainer,
                    weekPrev,
                    weekNext,
                    selectedAvailDate,
                    recyclerView,
                    recycler_view_all_driver,
                    all_driver_selectDate,
                    customAvailability // 🔥 Set the listener here
            );

            actionHandler = new AvailabilityActionHandler(requireContext(), sessionManager, recyclerView, dateTimeSelector);
            initCustomAvailability(rootView);
        } catch (IllegalStateException e) {
            Toast.makeText(requireContext(), "Initialization error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return rootView;
        }


        // Set up listeners
        dateButton.setOnClickListener(v -> dateTimeSelector.showDatePicker());
        pmSchoolButton.setOnClickListener(v -> actionHandler.pmSchoolOnly());
        amSchoolButton.setOnClickListener(v -> actionHandler.amSchoolOnly());
        unavailableButton.setOnClickListener(v -> actionHandler.setUnavailable());
        amPmSchoolButton.setOnClickListener(v -> actionHandler.bothOnly());

        sideMenu.setOnClickListener(v -> {
            HamMenu hamMenu = new HamMenu(requireContext(), requireActivity());
            hamMenu.openMenu(sideMenu);
        });

        weekPrev.setOnClickListener(v -> dateTimeSelector.updateWeekPrev());
        weekNext.setOnClickListener(v -> dateTimeSelector.updateWeekNext());

        customButton.setOnClickListener(v -> {
            customForm.setVisibility(customForm.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
        });
        cancel_button.setOnClickListener(v -> customForm.setVisibility(View.GONE));
        tabLayout();
        return rootView;
    }

    private void initCustomAvailability(View view) {
        try {
            customAvailability.initViews(
                    view.findViewById(R.id.from_time_edit_text),
                    view.findViewById(R.id.to_time_edit_text),
                    dateText,
                    view.findViewById(R.id.note_edit_text),
                    view.findViewById(R.id.add_ava),
                    view.findViewById(R.id.add_un),
                    view.findViewById(R.id.unavailable_button),
                    view.findViewById(R.id.give_or_take),
                    recyclerView,
                    sideMenu,
                    sessionManager,
                    dateTimeSelector
            );
//            customAvailability.renderListAva();
        } catch (IllegalStateException e) {
            Toast.makeText(requireContext(), "CustomAvailability initialization error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void tabLayout() {
        my_avail_layout.setVisibility(View.VISIBLE);
        all_driver_layout.setVisibility(View.GONE);
        my_avail_text.setTextColor(getResources().getColor(R.color.white));
        all_driver_text.setTextColor(getResources().getColor(R.color.gray));
        my_avail_card.setCardBackgroundColor(getResources().getColor(R.color.gray));
        all_driver_card.setCardBackgroundColor(getResources().getColor(R.color.white));
        my_avail_card.setOnClickListener(v -> {
            my_avail_layout.setVisibility(View.VISIBLE);
            all_driver_layout.setVisibility(View.GONE);
            my_avail_text.setTextColor(getResources().getColor(R.color.white));
            all_driver_text.setTextColor(getResources().getColor(R.color.gray));
            my_avail_card.setCardBackgroundColor(getResources().getColor(R.color.gray));
            all_driver_card.setCardBackgroundColor(getResources().getColor(R.color.white));
        });
        all_driver_card.setOnClickListener(v -> {
            my_avail_layout.setVisibility(View.GONE);
            all_driver_layout.setVisibility(View.VISIBLE);
            my_avail_text.setTextColor(getResources().getColor(R.color.gray));
            all_driver_text.setTextColor(getResources().getColor(R.color.white));
            all_driver_card.setCardBackgroundColor(getResources().getColor(R.color.gray));
            my_avail_card.setCardBackgroundColor(getResources().getColor(R.color.white));
        });
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Ensure custom form is initially hidden
        customForm.setVisibility(View.GONE);
    }

}