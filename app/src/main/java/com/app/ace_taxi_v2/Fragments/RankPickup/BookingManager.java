package com.app.ace_taxi_v2.Fragments.RankPickup;

import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.app.ace_taxi_v2.Components.CustomToast;
import com.app.ace_taxi_v2.Fragments.BookingFragment;
import com.app.ace_taxi_v2.Logic.AutoCompleteApi;
import com.app.ace_taxi_v2.Logic.JobApi.CreateBookingApi;
import com.app.ace_taxi_v2.Models.POI.LocalPOIResponse;
import com.app.ace_taxi_v2.R;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class BookingManager {
    private final BookingFragment fragment;
    private final MapAndLocationManager mapAndLocationManager;
    private List<String> destinationSuggestions;
    private ArrayAdapter<String> destinationAdapter;
    private CreateBookingApi createBookingApi;
    private double routeDistance = 0.0;
    public CustomToast customToast;

    public BookingManager(BookingFragment fragment, MapAndLocationManager mapAndLocationManager) {
        this.fragment = fragment;
        this.mapAndLocationManager = mapAndLocationManager;
        this.destinationSuggestions = new ArrayList<>();
        this.createBookingApi = new CreateBookingApi(fragment.requireContext());
        this.customToast = new CustomToast(fragment.getContext());
    }

    public void setupAutoCompleteFields(AutoCompleteTextView destinationLocationInput) {
        destinationAdapter = createArrayAdapter();
        configureAutoCompleteTextView(destinationLocationInput);

        destinationSuggestions.add("Test - 123 Main St");
        destinationAdapter.notifyDataSetChanged();
        Log.d("AutoComplete", "Static suggestions added, count: " + destinationAdapter.getCount());
    }

    private ArrayAdapter<String> createArrayAdapter() {
        return new ArrayAdapter<String>(fragment.requireContext(),
                android.R.layout.simple_spinner_dropdown_item, destinationSuggestions) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView,
                                @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view;
                textView.setTextColor(Color.BLACK);
                textView.setPadding(2, 2, 2, 2);
                return view;
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView,
                                        @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) view;
                textView.setTextColor(Color.BLACK);
                textView.setPadding(2, 2, 2, 2);
                return view;
            }
        };
    }

    private void configureAutoCompleteTextView(AutoCompleteTextView destinationLocationInput) {
        destinationLocationInput.setAdapter(destinationAdapter);
        destinationLocationInput.setThreshold(1);
        destinationLocationInput.setDropDownHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        destinationLocationInput.setDropDownWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        destinationLocationInput.setDropDownBackgroundResource(android.R.color.white);
        destinationLocationInput.setDropDownAnchor(R.id.passengerCard);
        destinationLocationInput.setDropDownVerticalOffset(-destinationLocationInput.getHeight() - 350); // Adjust offset to position above
        destinationLocationInput.addTextChangedListener(new AutoCompleteTextWatcher());
        destinationLocationInput.setOnClickListener(v -> showDestinationDropdown());
        destinationLocationInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) showDestinationDropdown();
        });
        destinationLocationInput.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) showDestinationDropdown();
            return false;
        });
        destinationLocationInput.setOnItemClickListener((parent, view, position, id) -> {
            String selected = destinationAdapter.getItem(position);
            destinationLocationInput.setText(selected);
            destinationLocationInput.setSelection(selected.length());
            Log.d("AutoComplete", "Selected: " + selected);
        });
    }

    public void showDestinationDropdown() {
        if (!destinationSuggestions.isEmpty()) {
            fragment.getDestinationLocationInput().showDropDown();
            Log.d("AutoComplete", "Dropdown triggered, suggestions: " + destinationSuggestions.size());
        }
    }

    private class AutoCompleteTextWatcher implements TextWatcher {
        private final Handler handler = new Handler(Looper.getMainLooper());
        private Runnable searchRunnable;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (searchRunnable != null) {
                handler.removeCallbacks(searchRunnable);
            }
            if (s.length() >= 1) {
                searchRunnable = () -> fetchAutoCompleteSuggestions(s.toString());
                handler.postDelayed(searchRunnable, 300);
            } else {
                fragment.requireActivity().runOnUiThread(() -> {
                    destinationSuggestions.clear();
                    destinationAdapter.notifyDataSetChanged();
                    Log.d("AutoComplete", "Cleared suggestions, count: " + destinationAdapter.getCount());
                });
            }
        }

        @Override
        public void afterTextChanged(Editable s) {}
    }

    private void fetchAutoCompleteSuggestions(String query) {
        Log.d("AutoComplete", "Fetching suggestions for query: " + query);
        AutoCompleteApi autoCompleteApi = new AutoCompleteApi(fragment.requireContext());
        autoCompleteApi.autoCompleteSearch(query, new AutoCompleteApi.AutoCompleteCallback() {
            @Override
            public void onSuccess(List<LocalPOIResponse> response) {
                Log.d("AutoCompleteRaw", "Raw response size: " + response.size());
                for (LocalPOIResponse poi : response) {
                    Log.d("AutoCompleteRaw", "POI: name=" + poi.getName() + ", address=" + poi.getAddress());
                }
                List<String> newSuggestions = new ArrayList<>();
                if (!response.isEmpty()) {
                    LocalPOIResponse poi = response.get(0); // Get the top result
                    String address = poi.getAddress() != null && !poi.getAddress().isEmpty() ? poi.getAddress() : "Unknown";
                    String postcode = poi.getPostcode() != null && !poi.getPostcode().isEmpty() ? poi.getPostcode() : "No address";
                    if (!address.equals("No address")) {
                        newSuggestions.add(address + " - " + postcode);
                    }
                }
                fragment.requireActivity().runOnUiThread(() -> {
                    destinationSuggestions.clear();
                    destinationSuggestions.addAll(newSuggestions);
                    destinationAdapter = createArrayAdapter();
                    fragment.getDestinationLocationInput().setAdapter(destinationAdapter);
                    destinationAdapter.notifyDataSetChanged();
                    if (!newSuggestions.isEmpty()) {
                        fragment.getDestinationLocationInput().showDropDown();
                    }
                });
            }

            @Override
            public void onFail(String error) {
                Log.e("AutoComplete", "Failed to fetch suggestions: " + error);
                fragment.requireActivity().runOnUiThread(() -> {
                    customToast.showCustomErrorToast("Failed to load suggestions");
                });
            }
        });
    }

    public void bookRide(String destinationText, String passengerName) {
        if (mapAndLocationManager.getCurrentPickupLocation() == null) {
//            customToast.showCustomErrorToast("Pickup location not set");
            return;
        }

        if (destinationText.isEmpty()) {
            customToast.showCustomErrorToast("Please enter destination location");
            return;
        }

        if (passengerName.isEmpty()) {
            customToast.showCustomErrorToast("Please enter passenger name");
            return;
        }

        // Parse destinationText into address and postcode
        String[] destinationParts = destinationText.split(" - ");
        if (destinationParts.length != 2) {
            customToast.showCustomErrorToast("Invalid destination format. Please select a valid destination.");
            return;
        }
        String destinationAddress = destinationParts[0].trim();
        String destinationPostCode = destinationParts[1].trim();

        // Update map with route before booking
        mapAndLocationManager.getLocationFromAddress(destinationText, false, new MapAndLocationManager.OnLocationFetchedListener() {
            @Override
            public void onLocationFetched(LatLng latLng, boolean isPickup) {
                if (!isPickup && mapAndLocationManager.getPickupMarker() != null && mapAndLocationManager.getDestinationMarker() != null) {
                    mapAndLocationManager.drawRoute(
                            mapAndLocationManager.getPickupMarker().getPosition(),
                            mapAndLocationManager.getDestinationMarker().getPosition(),
                            new MapAndLocationManager.OnRouteDrawnListener() {
                                @Override
                                public void onRouteDrawn(double distance) {
                                    routeDistance = distance;
                                    double price = calculatePrice(routeDistance);
                                    fragment.getPriceTextView().setText(String.format("£%.2f", price));
                                    proceedWithBooking(destinationAddress, destinationPostCode, passengerName);
                                }

                                @Override
                                public void onRouteDrawFailed() {
                                    // Proceed with booking even if route drawing fails
                                    proceedWithBooking(destinationAddress, destinationPostCode, passengerName);
                                }
                            });
                }
            }

            @Override
            public void onLocationFetchFailed() {
                // Proceed with booking even if geocoding fails
                proceedWithBooking(destinationAddress, destinationPostCode, passengerName);
            }
        });
    }

    private void proceedWithBooking(String destinationAddress, String destinationPostCode, String passengerName) {
        createBookingApi.createNewBooking(destinationAddress, destinationPostCode, passengerName,
                new CreateBookingApi.CreateBookingCallback() {
                    @Override
                    public void onSuccess() {
                        customToast.showCustomToast("Ride booked successfully");

                        // Clear inputs after booking
                        fragment.getDestinationLocationInput().setText("");
                        fragment.getPassengerNameInput().setText("");
                        fragment.getPriceTextView().setText("£0.00");
                        routeDistance = 0.0;

                        // Reset map
                        mapAndLocationManager.resetMap();
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        customToast.showCustomErrorToast("Failed to book ride: " + errorMessage);
                    }
                });
    }

    private double calculatePrice(double distanceInKm) {
        double baseFare = 2.0;
        double perKmRate = 1.5;
        return baseFare + (distanceInKm * perKmRate);
    }
}