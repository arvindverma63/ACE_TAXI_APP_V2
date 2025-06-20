package com.app.ace_taxi_v2.Fragments.RankPickup;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
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
import com.app.ace_taxi_v2.Helper.LogHelperLaravel;
import com.app.ace_taxi_v2.JobModals.BookingRequest;
import com.app.ace_taxi_v2.Logic.AddressIO.AddressIOGeocodeToLocation;
import com.app.ace_taxi_v2.Logic.AddressIO.GetAddressIOAddress;
import com.app.ace_taxi_v2.Logic.GetBookingPrice;
import com.app.ace_taxi_v2.Logic.JobApi.CreateBookingApi;
import com.app.ace_taxi_v2.Logic.Service.LocationSessionManager;
import com.app.ace_taxi_v2.Logic.SessionManager;
import com.app.ace_taxi_v2.Models.AddressIO.AddressIOLocationResponse;
import com.app.ace_taxi_v2.Models.AddressIO.Suggestion;
import com.app.ace_taxi_v2.Models.AddressIO.PostcodeResponse;
import com.app.ace_taxi_v2.Models.QuotesResponse;
import com.app.ace_taxi_v2.R;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class BookingManager {
    private final BookingFragment fragment;
    private List<String> destinationSuggestions;
    private ArrayAdapter<String> destinationAdapter;
    private CreateBookingApi createBookingApi;
    public String postcode, address;
    public double totalPrice;
    public CustomToast customToast;
    private static final String PICKUP_ADDRESS = "Rank Pickup";
    private static final String PICKUP_POSTCODE = "SP8 4PZ";
    private boolean isProgrammaticChange = false; // Flag to track programmatic text changes

    public BookingManager(BookingFragment fragment) {
        this.fragment = fragment;
        this.destinationSuggestions = new ArrayList<>();
        this.createBookingApi = new CreateBookingApi(fragment.requireContext());
        this.customToast = new CustomToast(fragment.getContext());
    }

    public void setupAutoCompleteFields(AutoCompleteTextView destinationLocationInput) {
        try {
            destinationAdapter = createArrayAdapter();
            configureAutoCompleteTextView(destinationLocationInput);

            destinationSuggestions.add("Test - 123 Main St");
            destinationAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            LogHelperLaravel.getInstance().e(TAG, "Exception: " + e);
        }
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
                textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                textView.setPadding(2, 2, 2, 2);
                return view;
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) view;
                textView.setTextColor(Color.BLACK);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                textView.setPadding(2, 2, 2, 2);
                return view;
            }
        };
    }

    private void configureAutoCompleteTextView(AutoCompleteTextView destinationLocationInput) {
        try {
            destinationLocationInput.setAdapter(destinationAdapter);
            destinationLocationInput.setThreshold(1);
            destinationLocationInput.setDropDownHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
            destinationLocationInput.setDropDownWidth(ViewGroup.LayoutParams.MATCH_PARENT);
            destinationLocationInput.setDropDownBackgroundResource(android.R.color.white);
            destinationLocationInput.setDropDownAnchor(R.id.passengerCard);
            destinationLocationInput.setDropDownVerticalOffset(-destinationLocationInput.getHeight() - 350);
            destinationLocationInput.addTextChangedListener(new AutoCompleteTextWatcher());
            destinationLocationInput.setOnTouchListener((v, event) -> {
                if (event.getAction() == MotionEvent.ACTION_DOWN && !destinationSuggestions.isEmpty()) {
                    destinationLocationInput.showDropDown();
                }
                return false;
            });
            destinationLocationInput.setOnItemClickListener((parent, view, position, id) -> {
                try {
                    isProgrammaticChange = true; // Set flag before changing text
                    String selected = destinationAdapter.getItem(position);
                    destinationLocationInput.setText(selected);
                    destinationLocationInput.setSelection(selected.length());
                    destinationLocationInput.dismissDropDown(); // Dismiss dropdown
                    isProgrammaticChange = false; // Reset flag after changing text

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
                } catch (Exception e) {
                    LogHelperLaravel.getInstance().e(TAG, "Exception: " + e);
                    isProgrammaticChange = false; // Ensure flag is reset in case of exception
                }
            });
        } catch (Exception e) {
            LogHelperLaravel.getInstance().e(TAG, "exception: " + e);
        }
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
            if (isProgrammaticChange) {
                return; // Skip if text change is programmatic
            }

            if (searchRunnable != null) {
                handler.removeCallbacks(searchRunnable);
            }

            if (s.length() >= 3) { // Only fetch suggestions after 3 characters
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
        GetAddressIOAddress addressIO = new GetAddressIOAddress();
        addressIO.getAddressId(query, new GetAddressIOAddress.AddressCallback() {
            @Override
            public void onSuccess(List<Suggestion> suggestions) {
                List<String> newSuggestions = new ArrayList<>();
                address = null;
                postcode = null;

                final int[] completedRequests = {0};
                final int totalRequests = suggestions.size();

                if (totalRequests == 0) {
                    fragment.requireActivity().runOnUiThread(() -> {
                        destinationSuggestions.clear();
                        destinationAdapter.notifyDataSetChanged();
                    });
                    return;
                }

                for (Suggestion suggestion : suggestions) {
                    addressIO.getPostcode(suggestion.getId(), new GetAddressIOAddress.PostcodeCallback() {
                        @Override
                        public void onSuccess(PostcodeResponse postcodeResponse) {
                            String suggestionAddress = suggestion.getAddress();
                            String suggestionPostcode = postcodeResponse.getPostcode();
                            if (suggestionAddress != null && !suggestionAddress.isEmpty() &&
                                    suggestionPostcode != null && !suggestionPostcode.isEmpty()) {
                                newSuggestions.add(suggestionAddress + " - " + suggestionPostcode);
                            }

                            completedRequests[0]++;
                            if (completedRequests[0] == totalRequests) {
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
                        }

                        @Override
                        public void onError(String error) {
                            completedRequests[0]++;
                            if (completedRequests[0] == totalRequests) {
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
                        }
                    });
                }
            }

            @Override
            public void onError(String error) {
                fragment.requireActivity().runOnUiThread(() -> {
//                    customToast.showCustomErrorToast("Failed to load suggestions: " + error);
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

        proceedWithBooking(destinationAddress, destinationPostCode, passengerName);
    }

    private void proceedWithBooking(String destinationAddress, String destinationPostCode, String passengerName) {
        Log.d("BookRide", "Creating booking: destinationAddress=" + destinationAddress + ", destinationPostCode=" + destinationPostCode + ", passengerName=" + passengerName);

        SessionManager sessionManager = new SessionManager(fragment.getContext());
        LocationSessionManager locationSessionManager = new LocationSessionManager(fragment.getContext());
        AddressIOGeocodeToLocation addressIOGeocodeToLocation = new AddressIOGeocodeToLocation(fragment.getContext());
        LogHelperLaravel.getInstance().d(TAG,"Current Location "+locationSessionManager.getLatitude()+" \n"+locationSessionManager.getLongitude()
        +" \n username "+sessionManager.getUsername()+" userId "+sessionManager.getUserId()
        );

        addressIOGeocodeToLocation.getLocation(
                locationSessionManager.getLatitude(),
                locationSessionManager.getLongitude(),
                new AddressIOGeocodeToLocation.PickupAddressPostcode() {
                    @Override
                    public void onSuccess(String pickupAddress, String pickupPostCode) {
                        // ✅ Got pickup info

                        LogHelperLaravel.getInstance().i(TAG,"pickup address : "+pickupAddress+" postcode "+pickupPostCode);

                        String timestamp = null;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            timestamp = Instant.now().toString();
                        }

                        // ✅ Now get price with pickupPostCode and destinationPostCode
                        GetBookingPrice getBookingPrice = new GetBookingPrice(fragment.getContext());
                        getBookingPrice.getBookingPrince(
                                pickupPostCode,
                                destinationPostCode,
                                timestamp,
                                1,
                                new GetBookingPrice.BookingPriceCallback() {
                                    @Override
                                    public void onSuccess(QuotesResponse response) {
                                        // ✅ Got full quote info: use it in your booking request

                                        SessionManager sessionManager = new SessionManager(fragment.getContext());
                                        int userId = sessionManager.getUserId();

                                        BookingRequest request = new BookingRequest();
                                        request.setPickup(pickupAddress);
                                        request.setPickupPostcode(pickupPostCode);
                                        request.setDestination(destinationAddress);
                                        request.setDestinationPostcode(destinationPostCode);
                                        request.setName(passengerName);
                                        request.setUserid(userId);

                                        // 🚀 Add all pricing info from the quote response
                                        request.setDurationMinutes(response.getTotalMinutes());
                                        request.setMileage(response.getTotalMileage());
                                        request.setMileageText(response.getMileageText());
                                        request.setDurationText(response.getDurationText());
                                        request.setPrice(response.getTotalPrice());

                                        // If needed, you can add extra fields:
                                        // e.g. response.getDeadMileage(), etc.
                                        // If your BookingRequest class supports them!

                                        createBookingApi.createNewBooking(
                                                request,
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
                                                }
                                        );
                                    }

                                    @Override
                                    public void onError(String error) {
                                        Log.e("BookRide", "Get price failed: " + error);
                                        customToast.showCustomErrorToast("Could not get price: " + error);
                                    }
                                }
                        );
                    }

                    @Override
                    public void onError(String error) {
                        Log.e("BookRide", "Failed to get pickup location: " + error);
                        customToast.showCustomErrorToast("Could not get pickup location");
                    }
                }
        );
    }


}