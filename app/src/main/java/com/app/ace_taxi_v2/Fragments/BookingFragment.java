package com.app.ace_taxi_v2.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.app.ace_taxi_v2.Fragments.RankPickup.BookingManager;
import com.app.ace_taxi_v2.Fragments.RankPickup.MapAndLocationManager;
import com.app.ace_taxi_v2.R;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class BookingFragment extends Fragment {

    private MapAndLocationManager mapAndLocationManager;
    private BookingManager bookingManager;
    private AutoCompleteTextView destinationLocationInput,pickupLocationInput;
    private MaterialButton bookButton;
    private TextInputEditText passengerNameInput;
    private TextView priceTextView,head_price;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mapAndLocationManager = new MapAndLocationManager(this);
        bookingManager = new BookingManager(this, mapAndLocationManager);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_booking, container, false);

        initializeUI(view);
        setupMapFragment();
        setupListeners(view);
        mapAndLocationManager.checkLocationPermissions();

        return view;
    }

    private void initializeUI(View view) {
        destinationLocationInput = view.findViewById(R.id.autoCompleteDestination);
        pickupLocationInput = view.findViewById(R.id.autoCompletePickupAddress); // Add this line
        passengerNameInput = view.findViewById(R.id.passengerName);
        priceTextView = view.findViewById(R.id.price);
        bookButton = view.findViewById(R.id.bookButton);
        head_price = view.findViewById(R.id.head_price);

        // Check for null before setting up autocomplete fields
        if (destinationLocationInput == null) {
            Log.e("BookingFragment", "Destination AutoCompleteTextView not found");
            Toast.makeText(requireContext(), "Error: Destination input not found", Toast.LENGTH_LONG).show();
            return;
        }
        if (pickupLocationInput == null) {
            Log.e("BookingFragment", "Pickup AutoCompleteTextView not found");
            Toast.makeText(requireContext(), "Error: Pickup input not found", Toast.LENGTH_LONG).show();
            return;
        }

        bookingManager.setupAutoCompleteFields(destinationLocationInput);
        bookingManager.setupAutoCompleteFields(pickupLocationInput);
    }

    private void setupMapFragment() {
//        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
//                .findFragmentById(R.id.mapFragment);
//        if (mapFragment != null) {
//            mapFragment.getMapAsync(mapAndLocationManager);
//        }
    }

    private void setupListeners(View view) {
        bookButton.setOnClickListener(v -> {
            String destinationText = destinationLocationInput.getText().toString().trim();
            String passengerName = passengerNameInput.getText().toString().trim();
            bookingManager.bookRide(destinationText, passengerName);
        });

        view.findViewById(R.id.testButton).setOnClickListener(v ->
                bookingManager.showDestinationDropdown());

        setupBackButtonHandler(view);
    }

    private void setupBackButtonHandler(View view) {
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                InputMethodManager imm = (InputMethodManager) requireContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm.isActive(destinationLocationInput)) {
                    imm.hideSoftInputFromWindow(destinationLocationInput.getWindowToken(), 0);
                    bookingManager.showDestinationDropdown();
                    return true;
                }
            }
            return false;
        });
    }

    // Getters for child classes
    public AutoCompleteTextView getDestinationLocationInput() {
        return destinationLocationInput;
    }

    public TextInputEditText getPassengerNameInput() {
        return passengerNameInput;
    }

    public TextView getPriceTextView() {
        return priceTextView;
    }

    public MapAndLocationManager getMapAndLocationManager() {
        return mapAndLocationManager;
    }
    public AutoCompleteTextView getPickupLocationInput() {
        return pickupLocationInput;
    }

    public TextView getHead_price() {return head_price;}
}