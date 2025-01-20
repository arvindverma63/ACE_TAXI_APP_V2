package com.example.ace_taxi_v2.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ace_taxi_v2.Activity.LoginActivity;
import com.example.ace_taxi_v2.Components.CustomDialog;
import com.example.ace_taxi_v2.Logic.SessionManager;
import com.example.ace_taxi_v2.R;

public class ReportFragment extends Fragment {

    private WebView webView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_report, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SessionManager sessionManager = new SessionManager(getContext());
        if(!sessionManager.isLoggedIn()){
            Intent intent = new Intent(getContext(), LoginActivity.class);
            startActivity(intent);
        }
        webView = view.findViewById(R.id.google_pie_chart);
        CustomDialog customDialog = new CustomDialog();
        // Configure WebView settings
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true); // Enable JavaScript
        webSettings.setAllowFileAccess(true);

        // Set a WebViewClient to monitor page loading
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

                customDialog.showProgressDialog(getContext());
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                customDialog.dismissProgressDialog();
            }
        });

        // Load the Google Pie Chart HTML
        webView.loadUrl("file:///android_asset/google_pie_chart.html");

        // Simulate passing data to the chart
        String jsonData = "[['Category', 'Value'], ['Marketing', 60], ['Development', 25], ['Sales', 10], ['Support', 5]]";
        updateChart(jsonData);
    }

    private void updateChart(String jsonData) {
        // Use evaluateJavascript to pass data to the WebView
        webView.evaluateJavascript("javascript:updateChart(" + jsonData + ")", null);
    }
}
