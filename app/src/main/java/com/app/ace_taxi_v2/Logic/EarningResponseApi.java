package com.app.ace_taxi_v2.Logic;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.ace_taxi_v2.ApiService.ApiService;
import com.app.ace_taxi_v2.Fragments.Adapters.EarningsAdapter;
import com.app.ace_taxi_v2.Instance.RetrofitClient;
import com.app.ace_taxi_v2.Models.EarningResponse;

import java.util.List;

import io.sentry.Sentry;
import io.sentry.protocol.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EarningResponseApi {
    private static final String TAG = "EarningResponseApi";
    private final Context context;
    private final WebView webView;

    public EarningResponseApi(Context context, WebView webView) {
        this.context = context;
        this.webView = webView;
        configureWebView();
    }

    public void getResponse(String from, String to, RecyclerView recyclerView, EarningCallback earningCallback) {
        SessionManager sessionManager = new SessionManager(context);
        String token = sessionManager.getToken();
        int userId = sessionManager.getUserId();

        if (token == null || token.isEmpty()) {
            Toast.makeText(context, "Authentication token missing", Toast.LENGTH_SHORT).show();
            Sentry.captureMessage("EarningResponseApi Error: Missing authentication token for user ID: " + userId);
            earningCallback.onError("Missing authentication token");
            return;
        }

        User sentryUser = new User();
        sentryUser.setId(String.valueOf(userId));
        Sentry.setUser(sentryUser);

        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        Log.d(TAG, "Request from: " + from + " to: " + to);

        apiService.getEarningResponse(token, from, to).enqueue(new Callback<List<EarningResponse>>() {
            @Override
            public void onResponse(Call<List<EarningResponse>> call, Response<List<EarningResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<EarningResponse> earningsList = response.body();
                    Log.d(TAG, "Earnings Report Response: " + earningsList);

                    // Update or set adapter
                    EarningsAdapter adapter = (EarningsAdapter) recyclerView.getAdapter();
                    if (adapter == null) {
                        adapter = new EarningsAdapter(earningsList);
                        recyclerView.setLayoutManager(new LinearLayoutManager(context));
                        recyclerView.setAdapter(adapter);
                    } else {
                        adapter.updateData(earningsList); // Update existing adapter
                    }

                    earningCallback.onSuccess(earningsList);

                    if (earningsList.isEmpty()) {
                        Toast.makeText(context, "No earnings data available for the selected period.", Toast.LENGTH_SHORT).show();
                        Sentry.captureMessage("EarningResponseApi: No earnings data available for user ID: " + userId);
                    } else {
                        // Calculate totals for pie chart
                        double cashTotal = 0.0, epaymentsTotal = 0.0, accountTotal = 0.0;
                        for (EarningResponse earning : earningsList) {
                            cashTotal += earning.getCashTotal();
                            epaymentsTotal += earning.getAccTotal();
                            accountTotal += earning.getRankTotal();
                        }
                        googlePieChart(cashTotal, epaymentsTotal, accountTotal);
                    }
                } else {
                    int statusCode = response.code();
                    String errorMessage = "Earnings API Error: HTTP " + statusCode + " - " + response.message();
                    Log.e(TAG, errorMessage);
                    Sentry.captureMessage(errorMessage);
                    Toast.makeText(context, "Error fetching data. Status Code: " + statusCode, Toast.LENGTH_SHORT).show();
                    earningCallback.onError(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<List<EarningResponse>> call, Throwable t) {
                String failureMessage = "EarningResponse API Call Failed: " + t.getMessage();
                Log.e(TAG, failureMessage, t);
                Sentry.captureException(t);
                Toast.makeText(context, "Failed to fetch data. Please check your internet connection.", Toast.LENGTH_LONG).show();
                earningCallback.onError(failureMessage);
            }
        });
    }

    private void configureWebView() {
        if (webView != null) {
            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setAllowFileAccess(true);

            webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    Log.d(TAG, "Google Pie Chart loaded successfully.");
                }
            });
        } else {
            Log.e(TAG, "WebView is null. Cannot load chart.");
            Sentry.captureMessage("WebView is null. Cannot load chart.");
        }
    }

    public void googlePieChart(double cash, double epayments, double account) {
        if (webView != null) {
            webView.loadUrl("file:///android_asset/google_pie_chart.html");

            webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    Log.d(TAG, "Google Pie Chart loaded successfully.");

                    String jsonData = "[['Category', 'Value'], ['Cash', " + cash + "], ['E-Payments', " + epayments + "], ['Account', " + account + "]]";
                    updateChart(jsonData);
                }
            });
        } else {
            Log.e(TAG, "WebView is null. Cannot update chart.");
            Sentry.captureMessage("WebView is null. Cannot update chart.");
        }
    }

    private void updateChart(String jsonData) {
        if (webView != null) {
            webView.evaluateJavascript("javascript:updateChart(" + jsonData + ")", value ->
                    Log.d(TAG, "Chart updated: " + value)
            );
        } else {
            Log.e(TAG, "WebView is null. Cannot execute JavaScript.");
            Sentry.captureMessage("WebView is null. Cannot execute JavaScript.");
        }
    }

    public interface EarningCallback {
        void onSuccess(List<EarningResponse> responses);
        void onError(String error);
    }
}