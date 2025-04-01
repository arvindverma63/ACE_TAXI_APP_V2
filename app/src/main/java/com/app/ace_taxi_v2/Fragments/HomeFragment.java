package com.app.ace_taxi_v2.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.app.ace_taxi_v2.Activity.LoginActivity;
import com.app.ace_taxi_v2.Components.BookingStartStatus;
import com.app.ace_taxi_v2.Fragments.HomeFragmentHelpers.ActivitiesHelper;
import com.app.ace_taxi_v2.Fragments.HomeFragmentHelpers.DashboardHelper;
import com.app.ace_taxi_v2.Fragments.HomeFragmentHelpers.ApplicationsHelper;
import com.app.ace_taxi_v2.Fragments.HomeFragmentHelpers.ProfileHelper;
import com.app.ace_taxi_v2.Fragments.HomeFragmentHelpers.ReportsHelper;
import com.app.ace_taxi_v2.JobModals.JobModal;
import com.app.ace_taxi_v2.Logic.Service.LocationPermissions;
import com.app.ace_taxi_v2.Logic.SessionManager;
import com.app.ace_taxi_v2.Logic.dashboard.CurrentBooking;
import com.app.ace_taxi_v2.Logic.dashboard.DashboardTotalApi;
import com.app.ace_taxi_v2.Models.Dashtotal;
import com.app.ace_taxi_v2.Models.Jobs.TodayBooking;
import com.app.ace_taxi_v2.R;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private static final String PREFS_NAME = "AppPrefs";
    private static final String SWITCH_STATE_KEY = "switch_state";

    private Switch locationSwitch;
    private TextView onlineStatusLabel, set_job_status;
    private LocationPermissions locationPermissions;
    private TextView pickup_address, destination_address, pickup_subaddress, destination_subaddress, date, price, passenger_count, passenger_name;
    private MaterialCardView current_job_card;
    private ImageView nav_icon;
    private View header_view;
    private TextView user_email, user_name, today_count, weekly_count, today_earning, weekly_earning;
    private MaterialCardView activeJobStatus;
    private MaterialCardView profile_btn, view_expenses, add_expenses, upload_document, message_btn, phone_btn, settings_btn, jobs_btn, avail_btn, chat_btn, earning_report_btn, statement_report_btn;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize UI components
        initializeViews(view);

        if (getActivity() == null) return view;

        // Setup helpers
        setupHelpers();

        SessionManager sessionManager = new SessionManager(getActivity());
        if (!sessionManager.isLoggedIn()) {
            startActivity(new Intent(getActivity(), LoginActivity.class));
            getActivity().finish();
            return view;
        }

        DashboardHelper dashboardHelper = new DashboardHelper(getContext());
        dashboardHelper.updateMessage(user_name, user_email);

        getCurrentBooking();
        updateDash();
        startRepeatingTask();

        locationSwitch = view.findViewById(R.id.online_toggle);
        onlineStatusLabel = view.findViewById(R.id.online_status_label);

        // Initialize LocationPermissions
        locationPermissions = new LocationPermissions(getActivity(), locationSwitch, onlineStatusLabel);

        // Load saved switch state
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean switchState = sharedPreferences.getBoolean(SWITCH_STATE_KEY, true);
        locationSwitch.setChecked(switchState);
        updateStatusLabel(switchState);

        // Initial permission check
        if (switchState) {
            locationPermissions.ensureAllPermissions();
        }

        // Setup switch listener
        locationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            saveSwitchState(isChecked);
            handleSwitchToggle(isChecked);
        });

        return view;
    }

    private void initializeViews(View view) {
        pickup_address = view.findViewById(R.id.pickup_address);
        destination_address = view.findViewById(R.id.destination_address);
        pickup_subaddress = view.findViewById(R.id.pickup_subaddress);
        destination_subaddress = view.findViewById(R.id.destination_subaddress);
        date = view.findViewById(R.id.current_date);
        price = view.findViewById(R.id.current_price);
        passenger_count = view.findViewById(R.id.current_passenger_count);
        passenger_name = view.findViewById(R.id.passenger_name);
        current_job_card = view.findViewById(R.id.current_job_card);
        set_job_status = view.findViewById(R.id.set_job_status);
        nav_icon = view.findViewById(R.id.nav_icon);
        user_name = view.findViewById(R.id.user_name);
        header_view = view.findViewById(R.id.header_slide);
        user_email = view.findViewById(R.id.user_email);
        activeJobStatus = view.findViewById(R.id.activeJobStatus);
        upload_document = view.findViewById(R.id.upload_document);
        view_expenses = view.findViewById(R.id.view_expenses);
        add_expenses = view.findViewById(R.id.add_expenses);
        profile_btn = view.findViewById(R.id.profile_btn);
        message_btn = view.findViewById(R.id.message_btn);
        phone_btn = view.findViewById(R.id.phone_btn);
        settings_btn = view.findViewById(R.id.settings_btn);
        jobs_btn = view.findViewById(R.id.jobs_btn);
        avail_btn = view.findViewById(R.id.avail_btn);
        chat_btn = view.findViewById(R.id.chat_btn);
        earning_report_btn = view.findViewById(R.id.earning_report_btn);
        statement_report_btn = view.findViewById(R.id.statement_report_btn);
        today_count = view.findViewById(R.id.today_count);
        weekly_count = view.findViewById(R.id.weekly_count);
        today_earning = view.findViewById(R.id.today_earning);
        weekly_earning = view.findViewById(R.id.weekly_earning);
    }

    private void setupHelpers() {
        ProfileHelper profileHelper = new ProfileHelper(getContext(), R.id.fragment_container);
        profileHelper.profileEvent(profile_btn, upload_document, add_expenses, view_expenses);

        ApplicationsHelper applicationsHelper = new ApplicationsHelper(getContext(), R.id.fragment_container);
        applicationsHelper.applicationEvents(message_btn, phone_btn, settings_btn);

        ActivitiesHelper activitiesHelper = new ActivitiesHelper(getContext(), R.id.fragment_container);
        activitiesHelper.activitiesEvent(jobs_btn, avail_btn, chat_btn);

        ReportsHelper reportsHelper = new ReportsHelper(getContext(), R.id.fragment_container);
        reportsHelper.reportEvent(earning_report_btn, statement_report_btn);
    }

    private void saveSwitchState(boolean state) {
        if (getActivity() != null) {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(SWITCH_STATE_KEY, state);
            editor.apply();
        }
    }

    private void handleSwitchToggle(boolean isChecked) {
        if (isChecked) {
            locationPermissions.ensureAllPermissions();
        } else {
            locationPermissions.setSwitchState(false);
            locationPermissions.stopLocationService();
            updateStatusLabel(false);
        }
    }

    private void updateStatusLabel(boolean isOnline) {
        if (onlineStatusLabel != null && nav_icon != null && getContext() != null) {
            onlineStatusLabel.setText(isOnline ? "Send Location ON" : "Send Location OFF");

            if (isOnline) {
                locationSwitch.setTrackTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                locationSwitch.setThumbTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                nav_icon.setImageTintList(ContextCompat.getColorStateList(getContext(),R.color.green));
            } else {
                locationSwitch.setTrackTintList(ColorStateList.valueOf(getResources().getColor(R.color.gray)));
                locationSwitch.setThumbTintList(ColorStateList.valueOf(getResources().getColor(R.color.gray)));
                nav_icon.setImageTintList(ContextCompat.getColorStateList(getContext(),R.color.red));
            }
        }
    }

    public void getCurrentBooking() {
        BookingStartStatus bookingStartStatus = new BookingStartStatus(getContext());
        int bookingId = 0;

        try {
            String bookingIdStr = bookingStartStatus.getBookingId();
            if (bookingIdStr != null && !bookingIdStr.isEmpty()) {
                bookingId = Integer.parseInt(bookingIdStr);
            }
        } catch (NumberFormatException e) {
            Log.e("getCurrentBooking", "Invalid booking ID", e);
        }

        CurrentBooking currentBooking = new CurrentBooking(getContext());
        int finalBookingId = bookingId;
        currentBooking.getCurrentBooking(new CurrentBooking.CurrentJobCallback() {
            @Override
            public void onSuccess(List<TodayBooking> list) {
                if (getView() == null) return;

                boolean hasActiveJob = false;
                for (TodayBooking booking : list) {
                    String status = booking.getStatus();
                    if ("1".equals(status) && booking.getBookingId() == finalBookingId) {
                        updateBookingUI(booking);
                        hasActiveJob = true;
                        break;
                    }
                }

                if (!hasActiveJob) {
                    current_job_card.setVisibility(View.GONE);
                    set_job_status.setText("No Active Job");
                    activeJobStatus.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(String error) {
                Log.e("CurrentBooking", "Error: " + error);
            }
        });
    }

    private void updateBookingUI(TodayBooking booking) {
        String pickup = booking.getPickupAddress();
        String[] pickupParts = pickup != null ? pickup.split(",") : new String[]{""};
        String firstPickup = pickupParts.length > 0 ? pickupParts[0].trim() : "";
        String lastPickup = pickupParts.length > 1 ? pickupParts[1].trim() + " " + booking.getPickupPostCode() : booking.getPickupPostCode();

        String destination = booking.getDestinationAddress();
        String[] destinationParts = destination != null ? destination.split(",") : new String[]{""};
        String firstDestination = destinationParts.length > 0 ? destinationParts[0].trim() : "";
        String lastDestination = destinationParts.length > 1 ? destinationParts[1].trim() + " " + booking.getDestinationPostCode() : booking.getDestinationPostCode();

        pickup_address.setText(firstPickup);
        pickup_subaddress.setText(lastPickup);
        destination_address.setText(firstDestination);
        destination_subaddress.setText(lastDestination);
        price.setText("£" + booking.getPrice());
        date.setText(booking.getPickupDateTime());
        passenger_count.setText(String.valueOf(booking.getPassengers()));
        passenger_name.setText(booking.getPassengerName());

        if ("Account".equals(booking.getScopeText())) {
            price.setText("ACC");
            price.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
        }

        current_job_card.setOnClickListener(v -> {
            JobModal jobModal = new JobModal(getContext());
            jobModal.JobViewForTodayJob(booking.getBookingId());
        });

        current_job_card.setVisibility(View.VISIBLE);
        set_job_status.setText("Active Job");
        activeJobStatus.setVisibility(View.GONE);
    }

    private final Handler handler = new Handler();
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            getCurrentBooking();
            handler.postDelayed(this, 3000);
        }
    };

    private void startRepeatingTask() {
        handler.postDelayed(runnable, 3000);
    }

    private void stopRepeatingTask() {
        handler.removeCallbacks(runnable);
    }

    public boolean isLocationEnabled() {
        if (getContext() == null) {
            Log.e(TAG, "Context is null in isLocationEnabled");
            return false;
        }

        LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null) {
            Log.e(TAG, "LocationManager is null");
            return false;
        }

        try {
            boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            return gpsEnabled || networkEnabled;
        } catch (Exception e) {
            Log.e(TAG, "Error checking location status", e);
            return false;
        }
    }

    public void updateDash() {
        DashboardTotalApi dashboardTotalApi = new DashboardTotalApi(getContext());
        dashboardTotalApi.getData(new DashboardTotalApi.DashCallback() {
            @Override
            public void onSuccess(Dashtotal dashtotal) {
                today_count.setText(String.valueOf(dashtotal.getTotalJobCountToday()));
                today_earning.setText("£" + dashtotal.getEarningsTotalToday());
                weekly_count.setText(String.valueOf(dashtotal.getTotalJobCountWeek()));
                weekly_earning.setText("£" + dashtotal.getEarningsTotalWeek());
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Dashboard update error: " + error);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopRepeatingTask();
    }
}