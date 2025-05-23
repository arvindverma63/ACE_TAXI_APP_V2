package com.app.ace_taxi_v2.JobModals;

import static androidx.core.content.ContextCompat.startActivity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.app.ace_taxi_v2.Components.BookingStartStatus;
import com.app.ace_taxi_v2.Components.JobStatusModal;
import com.app.ace_taxi_v2.Fragments.Adapters.JobAdapters.TodayJobAdapter;
import com.app.ace_taxi_v2.Fragments.JobFragment;
import com.app.ace_taxi_v2.Logic.Formater.HHMMFormater;
import com.app.ace_taxi_v2.Logic.GetBookingInfoApi;
import com.app.ace_taxi_v2.Logic.JobApi.TodayJobManager;
import com.app.ace_taxi_v2.Logic.Service.CurrentBookingSession;
import com.app.ace_taxi_v2.Logic.Service.CurrentShiftStatus;
import com.app.ace_taxi_v2.Models.Jobs.GetBookingInfo;
import com.app.ace_taxi_v2.Models.Jobs.TodayBooking;
import com.app.ace_taxi_v2.Models.Jobs.Vias;
import com.app.ace_taxi_v2.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import org.w3c.dom.Text;

import java.lang.ref.WeakReference;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import io.sentry.android.core.internal.threaddump.Line;

public class JobViewDialog {
    private static final String TAG = "JobViewDialog";
    private final WeakReference<Context> contextRef;
    CurrentBookingSession bookingSession;
    BookingStartStatus bookingStatus;
    public JobViewDialog(Context context) {
        this.contextRef = new WeakReference<>(context);
        bookingSession = new CurrentBookingSession(context);
        bookingStatus = new BookingStartStatus(context);
    }

    public void JobViewForTodayJob(int bookingId) {
        showJobView(bookingId, true);
    }

    public void JobViewForFutureAndHistory(int bookingId) {
        showJobView(bookingId, false);
    }

    private void showJobView(int bookingId, boolean showCompleteButton) {
        Context context = contextRef.get();
        if (context == null) return;

        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.job_view, null);

        Dialog dialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setContentView(dialogView);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        dialog.show();

        setupJobView(dialogView, bookingId, dialog, showCompleteButton);
    }

    private void setupJobView(View dialogView, int bookingId, Dialog dialog, boolean showCompleteButton) {

        try{
            Context context = contextRef.get();
            if (context == null) return;

            Log.d(TAG, "booking Id setupJobView : " + bookingId);

            TextView pickupAddress = dialogView.findViewById(R.id.pickup_address);
            TextView destinationAddress = dialogView.findViewById(R.id.destination_address);
            TextView pickupdate = dialogView.findViewById(R.id.pickup_date);
            TextView bookingprice = dialogView.findViewById(R.id.price);
            TextView customerName = dialogView.findViewById(R.id.passenger_name);
            TextView distance_duration = dialogView.findViewById(R.id.distance_duration);
            TextView pickup_subaddress = dialogView.findViewById(R.id.pickup_subaddress);
            TextView destination_subaddress = dialogView.findViewById(R.id.destination_subaddress);
            MaterialButton complete_button = dialogView.findViewById(R.id.complete_button);
            MaterialButton start_button = dialogView.findViewById(R.id.start_button);
            ImageView job_status_change = dialogView.findViewById(R.id.change_status);
            ImageView closeBtn = dialogView.findViewById(R.id.close_btn);
            TextView bookerName = dialogView.findViewById(R.id.bookerName);
            MaterialButton cancel_button = dialogView.findViewById(R.id.cancel_button);
            TextView passenger_count = dialogView.findViewById(R.id.passenger_count);
            TextView pickupTime = dialogView.findViewById(R.id.pickup_time);
            TextView destinationTime = dialogView.findViewById(R.id.destination_time);
            TextView vias_address = dialogView.findViewById(R.id.vias_address);
            TextView vias_code = dialogView.findViewById(R.id.vias_code);
            LinearLayout vias_container = dialogView.findViewById(R.id.vias_container); // Try to find container
            LinearLayout vias_layout = dialogView.findViewById(R.id.vias);
            TextView jobId = dialogView.findViewById(R.id.bookingId);
            MaterialTextView scopeText = dialogView.findViewById(R.id.scope_text);
            MaterialCardView scopeCard = dialogView.findViewById(R.id.scope_card);
            MaterialTextView payment_status = dialogView.findViewById(R.id.payment_status);
            MaterialCardView paymentCard = dialogView.findViewById(R.id.payment_card);
            MaterialCardView asap_card = dialogView.findViewById(R.id.asap_card);
            TextView distance = dialogView.findViewById(R.id.distance);
            MaterialButton callBtn = dialogView.findViewById(R.id.callBtn);
            MaterialButton arrivedBtn = dialogView.findViewById(R.id.arrived_btn);
            TextView notes = dialogView.findViewById(R.id.notes);


            jobId.setText("#"+bookingId);



            closeBtn.setContentDescription("Close dialog");
            closeBtn.setOnClickListener(v ->{
                dialog.dismiss();


            });

            job_status_change.setContentDescription("Change job status");


            CurrentBookingSession bookingSession = new CurrentBookingSession(context);
            if (bookingSession.getBookingId().equals(String.valueOf(bookingId))) {
                start_button.setVisibility(View.GONE);
                cancel_button.setVisibility(View.GONE);
            }else{
                complete_button.setVisibility(View.GONE);
            }

            if (!showCompleteButton) complete_button.setVisibility(View.GONE);

            complete_button.setOnClickListener(v -> {
                dialog.dismiss();
                JobModal jobModal = new JobModal(context);
                jobModal.jobCompleteBooking(bookingId);
            });


            View todayjobView = LayoutInflater.from(context).inflate(R.layout.fragment_today,null);
            SwipeRefreshLayout swipeRefreshLayout = todayjobView.findViewById(R.id.swipeRefreshLayout);
            TextView noBookingTextView = todayjobView.findViewById(R.id.noBookingTextView);
            RecyclerView recyclerView = todayjobView.findViewById(R.id.recycler_view);


            if(!showCompleteButton){
                complete_button.setVisibility(View.GONE);
                start_button.setVisibility(View.GONE);
                cancel_button.setVisibility(View.GONE);
                callBtn.setVisibility(View.GONE);
                arrivedBtn.setVisibility(View.GONE);
            }

            cancel_button.setOnClickListener(view -> {
                dialog.dismiss();
                bookingSession.saveBookingId(bookingId);
                BookingStartStatus bookingStartStatus = new BookingStartStatus(context);
                bookingStartStatus.setBookingId(bookingId+"");
                if (context instanceof FragmentActivity) {
                    FragmentActivity activity = (FragmentActivity) context;
                    FragmentManager fragmentManager = activity.getSupportFragmentManager();

                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.replace(R.id.fragment_container, new JobFragment());
                    transaction.addToBackStack(null);
                    transaction.commit();
                }

            });

            GetBookingInfoApi getBookingInfoApi = new GetBookingInfoApi(context);
            getBookingInfoApi.getInfo(bookingId, new GetBookingInfoApi.BookingCallback() {
                @Override
                public void onSuccess(GetBookingInfo bookingInfo) {
                    pickupAddress.setOnClickListener(v -> openGoogleMaps(bookingInfo.getPickupAddress()));
                    destinationAddress.setOnClickListener(v -> openGoogleMaps(bookingInfo.getDestinationAddress()));
                    distance.setText(bookingInfo.getMileage()+" Miles");
                    if(bookingInfo.isASAP()){
                        asap_card.setVisibility(View.VISIBLE);
                    }
                    pickupdate.setText(bookingInfo.getFormattedDateTime());
                    customerName.setText(bookingInfo.getPassengerName());
                    bookerName.setText(bookingInfo.getFullname());
                    if(bookingInfo.getScopeText().equals("Account")){
                        scopeCard.setBackgroundTintList(ContextCompat.getColorStateList(context,R.color.red));
                        scopeText.setText("ACCOUNT");
                        paymentCard.setBackgroundTintList(ContextCompat.getColorStateList(context,R.color.red));
                        payment_status.setText(bookingInfo.getAccountNumber()+"");
                    }else if(bookingInfo.getScopeText().equals("Card")){
                        if(bookingInfo.getPaymentStatusText().equals("Unpaid")){
                            scopeCard.setBackgroundTintList(ContextCompat.getColorStateList(context,R.color.purple));
                            scopeText.setText("CARD");
                            paymentCard.setBackgroundTintList(ContextCompat.getColorStateList(context,R.color.red));
                            payment_status.setText("UNPAID");
                        }else if(bookingInfo.getPaymentStatusText().equals("Paid")){
                            scopeCard.setBackgroundTintList(ContextCompat.getColorStateList(context,R.color.purple));
                            scopeText.setText("CARD");
                            paymentCard.setBackgroundTintList(ContextCompat.getColorStateList(context,R.color.green));
                            payment_status.setText("PAID");
                        }
                    }else if(bookingInfo.getScopeText().equals("Cash")){
                        paymentCard.setVisibility(View.GONE);
                        scopeCard.setBackgroundTintList(ContextCompat.getColorStateList(context,R.color.green));
                        scopeText.setText("CASH");
                    }else if(bookingInfo.getScopeText().equals("Rank")){
                        paymentCard.setVisibility(View.GONE);
                        scopeCard.setBackgroundTintList(ContextCompat.getColorStateList(context,R.color.purple));
                        scopeText.setText("RANK");
                    }else{
                        bookingprice.setText(NumberFormat.getCurrencyInstance(Locale.UK).format(bookingInfo.getPrice()));
                    }

                    bookingprice.setText(NumberFormat.getCurrencyInstance(Locale.UK).format(bookingInfo.getPrice()));
                    distance_duration.setText(formatDuration(bookingInfo.getDurationMinutes()));

                    HHMMFormater formater = new HHMMFormater();
                    pickupTime.setText(formater.formatDateTime(bookingInfo.getPickupDateTime()));
                    if(bookingInfo.getArriveBy() != null){
                        destinationTime.setText(formater.formatDateTime(bookingInfo.getArriveBy()));
                    }else {
                        destinationTime.setVisibility(View.GONE);
                    }


                    vias_layout.setVisibility(View.GONE); // Hide original vias layout
                    vias_container.removeAllViews(); // Clear previous via views
                    List<Vias> vias = bookingInfo.getVias();
                    if (vias != null && !vias.isEmpty()) {
                        vias_container.setVisibility(View.VISIBLE);
                        LayoutInflater inflater = LayoutInflater.from(context);
                        for (Vias via : vias) {
                            View viaView = inflater.inflate(R.layout.layout_via_item, vias_container, false);
                            TextView viaAddress = viaView.findViewById(R.id.via_address);
                            TextView viaCode = viaView.findViewById(R.id.via_code);

                            String viaAddressText = via.getAddress() != null ? via.getAddress() : "";
                            String viaPostCode = via.getPostCode() != null ? via.getPostCode() : "";
                            String[] viaParts = viaAddressText.split(",");
                            String firstVia = viaParts.length > 0 ? viaParts[0].trim() : "";
                            String lastVia = viaParts.length > 1 ? viaParts[viaParts.length - 1].trim() : "";

                            viaAddress.setText(firstVia);
                            viaAddress.setOnClickListener(v -> openGoogleMaps(viaAddressText));
                            viaCode.setText(lastVia + (viaPostCode.isEmpty() ? "" : " " + viaPostCode));

                            vias_container.addView(viaView);
                        }
                    } else {
                        vias_container.setVisibility(View.GONE);
                    }
                    String pickup = bookingInfo.getPickupAddress() != null ? bookingInfo.getPickupAddress() : "";
                    String[] pickupParts = pickup.split(",");
                    String firstPickup = pickupParts.length > 0 ? pickupParts[0].trim() : "";
                    String lastPickup = pickupParts.length > 1 ? pickupParts[1].trim() : "";
                    if (bookingInfo.getPickupPostCode() != null) {
                        lastPickup += " " + bookingInfo.getPickupPostCode();
                    }
                    pickup_subaddress.setText(lastPickup.isEmpty() ? "N/A" : lastPickup);
                    pickupAddress.setText(firstPickup.isEmpty() ? "N/A" : firstPickup);

                    String destination = bookingInfo.getDestinationAddress() != null ? bookingInfo.getDestinationAddress() : "";
                    String[] destinationParts = destination.split(",");
                    String firstDestination = destinationParts.length > 0 ? destinationParts[0].trim() : "";
                    String lastDestination = destinationParts.length > 1 ? destinationParts[1].trim() : "";
                    if (bookingInfo.getDestinationPostCode() != null) {
                        lastDestination += " " + bookingInfo.getDestinationPostCode();
                    }
                    destination_subaddress.setText(lastDestination.isEmpty() ? "N/A" : lastDestination);
                    destinationAddress.setText(firstDestination.isEmpty() ? "N/A" : firstDestination);
                    passenger_count.setText(bookingInfo.getPassengers() + " Passengers");
                    start_button.setOnClickListener(v -> {
                        dialog.dismiss();
                        startBooking(context,bookingInfo);
                    });

                    job_status_change.setOnClickListener(v -> {
                        if (bookingSession.getBookingId().equals(String.valueOf(bookingInfo.getBookingId()))) {
                            new JobStatusModal(context).openModal(bookingId);
                        }
                    });

                    callBtn.setOnClickListener(v -> {
                        openDial(bookingInfo.getPhoneNumber());
                    });
                    notes.setVisibility(View.VISIBLE);
                    notes.setText(""+bookingInfo.getDetails());
                }

                @Override
                public void onfailer(String error) {

                }


            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void startBooking(Context context, GetBookingInfo job) {  // Change List to single object
        try {


            int activeBookingId = -1;
            String currentBookingId = bookingStatus.getBookingId();

            if (currentBookingId != null && !currentBookingId.isEmpty()) {
                activeBookingId = Integer.parseInt(currentBookingId);
            }


                bookingStatus.setBookingId(String.valueOf(job.getBookingId()));
                bookingSession.saveBookingId(job.getBookingId());


        } catch (Exception e) {
            Log.e(TAG, "Error starting booking", e);
            Toast.makeText(context, "Error starting job", Toast.LENGTH_SHORT).show();
        }
    }

    public String formatDuration(int totalMinutes) {
        int hours = totalMinutes / 60;
        int minutes = totalMinutes % 60;

        StringBuilder result = new StringBuilder();
        if (hours > 0) {
            result.append(hours).append(" Hr");
        }
        if (minutes > 0) {
            if (result.length() > 0) {
                result.append(", ");
            }
            result.append(minutes).append(" min");
        }
        if (result.length() == 0) {
            return "0 min"; // fallback if duration is 0
        }

        return result.toString();
    }

    private void openGoogleMaps(String address) {
        Context context = contextRef.get();
        if (context == null) return;
        if (address == null || address.trim().isEmpty()) {
            Toast.makeText(context, "Invalid address", Toast.LENGTH_SHORT).show();
            return;
        }

        // Encode the address to handle spaces and special characters
        String encodedAddress = Uri.encode(address);
        // Create a geo URI for the address
        String geoUri = "geo:0,0?q=" + encodedAddress;

        // Create an Intent to open Google Maps
        Uri gmmIntentUri = Uri.parse(geoUri);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps"); // Restrict to Google Maps

        // Check if Google Maps is installed
        if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(mapIntent);
        } else {
            // Fallback: Open the address in a browser
            String mapsUrl = "https://www.google.com/maps/search/?api=1&query=" + encodedAddress;
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mapsUrl));
            context.startActivity(browserIntent);
        }
    }
    public void openDial(String phoneNumber) {
        Context context = contextRef.get();
        String phone = "tel:"+phoneNumber;
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse(phone));
        context.startActivity(intent);
    }


}