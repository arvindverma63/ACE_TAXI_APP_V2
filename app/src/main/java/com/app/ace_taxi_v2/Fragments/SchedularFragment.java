package com.app.ace_taxi_v2.Fragments;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
        View view = inflater.inflate(R.layout.fragment_schedular, container, false);

        webView = view.findViewById(R.id.webview);

        WebSettings webSettings = webView.getSettings();

        sideMenu = view.findViewById(R.id.sideMenu);
        sideMenu.setOnClickListener(v -> {
            HamMenu hamMenu = new HamMenu(getContext(), getActivity());
            hamMenu.openMenu(sideMenu);
        });

        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setSupportZoom(false);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setDisplayZoomControls(false);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setMediaPlaybackRequiresUserGesture(false);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d("WebView", "Navigating to: " + url);
                if (url.startsWith("https://www.google.com/maps") || url.startsWith("geo:")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    intent.setPackage("com.google.android.apps.maps");
                    try {
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        intent.setPackage(null);
                        startActivity(intent);
                    }
                    return true;
                }
                if (url.startsWith("tel:")) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                    try {
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        Log.e("WebView", "Dialer not found: " + e.getMessage());
                    }
                    return true;
                }
                view.loadUrl(url);
                return true;
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                // Handle progress if needed
            }
        });

        SessionManager sessionManager = new SessionManager(getContext());
        String weburl = DeviceMode.getInstance().isLiveMode()
                ? "https://ace-scheduler-driver.vercel.app?token="
                : "https://dev-ace-scheduler-driver.vercel.app?token=";

        webView.loadUrl(weburl + sessionManager.getToken());

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