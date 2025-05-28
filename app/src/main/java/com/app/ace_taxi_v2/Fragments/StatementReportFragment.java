package com.app.ace_taxi_v2.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.app.ace_taxi_v2.ApiService.ApiService;
import com.app.ace_taxi_v2.Components.CustomDialog;
import com.app.ace_taxi_v2.Components.CustomToast;
import com.app.ace_taxi_v2.Fragments.Adapters.StatementAdapter;
import com.app.ace_taxi_v2.Helper.LogHelperLaravel;
import com.app.ace_taxi_v2.Instance.RetrofitClient;
import com.app.ace_taxi_v2.Logic.GetStatementsApi;
import com.app.ace_taxi_v2.Logic.OpenStatement;
import com.app.ace_taxi_v2.Logic.SessionManager;
import com.app.ace_taxi_v2.Models.Reports.StatementItem;
import com.app.ace_taxi_v2.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textview.MaterialTextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StatementReportFragment extends Fragment implements OpenStatement {

    private TextView totalEarn;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MaterialTextView noDataText;
    private List<StatementItem> statementList = new ArrayList<>();
    private StatementAdapter statementAdapter;
    private SimpleDateFormat isoDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS", Locale.getDefault());
    private SimpleDateFormat displayFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
    private String startDate, endDate;
    private MaterialButton dateRangeButton;
    private int userId;

    private static final String KEY_START_DATE = "start_date";
    private static final String KEY_END_DATE = "end_date";
    private static final String FILE_PROVIDER_AUTHORITY = "com.app.ace_taxi_v2.fileprovider";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_statement_report, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize SessionManager and userId
        SessionManager sessionManager = new SessionManager(requireContext());
        userId = sessionManager.getUserId();

        // Initialize UI Components
        recyclerView = view.findViewById(R.id.recycler_view);
        totalEarn = view.findViewById(R.id.totalAmount);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        noDataText = view.findViewById(R.id.no_data_text);
        MaterialToolbar toolbar = view.findViewById(R.id.header_toolbar);
        dateRangeButton = view.findViewById(R.id.date_range_button);

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        statementAdapter = new StatementAdapter(requireContext(), statementList, this);
        recyclerView.setAdapter(statementAdapter);

        // Set up SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(this::fetchStatements);

        // Set toolbar navigation
        toolbar.setNavigationOnClickListener(v -> navigateToReportPage());

        // Set up date range button
        dateRangeButton.setOnClickListener(v -> showDateRangePicker());

        // Restore state or set default date range
        if (savedInstanceState != null) {
            startDate = savedInstanceState.getString(KEY_START_DATE);
            endDate = savedInstanceState.getString(KEY_END_DATE);
            updateDateRangeButtonText();
        } else {
            setDefaultDateRange();
        }

        // Load Data Initially
        fetchStatements();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_START_DATE, startDate);
        outState.putString(KEY_END_DATE, endDate);
    }

    @Override
    public void openStatement(int statementId, String fileName) {
        SessionManager sessionManager = new SessionManager(requireContext());
        String token = sessionManager.getToken();

        if (token == null || token.isEmpty()) {
            Log.e("StatementReportFragment", "Authentication token missing for statementId: " + statementId);
            Toast.makeText(requireContext(), "Authentication token missing", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show progress dialog
        CustomDialog customDialog = new CustomDialog();
        customDialog.showProgressDialog(requireContext());

        // Construct the API call
        String authHeader = "Bearer " + token;
        Log.d("StatementReportFragment", "Fetching PDF for statementId: " + statementId + " with token: " + token);

        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        Call<ResponseBody> call = apiService.downloadStatement(authHeader, statementId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                customDialog.dismissProgressDialog();

                if (response.isSuccessful() && response.body() != null) {
                    // Sanitize file name and use a unique temporary file
                    String sanitizedFileName = "Statement_" + statementId + "_" + System.currentTimeMillis() + ".pdf";
                    File tempFile = new File(requireContext().getCacheDir(), sanitizedFileName);
                    Log.d("StatementReportFragment", "Saving PDF to: " + tempFile.getAbsolutePath());

                    // Check available storage
                    long availableSpace = requireContext().getCacheDir().getUsableSpace();
                    long requiredSpace = 10 * 1024 * 1024; // Assume 10MB needed
                    if (availableSpace < requiredSpace) {
                        Log.e("StatementReportFragment", "Insufficient storage: " + availableSpace + " bytes available");
                        Toast.makeText(requireContext(), "Insufficient storage to save PDF", Toast.LENGTH_LONG).show();
                        return;
                    }

                    try (InputStream inputStream = response.body().byteStream();
                         FileOutputStream outputStream = new FileOutputStream(tempFile)) {
                        byte[] buffer = new byte[8192];
                        int bytesRead;
                        long totalBytes = 0;
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                            totalBytes += bytesRead;
                        }
                        outputStream.flush();
                        Log.d("StatementReportFragment", "PDF saved: " + totalBytes + " bytes");

                        // Open the temporary file
                        try {
                            Uri pdfUri = FileProvider.getUriForFile(requireContext(), FILE_PROVIDER_AUTHORITY, tempFile);
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setDataAndType(pdfUri, "application/pdf");
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

                            startActivity(intent);
                            Log.d("StatementReportFragment", "PDF opened successfully: " + tempFile.getAbsolutePath());
                        } catch (IllegalArgumentException e) {
                            Log.e("StatementReportFragment", "FileProvider error: " + e.getMessage(), e);
                            Toast.makeText(requireContext(), "Failed to open PDF: File provider configuration error", Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            Log.e("StatementReportFragment", "Error opening PDF: " + e.getMessage(), e);
                            Toast.makeText(requireContext(), "No PDF viewer app installed", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e("StatementReportFragment", "Error saving PDF: " + e.getMessage(), e);
                        Toast.makeText(requireContext(), "Failed to save PDF: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    String errorBody = "";
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        Log.e("StatementReportFragment", "Error reading error body: " + e.getMessage());
                    }
                    Log.e("StatementReportFragment", "Server error: HTTP " + response.code() + " - " + response.message() + " | Body: " + errorBody);
                    String errorMessage = "Failed to fetch PDF: " + response.message();
                    if (response.code() == 401) {
                        errorMessage = "Unauthorized: Invalid or expired token";
                    } else if (response.code() == 404) {
                        errorMessage = "PDF not found for statementId: " + statementId;
                    } else if (response.code() == 403) {
                        errorMessage = "Access denied for this PDF";
                    }
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show();
                    new CustomToast(getContext()).showCustomErrorToast(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                customDialog.dismissProgressDialog();
                Log.e("StatementReportFragment", "Error fetching PDF: " + t.getMessage());
                Toast.makeText(requireContext(), "Failed to fetch PDF: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setDefaultDateRange() {
        Calendar calendar = Calendar.getInstance();
        endDate = isoDateFormat.format(calendar.getTime());
        calendar.add(Calendar.DAY_OF_YEAR, -30);
        startDate = isoDateFormat.format(calendar.getTime());
        updateDateRangeButtonText();
    }

    private void updateDateRangeButtonText() {
        try {
            Date start = isoDateFormat.parse(startDate);
            Date end = isoDateFormat.parse(endDate);
            if (start != null && end != null) {
                dateRangeButton.setText(displayFormat.format(start) + " - " + displayFormat.format(end));
            }
        } catch (Exception e) {
            Log.e("StatementReport", "Error parsing dates for button text", e);
        }
    }

    private void fetchStatements() {
        swipeRefreshLayout.setRefreshing(true);

        GetStatementsApi getStatementsApi = new GetStatementsApi(requireContext(), startDate, endDate, userId);
        getStatementsApi.getStatements(new GetStatementsApi.statementListener() {
            @Override
            public void onSuccess(List<StatementItem> items) {
                statementAdapter.updateData(items);
                calculateTotalEarnings(items);
                updateEmptyView();
                try{
                    LogHelperLaravel.getInstance().i("Statement Logs: ",items.get(0).getStatementDate()+"");
                }catch (Exception e){
                    LogHelperLaravel.getInstance().e("statement error",e+"");
                }
                if (items == null || items.isEmpty()) {
                    new CustomToast(getContext()).showCustomErrorToast("No statement found");
                }
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFail(String error) {
                String errorMessage = "Failed to fetch statements";
                if (error != null) {
                    if (error.contains("network")) {
                        errorMessage = "Network error. Please check your connection.";
                    } else if (error.contains("server")) {
                        errorMessage = "Server error. Please try again later.";
                    }
                }
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
                statementAdapter.updateData(null);
                calculateTotalEarnings(null);
                updateEmptyView();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void calculateTotalEarnings(List<StatementItem> items) {
        double total = 0;
        if (items != null) {
            for (StatementItem item : items) {
                if (item != null) {
                    total += item.getSubTotal();
                }
            }
        }
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.UK);
        totalEarn.setText(currencyFormat.format(total));
    }

    private void updateEmptyView() {
        if (statementAdapter.getItemCount() == 0) {
            recyclerView.setVisibility(View.GONE);
            noDataText.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            noDataText.setVisibility(View.GONE);
        }
    }

    private void navigateToReportPage() {
        Fragment selectedFragment = new ReportPageFragment();
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, selectedFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void showDateRangePicker() {
        MaterialDatePicker.Builder<androidx.core.util.Pair<Long, Long>> builder =
                MaterialDatePicker.Builder.dateRangePicker().setTheme(R.style.ThemeOverlay_App_MaterialCalendar);
        builder.setTitleText("Select Date Range");

        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
        builder.setCalendarConstraints(constraintsBuilder.build());

        MaterialDatePicker<androidx.core.util.Pair<Long, Long>> datePicker = builder.build();
        datePicker.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDatePickerTheme);
        datePicker.show(getParentFragmentManager(), "DATE_PICKER");

        datePicker.addOnPositiveButtonClickListener(selection -> {
            try {
                // Set time to 00:00:00.0000000 for start date and 23:59:59.9999999 for end date
                Calendar startCal = Calendar.getInstance();
                startCal.setTimeInMillis(selection.first);
                startCal.set(Calendar.HOUR_OF_DAY, 0);
                startCal.set(Calendar.MINUTE, 0);
                startCal.set(Calendar.SECOND, 0);
                startCal.set(Calendar.MILLISECOND, 0);

                Calendar endCal = Calendar.getInstance();
                endCal.setTimeInMillis(selection.second);
                endCal.set(Calendar.HOUR_OF_DAY, 23);
                endCal.set(Calendar.MINUTE, 59);
                endCal.set(Calendar.SECOND, 59);
                endCal.set(Calendar.MILLISECOND, 999);

                startDate = isoDateFormat.format(startCal.getTime());
                endDate = isoDateFormat.format(endCal.getTime());

                updateDateRangeButtonText();
                fetchStatements();
            } catch (Exception e) {
                Log.e("DatePicker", "Error formatting dates", e);
                Toast.makeText(requireContext(), "Error selecting date range", Toast.LENGTH_SHORT).show();
            }
        });
    }
}