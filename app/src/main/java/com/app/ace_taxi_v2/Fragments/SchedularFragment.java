package com.app.ace_taxi_v2.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.app.ace_taxi_v2.Components.HamMenu;
import com.app.ace_taxi_v2.Helper.DeviceMode;
import com.app.ace_taxi_v2.Logic.SessionManager;
import com.app.ace_taxi_v2.R;

public class SchedularFragment extends Fragment {

    private WebView webView;
    public ImageView sideMenu;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_schedular, container, false);

        // Initialize WebView
        webView = view.findViewById(R.id.webview);

        // Configure WebView settings
        WebSettings webSettings = webView.getSettings();

        sideMenu = view.findViewById(R.id.sideMenu);
        sideMenu.setOnClickListener(v -> {
            HamMenu hamMenu = new HamMenu(getContext(),getActivity());
            hamMenu.openMenu(sideMenu);
        });
        // Enable JavaScript
        webSettings.setJavaScriptEnabled(true);

        // Enable localStorage and DOM storage
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);

        // Disable zoom
        webSettings.setSupportZoom(false);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setDisplayZoomControls(false);

        // Additional WebView settings
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setMediaPlaybackRequiresUserGesture(false);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);

        // Enable mixed content (for handling both HTTP and HTTPS)
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        // Set WebViewClient to handle page navigation within WebView
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        // Set WebChromeClient for JavaScript dialogs, favicons, titles, and progress
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                // Handle progress changes if needed
            }
        });
        SessionManager sessionManager = new SessionManager(getContext());
        String weburl = "";
        if(DeviceMode.getInstance().isLiveMode()){
            weburl = "https://ace-scheduler-driver.vercel.app?token=";
        }else{
            weburl = "https://dev-ace-scheduler-driver.vercel.app?token=";
        }
        // Load the specified URL
        webView.loadUrl(weburl+sessionManager.getToken());

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        webView.onPause();
        webView.pauseTimers();
    }

    @Override
    public void onResume() {
        super.onResume();
        webView.onResume();
        webView.resumeTimers();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (webView != null) {
            webView.destroy();
            webView = null;
        }
    }
}