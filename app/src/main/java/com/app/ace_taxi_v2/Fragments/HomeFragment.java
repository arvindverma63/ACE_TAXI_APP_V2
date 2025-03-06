package com.app.ace_taxi_v2.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.app.ace_taxi_v2.Activity.LoginActivity;
import com.app.ace_taxi_v2.Components.BookingStartStatus;
import com.app.ace_taxi_v2.Logic.Service.LocationPermissions;
import com.app.ace_taxi_v2.Logic.SessionManager;
import com.app.ace_taxi_v2.Logic.dashboard.CurrentBooking;
import com.app.ace_taxi_v2.Models.Jobs.TodayBooking;
import com.app.ace_taxi_v2.R;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private static final String PREFS_NAME = "AppPrefs";
    private static final String SWITCH_STATE_KEY = "switch_state";

    private Switch locationSwitch;
    private TextView onlineStatusLabel;
    private LocationPermissions locationPermissions;
    private TextView pickup_address, destination_address, pickup_subaddress, destination_subaddress, date, price, passenger_count, passenger_name;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize UI components
        pickup_address = view.findViewById(R.id.pickup_address);
        destination_address = view.findViewById(R.id.destination_address);
        pickup_subaddress = view.findViewById(R.id.pickup_subaddress);
        destination_subaddress = view.findViewById(R.id.destination_subaddress);
        date = view.findViewById(R.id.current_date);
        price = view.findViewById(R.id.current_price);
        passenger_count = view.findViewById(R.id.current_passenger_count);
        passenger_name = view.findViewById(R.id.passenger_name);

        // Check user session
        if (getActivity() == null) return view;
        SessionManager sessionManager = new SessionManager(getActivity());
        if (!sessionManager.isLoggedIn()) {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
            return view;
        }



        getCurrentBooking(); // Load booking details

//        NotificationJobDialog notificationJobDialog = new NotificationJobDialog(getContext());
//        notificationJobDialog.openNotificationModalForJob();
        // Initialize Switch and TextView
        locationSwitch = view.findViewById(R.id.online_toggle);
        onlineStatusLabel = view.findViewById(R.id.online_status_label);

        // Initialize LocationPermissions instance
        locationPermissions = new LocationPermissions(getActivity(), locationSwitch, onlineStatusLabel);

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


        return view;
    }

    private void saveSwitchState(boolean state) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SWITCH_STATE_KEY, state);
        editor.apply();
    }

    private void handleSwitchToggle(boolean isChecked) {
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
    }

    private void updateStatusLabel(boolean isOnline) {
        onlineStatusLabel.setText(isOnline ? "You Are Online" : "You Are Offline");

        if (isOnline) {
            locationSwitch.setTrackTintList(ColorStateList.valueOf(getResources().getColor(R.color.primaryColor)));
            locationSwitch.setThumbTintList(ColorStateList.valueOf(getResources().getColor(R.color.primaryColor)));
        } else {
            locationSwitch.setTrackTintList(ColorStateList.valueOf(getResources().getColor(R.color.gray))); // Change to your OFF color
            locationSwitch.setThumbTintList(ColorStateList.valueOf(getResources().getColor(R.color.gray)));
        }
    }

    public void getCurrentBooking() {
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
        currentBooking.getCurrentBooking(new CurrentBooking.CurrentJobCallback() {
            @Override
            public void onSuccess(List<TodayBooking> list) {
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
                        passenger_count.setText(String.valueOf(booking.getPassengers()));
                        passenger_name.setText(booking.getPassengerName());
                    }
                }
            }

            @Override
            public void onError(String error) {
                Log.e("CurrentBooking", "Error: " + error);
            }
        });
    }
    private final Handler handler = new Handler();
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            getCurrentBooking();
            handler.postDelayed(this, 3000); // Repeat every 3 seconds
        }
    };

    // Start the repeated task
    private void startRepeatingTask() {
        handler.postDelayed(runnable, 3000);
    }

    // Stop the repeated task (when needed)
    private void stopRepeatingTask() {
        handler.removeCallbacks(runnable);
    }


}
