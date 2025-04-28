package com.app.ace_taxi_v2.Components;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.app.ace_taxi_v2.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class EarningStatementDialog {
    private final Context context;
    private AlertDialog dialog;
    private final SimpleDateFormat apiDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
    private final SimpleDateFormat inputDateFormat = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
    private final SimpleDateFormat displayDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    public EarningStatementDialog(Context context) {
        this.context = context;
    }

    public void openDialog(
            String date, int userId, double cashTotal, double accTotal,
            double rankTotal, double commsTotal, double grossTotal, double netTotal,
            int cashJobsCount, int accJobsCount, int rankJobsCount,
            double cashMilesCount, double accMilesCount, double rankMilesCount
    ) {
        // Inflate the dialog layout
        View view = LayoutInflater.from(context).inflate(R.layout.item_earning_statement_dialog, null);

        // Formatters
        DecimalFormat currencyFormat = new DecimalFormat("£#,##0.00");
        DecimalFormat milesFormat = new DecimalFormat("#,##0.0");

        // Find and populate views
        try {
            // Date
            TextView dateView = view.findViewById(R.id.date);
            String formattedDate = formatDate(date);
            dateView.setText("Date: " + formattedDate);

            // User ID
            TextView userIdView = view.findViewById(R.id.userId);
            userIdView.setText("User ID: " + userId);

            // Totals
            TextView cashTotalView = view.findViewById(R.id.cashTotal);
            cashTotalView.setText("Cash: " + currencyFormat.format(cashTotal));

            TextView accTotalView = view.findViewById(R.id.accTotal);
            accTotalView.setText("Account: " + currencyFormat.format(accTotal));

            TextView rankTotalView = view.findViewById(R.id.rankTotal);
            rankTotalView.setText("Rank: " + currencyFormat.format(rankTotal));

            TextView commsTotalView = view.findViewById(R.id.commsTotal);
            commsTotalView.setText("Comm: " + currencyFormat.format(commsTotal));

            TextView grossTotalView = view.findViewById(R.id.grossTotal);
            grossTotalView.setText("Gross: " + currencyFormat.format(grossTotal));

            TextView netTotalView = view.findViewById(R.id.netTotal);
            netTotalView.setText("Net: " + currencyFormat.format(netTotal));

            // Job Counts
            TextView cashJobsView = view.findViewById(R.id.cashJobsCount);
            cashJobsView.setText("Cash Jobs: " + cashJobsCount);

            TextView accJobsView = view.findViewById(R.id.accJobsCount);
            accJobsView.setText("Acc Jobs: " + accJobsCount);

            TextView rankJobsView = view.findViewById(R.id.rankJobsCount);
            rankJobsView.setText("Rank Jobs: " + rankJobsCount);

            // Miles Counts
            TextView cashMilesView = view.findViewById(R.id.cashMilesCount);
            cashMilesView.setText("Cash: " + milesFormat.format(cashMilesCount) + " mi");

            TextView accMilesView = view.findViewById(R.id.accMilesCount);
            accMilesView.setText("Acc: " + milesFormat.format(accMilesCount) + " mi");

            TextView rankMilesView = view.findViewById(R.id.rankMilesCount);
            rankMilesView.setText("Rank: " + milesFormat.format(rankMilesCount) + " mi");

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create and configure the dialog
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(context)
                .setView(view)
                .setCancelable(true);

        // Handle the close button
        view.findViewById(R.id.close_btn).setOnClickListener(v -> {
            if (dialog != null) {
                dialog.dismiss();
            }
        });

        // Show the dialog
        dialog = dialogBuilder.create();
        dialog.show();
    }

    // Method to dismiss dialog if needed
    public void dismiss() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    private String formatDate(String dateStr) {
        if (dateStr != null && !dateStr.isEmpty()) {
            try {
                // Try parsing as dd/MM/yy (from EarningsAdapter)
                return displayDateFormat.format(inputDateFormat.parse(dateStr));
            } catch (ParseException e) {
                try {
                    // Fallback to parsing as yyyy-MM-dd'T'HH:mm:ss (raw API format)
                    return displayDateFormat.format(apiDateFormat.parse(dateStr));
                } catch (ParseException e2) {
                    e2.printStackTrace();
                }
            }
        }
        return "N/A";
    }

    // Example usage method with your sample data
    public void showSampleDialog() {
        openDialog(
                "2025-03-24T11:59:00", 0, 2.0, 0.0,
                0.0, 0.4, 2.0, 1.6,
                1, 0, 0,
                0.1, 0.0, 0.0
        );
    }
}