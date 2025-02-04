package com.example.ace_taxi_v2.Fragments;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ace_taxi_v2.Activity.LoginActivity;
import com.example.ace_taxi_v2.Logic.DateRangeApi;
import com.example.ace_taxi_v2.Logic.SessionManager;
import com.example.ace_taxi_v2.Models.Jobs.DateRangeResponse;
import com.example.ace_taxi_v2.R;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ReportFragment extends Fragment {

    private WebView webView;
    public MaterialButton dateRangeButton, updateGraphButton;
    private Calendar fromDate = Calendar.getInstance();
    private Calendar toDate = Calendar.getInstance();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private boolean webViewLoaded = false;
    private List<DateRangeResponse> bookingList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_report, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SessionManager sessionManager = new SessionManager(getContext());
        if (!sessionManager.isLoggedIn()) {
            Intent intent = new Intent(getContext(), LoginActivity.class);
            startActivity(intent);
        }

        webView = view.findViewById(R.id.google_pie_chart);
        dateRangeButton = view.findViewById(R.id.date_range_button);
        updateGraphButton = view.findViewById(R.id.update_graph_button);

        dateRangeButton.setOnClickListener(v -> showDateRangePicker());
        updateGraphButton.setOnClickListener(v -> fetchAndUpdateChart());

        setupWebView();
    }

    private void setupWebView() {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                Log.d("ReportFragment", "WebView Loaded Successfully.");
                webViewLoaded = true;
                fetchAndUpdateChart(); // Ensure initial chart update
            }
        });

        webView.loadUrl("file:///android_asset/google_pie_chart.html");
    }

    private void fetchAndUpdateChart() {
        if (!webViewLoaded) {
            Log.d("ReportFragment", "WebView not yet loaded, delaying chart update.");
            return;
        }

        String from = formatToISO(fromDate);
        String to = formatToISO(toDate);

        Log.d("ReportFragment", "Fetching data from " + from + " to " + to);

        DateRangeApi dateRangeApi = new DateRangeApi(getContext());
        dateRangeApi.getData(from, to, new DateRangeApi.BookingCallback() {

            @Override
            public void onSuccess(List<DateRangeResponse> list) {
                if (list == null || list.isEmpty()) {
                    Log.d("ReportFragment", "No data received from API.");
                    Toast.makeText(getContext(), "No data available", Toast.LENGTH_SHORT).show();
                    return;
                }

                for (int i = 0; i<list.size();i++){
                    Log.e("list size : ",""+list.get(i).getPassengerName());
                }


                String jsonData = "[['Category', ''],['Online Payments', 50], ['Bank Transfer', 30], ['Cash', 20]]";
                Log.d("ReportFragment", "Updating chart with data: " + jsonData);

                webView.post(() -> webView.evaluateJavascript("javascript:updateChart(" + jsonData + ")", value -> {
                    Log.d("ReportFragment", "Chart updated successfully.");
                }));
            }

            @Override
            public void onFail(String error) {
                Log.e("ReportFragment", "Error fetching data: " + error);
                Toast.makeText(getContext(), "Failed to load data: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void showDateRangePicker() {
        new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
            fromDate.set(year, month, dayOfMonth);

            new DatePickerDialog(getContext(), (view1, year1, month1, dayOfMonth1) -> {
                toDate.set(year1, month1, dayOfMonth1);
                updateDateRangeButtonText();
                fetchAndUpdateChart();
            }, toDate.get(Calendar.YEAR), toDate.get(Calendar.MONTH), toDate.get(Calendar.DAY_OF_MONTH)).show();

        }, fromDate.get(Calendar.YEAR), fromDate.get(Calendar.MONTH), fromDate.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updateDateRangeButtonText() {
        String formattedDateRange = dateFormat.format(fromDate.getTime()) + " – " + dateFormat.format(toDate.getTime());
        dateRangeButton.setText(formattedDateRange);
    }

    public String getSelectedDateRange() {
        return dateFormat.format(fromDate.getTime()) + " - " + dateFormat.format(toDate.getTime());
    }

    /**
     * Convert API response list to JSON format for Google Charts
     */
    private String convertListToJson(List<DateRangeResponse> list) {
        StringBuilder jsonData = new StringBuilder("[['Category', 'Value']");

        for (DateRangeResponse item : list) {
            String category = item.getPassengerName(); // Example field for category
            int value = item.getPaymentStatus(); // Example field for value

            jsonData.append(",['").append(category).append("',").append(value).append("]");
        }

        jsonData.append("]");
        return jsonData.toString();
    }
    private String formatToISO(Calendar calendar) {
        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        isoFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC")); // Set to UTC timezone
        return isoFormat.format(calendar.getTime());
    }

}
