package com.app.ace_taxi_v2.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.app.ace_taxi_v2.Activity.LoginActivity;
import com.app.ace_taxi_v2.Logic.Service.CurrentShiftStatus;
import com.app.ace_taxi_v2.Logic.Service.LocationPermissions;
import com.app.ace_taxi_v2.Logic.SessionManager;
import com.app.ace_taxi_v2.Logic.dashboard.CurrentBooking;
import com.app.ace_taxi_v2.Models.Jobs.TodayBooking;
import com.app.ace_taxi_v2.R;

import java.util.List;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    private Switch locationSwitch;
    private TextView onlineStatusLabel;
    private LocationPermissions locationPermissions;  // LocationPermissions instance
    public TextView pickup_address,destination_address,pickup_subaddress,destination_subaddress,date,price,passenger_count,passenger_name;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

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
        getCurrentBooking();

        // Initialize UI components
        locationSwitch = view.findViewById(R.id.online_toggle);
        onlineStatusLabel = view.findViewById(R.id.online_status_label);

        // Initialize LocationPermissions instance
        locationPermissions = new LocationPermissions(getActivity(), locationSwitch, onlineStatusLabel);

        // Set up the Switch listener
        locationSwitch.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (isChecked) {
                if (locationPermissions.isLocationEnabled()) {
                    if (locationPermissions.checkLocationPermissions() && locationPermissions.checkBatteryOptimizations()) {
                        locationPermissions.startLocationService();
                        locationPermissions.setSwitchState(true);
                        onlineStatusLabel.setText("You Are Online");
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
                onlineStatusLabel.setText("You Are Offline");
            }
        });

        return view;
    }

    public void getCurrentBooking(){
        CurrentBooking currentBooking = new CurrentBooking(getContext());
        currentBooking.getCurrentBooking(new CurrentBooking.CurrentJobCallback() {
            @Override
            public void onSuccess(List<TodayBooking> list) {
                for (int i = 0; i < list.size(); i++) {
                    String status = list.get(i).getStatus(); // Store in variable
                    if ("1".equals(status)) { // Safe way to compare
                        String pickup = list.get(i).getPickupAddress();
                        String[] pickupParts = pickup != null ? pickup.split(",") : new String[]{""};
                        String firstPickup = pickupParts.length > 0 ? pickupParts[0].trim() : "";
                        String lastPickup = pickupParts.length > 1 ? pickupParts[1].trim() + list.get(i).getPickupPostCode() : list.get(i).getPickupPostCode();

                        String destination = list.get(i).getDestinationAddress();
                        String[] destinationParts = destination != null ? destination.split(",") : new String[]{""};
                        String firstDestination = destinationParts.length > 0 ? destinationParts[0].trim() : "";
                        String lastDestination = destinationParts.length > 1 ? destinationParts[1].trim() + list.get(i).getDestinationPostCode() : list.get(i).getDestinationPostCode();

                        pickup_address.setText(firstPickup);
                        pickup_subaddress.setText(lastPickup);
                        destination_address.setText(firstDestination);
                        destination_subaddress.setText(lastDestination);
                        price.setText("£" + list.get(i).getPrice());
                        date.setText(list.get(i).getPickupDateTime());
                        passenger_count.setText("" + list.get(i).getPassengers());
                        passenger_name.setText(list.get(i).getPassengerName());
                    }
                }
            }

            @Override
            public void onError(String error) {
                Log.e("CurrentBooking", "Error: " + error);
            }
        });
    }



}
