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
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.app.ace_taxi_v2.Activity.LoginActivity;
import com.app.ace_taxi_v2.Components.BookingStartStatus;
import com.app.ace_taxi_v2.Components.ShiftChangeModal;
import com.app.ace_taxi_v2.Fragments.HomeFragmentHelpers.ActivitiesHelper;
import com.app.ace_taxi_v2.Fragments.HomeFragmentHelpers.DashboardHelper;
import com.app.ace_taxi_v2.Fragments.HomeFragmentHelpers.ApplicationsHelper;
import com.app.ace_taxi_v2.Fragments.HomeFragmentHelpers.ProfileHelper;
import com.app.ace_taxi_v2.Fragments.HomeFragmentHelpers.ReportsHelper;
import com.app.ace_taxi_v2.JobModals.JobModal;
import com.app.ace_taxi_v2.JobModals.JobViewDialog;
import com.app.ace_taxi_v2.Logic.LoginManager;
import com.app.ace_taxi_v2.Logic.Service.LocationPermissions;
import com.app.ace_taxi_v2.Logic.SessionManager;
import com.app.ace_taxi_v2.Logic.dashboard.CurrentBooking;
import com.app.ace_taxi_v2.Logic.dashboard.DashboardTotalApi;
import com.app.ace_taxi_v2.Models.Dashtotal;
import com.app.ace_taxi_v2.Models.Jobs.TodayBooking;
import com.app.ace_taxi_v2.Models.UserProfileResponse;
import com.app.ace_taxi_v2.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.materialswitch.MaterialSwitch;

import java.util.List;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private static final String PREFS_NAME = "AppPrefs";
    private static final String SWITCH_STATE_KEY = "switch_state";

    private MaterialButton viewBtn;
    public ImageView sideMenu;
    private MaterialSwitch locationSwitch;
    private TextView onlineStatusLabel,set_job_status,payment_status,scope_text;
    private LocationPermissions locationPermissions;
    private TextView pickup_address,driver_name, destination_address, pickup_subaddress, destination_subaddress, date, price, passenger_count, passenger_name;
    private MaterialCardView current_job_card,userProfile;
    private MaterialCardView activeJobStatus,location_card,add_expenses;
    private LinearLayout notification_btn,report_btn,logout_btn;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        try {
            // Initialize UI components
            pickup_address = view.findViewById(R.id.pickup_address);
            destination_address = view.findViewById(R.id.destination_address);
            pickup_subaddress = view.findViewById(R.id.pickup_subaddress);
            destination_subaddress = view.findViewById(R.id.destination_subaddress);
            date = view.findViewById(R.id.current_date);
            set_job_status = view.findViewById(R.id.set_job_status);
            price = view.findViewById(R.id.current_price);
            passenger_count = view.findViewById(R.id.passengers);
            driver_name = view.findViewById(R.id.driver_name);
            passenger_name = view.findViewById(R.id.passenger_name);
            current_job_card = view.findViewById(R.id.current_job_card);
            activeJobStatus = view.findViewById(R.id.activeJobStatus);
            location_card = view.findViewById(R.id.location_card);
            viewBtn = view.findViewById(R.id.view_btn);
            add_expenses = view.findViewById(R.id.add_expenses);
            userProfile = view.findViewById(R.id.user_profile);
            sideMenu = view.findViewById(R.id.sideMenu);
            notification_btn = view.findViewById(R.id.notification_btn);
            report_btn = view.findViewById(R.id.report_btn);
            logout_btn = view.findViewById(R.id.logout_btn);
            payment_status = view.findViewById(R.id.payment_status);
            scope_text = view.findViewById(R.id.scope_text);

            if (getActivity() == null) return view;
            SessionManager sessionManager = new SessionManager(getActivity());
            if (!sessionManager.isLoggedIn()) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
                return view;
            }

            getCurrentBooking(); // Load booking details
            updateDash();

            locationSwitch = view.findViewById(R.id.online_toggle);
            onlineStatusLabel = view.findViewById(R.id.online_status_label);

            // Initialize LocationPermissions instance
            locationPermissions = new LocationPermissions(getActivity(), locationSwitch, onlineStatusLabel);
            if (!isLocationEnabled()) {
                locationPermissions.promptEnableGPS();
            }

            // Retrieve switch state from SharedPreferences, default to true
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            boolean switchState = sharedPreferences.getBoolean(SWITCH_STATE_KEY, true); // Default is true
            locationSwitch.setChecked(switchState);
            updateStatusLabel(switchState);

            // If switch is enabled by default, start location service
            if (switchState) {
                locationPermissions.startLocationService();
            }

            // Set up the Switch listener
            locationSwitch.setOnCheckedChangeListener((compoundButton, isChecked) -> {
                saveSwitchState(isChecked);
                handleSwitchToggle(isChecked);
            });
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreateView", e);
        }

        return view;
    }

    private void saveSwitchState(boolean state) {
        try {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(SWITCH_STATE_KEY, state);
            editor.apply();
        } catch (Exception e) {
            Log.e(TAG, "Error saving switch state", e);
        }
    }

    private void handleSwitchToggle(boolean isChecked) {
        try {
            if (isChecked) {
                if (locationPermissions.isLocationEnabled()) {
                    if (locationPermissions.checkLocationPermissions() && locationPermissions.checkBatteryOptimizations()) {
                        locationPermissions.startLocationService();
                        locationPermissions.setSwitchState(true);
                        updateStatusLabel(true);
                    } else {
                        locationPermissions.requestLocationPermissions();
                        locationPermissions.setSwitchState(false);
                    }
                } else {
                    Log.e(TAG, "Location services (GPS) are disabled.");
                    locationPermissions.promptEnableGPS();
                    locationPermissions.setSwitchState(false);
                }
            } else {
                locationPermissions.setSwitchState(false);
                locationPermissions.stopLocationService();
                updateStatusLabel(false);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in handleSwitchToggle", e);
        }
    }

    private void updateStatusLabel(boolean isOnline) {
        try {
            // Update the status label text
            onlineStatusLabel.setText(isOnline ? "Send Location On" : "Send Location Off");

            // Define colorsMeshes
            int trackColor = ContextCompat.getColor(getContext(), isOnline ? R.color.green : R.color.light_gray);
            int thumbColor = ContextCompat.getColor(getContext(), isOnline ? R.color.white : R.color.gray);
            int iconColor = ContextCompat.getColor(getContext(), isOnline ? R.color.green : R.color.red);

            location_card.setBackgroundTintList(ContextCompat.getColorStateList(getContext(),isOnline ? R.color.green : R.color.red));

            // Apply colors to switch and icon
            locationSwitch.setTrackTintList(ColorStateList.valueOf(trackColor));
            locationSwitch.setThumbTintList(ColorStateList.valueOf(thumbColor));
        } catch (Exception e) {
            Log.e(TAG, "Error in updateStatusLabel", e);
        }
    }

    public void getCurrentBooking() {
        try {
            BookingStartStatus bookingStartStatus = new BookingStartStatus(getContext());
            int bookingId = 0; // Default to -1 if no booking exists

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
            int finalBookingId1 = bookingId;
            currentBooking.getCurrentBooking(new CurrentBooking.CurrentJobCallback() {
                @Override
                public void onSuccess(List<TodayBooking> list) {
                    try {
                        for (TodayBooking booking : list) {
                            String status = booking.getStatus();
                            if ("1".equals(status) && booking.getBookingId() == finalBookingId) {
                                String pickup = booking.getPickupAddress();
                                String[] pickupParts = pickup != null ? pickup.split(",") : new String[]{""};
                                String firstPickup = pickupParts.length > 0 ? pickupParts[0].trim() : "";
                                String lastPickup = pickupParts.length > 1 ? pickupParts[1].trim() + booking.getPickupPostCode() : booking.getPickupPostCode();

                                String destination = booking.getDestinationAddress();
                                String[] destinationParts = destination != null ? destination.split(",") : new String[]{""};
                                String firstDestination = destinationParts.length > 0 ? destinationParts[0].trim() : "";
                                String lastDestination = destinationParts.length > 1 ? destinationParts[1].trim() + booking.getDestinationPostCode() : booking.getDestinationPostCode();

                                pickup_address.setText(firstPickup);
                                pickup_subaddress.setText(lastPickup);
                                destination_address.setText(firstDestination);
                                destination_subaddress.setText(lastDestination);
                                price.setText("£" + booking.getPrice());
                                date.setText(booking.getPickupDateTime());
                                passenger_count.setText(String.valueOf(booking.getPassengers())+" Passengers");
                                passenger_name.setText(booking.getPassengerName());
                                payment_status.setText(booking.getPaymentStatusText());
                                scope_text.setText(booking.getScopeText());

                                try {
                                    if("Account".equals(booking.getScopeText())){
                                        price.setText("ACC");
                                        price.setTextColor(ContextCompat.getColor(getContext(),R.color.red));
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                current_job_card.setOnClickListener(v -> {
                                    JobViewDialog jobModal = new JobViewDialog(getContext());
                                    jobModal.JobViewForTodayJob(finalBookingId1);
                                });
                                viewBtn.setOnClickListener(view -> {
                                    JobViewDialog jobModal = new JobViewDialog(getContext());
                                    jobModal.JobViewForTodayJob(finalBookingId1);
                                });
                                Log.e("current Job card vissble ","visiable");
                                current_job_card.setVisibility(getView().VISIBLE);
                                set_job_status.setText("Active Booking");
                                viewBtn.setVisibility(getView().VISIBLE);
                                activeJobStatus.setVisibility(getView().GONE);
                            }
                        }
                    } catch (Exception e) {
                        Log.e("CurrentBooking", "Error processing booking list", e);
                    }
                }

                @Override
                public void onError(String error) {
                    Log.e("CurrentBooking", "Error: " + error);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error in getCurrentBooking", e);
        }
    }

    private final Handler handler = new Handler();
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                getCurrentBooking();
                handler.postDelayed(this, 3000); // Repeat every 3 seconds
            } catch (Exception e) {
                Log.e(TAG, "Error in repeating task", e);
            }
        }
    };

    // Start the repeated task
    private void startRepeatingTask() {
        try {
            handler.postDelayed(runnable, 3000);
        } catch (Exception e) {
            Log.e(TAG, "Error starting repeating task", e);
        }
    }

    // Stop the repeated task (when needed)
    private void stopRepeatingTask() {
        try {
            handler.removeCallbacks(runnable);
        } catch (Exception e) {
            Log.e(TAG, "Error stopping repeating task", e);
        }
    }

    public boolean isLocationEnabled() {
        try {
            if (getContext() == null) {
                Log.e(TAG, "Context is null in isLocationEnabled");
                return false;
            }

            LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
            if (locationManager == null) {
                Log.e(TAG, "LocationManager is null");
                return false;
            }

            boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            return gpsEnabled || networkEnabled;
        } catch (Exception e) {
            Log.e(TAG, "Error checking location status", e);
            return false;
        }
    }

    public void updateDash(){
        try {
            LoginManager loginManager = new LoginManager(getContext());
            loginManager.getProfile(new LoginManager.ProfileCallback() {
                @Override
                public void onSuccess(UserProfileResponse userProfileResponse) {
                    try {
                        driver_name.setText(userProfileResponse.getFullname());
                    } catch (Exception e) {
                        Log.e(TAG, "Error setting driver name", e);
                    }
                }

                @Override
                public void onFailure(String errorMessage) {
                    Log.e(TAG, "Profile fetch failed: " + errorMessage);
                }
            });

            add_expenses.setOnClickListener(view -> {
                replaceFragment(new NewExpense());
            });
            userProfile.setOnClickListener(view -> {
                replaceFragment(new UserProfileFragment());
            });
            notification_btn.setOnClickListener(view -> {
                replaceFragment(new MessageFragment());
            });
            report_btn.setOnClickListener(view -> {
                replaceFragment(new ReportFragment());
            });
            sideMenu.setOnClickListener(view -> {
                replaceFragment(new SettingFragment());
            });
            logout_btn.setOnClickListener(view -> {
                try {
                    SessionManager sessionManager = new SessionManager(getContext());
                    sessionManager.clearSession();
                    Intent intent = new Intent(getContext(),LoginActivity.class);
                    startActivity(intent);
                } catch (Exception e) {
                    Log.e(TAG, "Error during logout", e);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error in updateDash", e);
        }
    }

    private void replaceFragment(Fragment fragment) {
        try {
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        } catch (Exception e) {
            Log.e(TAG, "Error replacing fragment", e);
        }
    }
}