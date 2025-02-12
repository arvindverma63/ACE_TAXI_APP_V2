package com.app.ace_taxi_v2.Logic;

import android.content.Context;
import android.util.Log;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EarningResponseApi {
    private final Context context;
    private final WebView webView;

    public EarningResponseApi(Context context, WebView webView) {
        this.context = context;
        this.webView = webView;
        configureWebView();
    }

    public void getResponse(String from, String to, RecyclerView recyclerView) {
        SessionManager sessionManager = new SessionManager(context);
        String token = sessionManager.getToken();

        if (token == null || token.isEmpty()) {
            Toast.makeText(context, "Authentication token missing", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        Log.d("Request from and to date", from + " to: " + to);

        apiService.getEarningResponse(token, from, to).enqueue(new Callback<List<EarningResponse>>() {
            @Override
            public void onResponse(Call<List<EarningResponse>> call, Response<List<EarningResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<EarningResponse> earningsList = response.body();
                    Log.d("Earnings Report", "Response: " + earningsList);

                    if (earningsList.isEmpty()) {
                        Toast.makeText(context, "No earnings data available for the selected period.", Toast.LENGTH_SHORT).show();
                    } else {
                        recyclerView.setLayoutManager(new LinearLayoutManager(context));
                        recyclerView.setAdapter(new EarningsAdapter(earningsList));

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
                    Log.e("Earnings API Error", "HTTP Status Code: " + statusCode);
                    Toast.makeText(context, "Error fetching data. Status Code: " + statusCode, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<EarningResponse>> call, Throwable t) {
                Log.e("Earnings API Failure", "Error: " + t.getMessage(), t);
                Toast.makeText(context, "Failed to fetch data. Please check your internet connection.", Toast.LENGTH_LONG).show();
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
                    Log.d("WebView", "Google Pie Chart loaded successfully.");
                }
            });
        } else {
            Log.e("WebView Error", "WebView is null. Cannot load chart.");
        }
    }

    public void googlePieChart(double cash, double epayments, double account) {
        if (webView != null) {
            webView.loadUrl("file:///android_asset/google_pie_chart.html");

            webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    Log.d("WebView", "Google Pie Chart loaded successfully.");

                    String jsonData = "[['Category', 'Value'], ['Cash', " + cash + "], ['E-Payments', " + epayments + "], ['Account', " + account + "]]";
                    updateChart(jsonData);
                }
            });
        } else {
            Log.e("WebView Error", "WebView is null. Cannot update chart.");
        }
    }

    private void updateChart(String jsonData) {
        if (webView != null) {
            webView.evaluateJavascript("javascript:updateChart(" + jsonData + ")", value ->
                    Log.d("WebView JS Response", "Chart updated: " + value)
            );
        } else {
            Log.e("WebView Error", "WebView is null. Cannot execute JavaScript.");
        }
    }
}
