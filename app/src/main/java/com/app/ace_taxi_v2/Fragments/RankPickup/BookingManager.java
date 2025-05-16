package com.app.ace_taxi_v2.Fragments.RankPickup;

import android.annotation.SuppressLint;
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
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.app.ace_taxi_v2.Components.CustomToast;
import com.app.ace_taxi_v2.Fragments.BookingFragment;
import com.app.ace_taxi_v2.Logic.AutoCompleteApi;
import com.app.ace_taxi_v2.Logic.GetBookingPrice;
import com.app.ace_taxi_v2.Logic.JobApi.CreateBookingApi;
import com.app.ace_taxi_v2.Models.POI.LocalPOIResponse;
import com.app.ace_taxi_v2.Models.QuotesResponse;
import com.app.ace_taxi_v2.R;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class BookingManager {
    private final BookingFragment fragment;
    private final MapAndLocationManager mapAndLocationManager;
    private List<String> destinationSuggestions;
    private ArrayAdapter<String> destinationAdapter;
    private CreateBookingApi createBookingApi;
    public String postcode, address;
    public double totalPrice;
    public CustomToast customToast;
    private static final String PICKUP_ADDRESS = "Rank Pickup";
    private static final String PICKUP_POSTCODE = "SP8 4PZ";

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
    }

    private ArrayAdapter<String> createArrayAdapter() {
        return new ArrayAdapter<String>(fragment.requireContext(),
                android.R.layout.simple_spinner_dropdown_item, destinationSuggestions) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view;
                textView.setTextColor(Color.BLACK);
                textView.setPadding(2, 2, 2, 2);
                return view;
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
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
        destinationLocationInput.setDropDownVerticalOffset(-destinationLocationInput.getHeight() - 350);
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

            // Extract postcode from selected suggestion
            String[] selectedParts = selected.split(" - ");
            if (selectedParts.length != 2 || selectedParts[1].trim().isEmpty()) {
                customToast.showCustomErrorToast("Invalid destination selected");
                fragment.getPriceTextView().setText("£0.00");
                fragment.getHead_price().setText("£0.00");
                return;
            }
            postcode = selectedParts[1].trim();
            address = selectedParts[0].trim();

            @SuppressLint({"NewApi", "LocalSuppress"}) String currentDateTime = Instant.now()
                    .atZone(ZoneId.systemDefault())
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));

            GetBookingPrice getBookingPrice = new GetBookingPrice(fragment.getContext());
            Log.d("GetPriceApi", "Input: pickup=" + PICKUP_POSTCODE + ", destination=" + postcode + ", time=" + currentDateTime);
            getBookingPrice.getBookingPrince(PICKUP_POSTCODE, postcode, currentDateTime, 0, new GetBookingPrice.BookingPriceCallback() {
                @Override
                public void onSuccess(QuotesResponse response) {
                    Log.d("GetPriceApi", "Success: price=£" + response.getTotalPrice());
                    fragment.getPriceTextView().setText(String.format("£%.2f", response.getTotalPrice()));
                    fragment.getHead_price().setText(String.format("£%.2f", response.getTotalPrice()));
                    totalPrice = response.getTotalPrice();
                }

                @Override
                public void onError(String error) {
                    Log.e("GetPriceApi", "Error: " + error);
                    customToast.showCustomErrorToast("Failed to fetch price: " + error);
                    fragment.getPriceTextView().setText("£0.00");
                    fragment.getHead_price().setText("£0.00");
                }
            });
        });
    }

    public void showDestinationDropdown() {
        if (!destinationSuggestions.isEmpty()) {
            fragment.getDestinationLocationInput().showDropDown();
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
                    fragment.getPriceTextView().setText("£0.00");
                    fragment.getHead_price().setText("£0.00");
                });
            }
        }

        @Override
        public void afterTextChanged(Editable s) {}
    }

    private void fetchAutoCompleteSuggestions(String query) {
        AutoCompleteApi autoCompleteApi = new AutoCompleteApi(fragment.requireContext());
        autoCompleteApi.autoCompleteSearch(query, new AutoCompleteApi.AutoCompleteCallback() {
            @Override
            public void onSuccess(List<LocalPOIResponse> response) {
                List<String> newSuggestions = new ArrayList<>();
                // Reset address and postcode
                address = null;
                postcode = null;

                // Take only the first valid suggestion
                if (!response.isEmpty()) {
                    LocalPOIResponse poi = response.get(0); // Top result
                    String poiAddress = poi.getAddress() != null && !poi.getAddress().isEmpty() ? poi.getAddress() : null;
                    String poiPostcode = poi.getPostcode() != null && !poi.getPostcode().isEmpty() ? poi.getPostcode() : null;
                    if (poiAddress != null && poiPostcode != null) {
                        newSuggestions.add(poiAddress + " - " + poiPostcode);
                        address = poiAddress;
                        postcode = poiPostcode;
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
                fragment.requireActivity().runOnUiThread(() -> {
                    customToast.showCustomErrorToast("Failed to load suggestions: " + error);
                    destinationSuggestions.clear();
                    destinationAdapter.notifyDataSetChanged();
                });
            }
        });
    }

    public void bookRide(String destinationText, String passengerName) {
        Log.d("BookRide", "Attempting to book ride: destination=" + destinationText + ", passenger=" + passengerName);

        if (destinationText.isEmpty()) {
            customToast.showCustomErrorToast("Please enter destination location");
            Log.e("BookRide", "Destination text is empty");
            return;
        }

        if (passengerName.isEmpty()) {
            customToast.showCustomErrorToast("Please enter passenger name");
            Log.e("BookRide", "Passenger name is empty");
            return;
        }

        String[] destinationParts = destinationText.split(" - ");
        if (destinationParts.length != 2 || destinationParts[0].trim().isEmpty() || destinationParts[1].trim().isEmpty()) {
            customToast.showCustomErrorToast("Invalid destination format. Please select a valid destination.");
            Log.e("BookRide", "Invalid destination format: " + destinationText);
            return;
        }
        String destinationAddress = destinationParts[0].trim();
        String destinationPostCode = destinationParts[1].trim();

        // Proceed directly to booking without map logic
        proceedWithBooking(destinationAddress, destinationPostCode, passengerName);
    }

    private void proceedWithBooking(String destinationAddress, String destinationPostCode, String passengerName) {
        Log.d("BookRide", "Creating booking: destinationAddress=" + destinationAddress + ", destinationPostCode=" + destinationPostCode + ", passengerName=" + passengerName);
        createBookingApi.createNewBooking(destinationAddress, destinationPostCode, passengerName,totalPrice,
                new CreateBookingApi.CreateBookingCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d("BookRide", "Booking successful");
                        customToast.showCustomToast("Ride booked successfully");
                        fragment.getDestinationLocationInput().setText("");
                        fragment.getPassengerNameInput().setText("");
                        fragment.getPriceTextView().setText("£0.00");
                        fragment.getHead_price().setText("£0.00");
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Log.e("BookRide", "Booking failed: " + errorMessage);
                        customToast.showCustomErrorToast("Failed to book ride: " + errorMessage);
                    }
                });
    }
}