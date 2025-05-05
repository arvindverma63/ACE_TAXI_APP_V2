package com.app.ace_taxi_v2.Components;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.app.ace_taxi_v2.Models.Reports.StatementItem;
import com.app.ace_taxi_v2.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.NumberFormat;
import java.util.Locale;

public class StatementDialog {

    public void showStatementDialog(Context context, StatementItem statementItem) {
        try {
            // Initialize NumberFormat for UK currency
            NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.UK);

            // Inflate the dialog layout
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.statement_modal, null);

            // Find UI elements
            TextView tvAccountJobs = view.findViewById(R.id.tv_accountJobsTotalCount);
            TextView tvCashJobs = view.findViewById(R.id.tv_cashJobsTotalCount);
            TextView tvCommission = view.findViewById(R.id.tv_commissionDue);
            TextView tvDateCreated = view.findViewById(R.id.tv_dateCreated);
            TextView tvEarningsAccount = view.findViewById(R.id.tv_earningsAccount);
            TextView tvEarningsCash = view.findViewById(R.id.tv_earningsCash);
            TextView tvPaymentDue = view.findViewById(R.id.tv_paymentDue);
            TextView tvTotalEarned = view.findViewById(R.id.tv_totalEarned);
            TextView tvTotalJobs = view.findViewById(R.id.tv_totalJobCount);
            Button btnClose = view.findViewById(R.id.btn_close);

            // Populate data
            try {
//                tvAccountJobs.setText("Account Jobs: " + statementItem.getAccountJobsTotalCount());
//                tvCashJobs.setText("Cash Jobs: " + statementItem.getCashJobsTotalCount());
//                tvCommission.setText("Commission Due: " + formatter.format(statementItem.getCommissionDue()));
//                tvDateCreated.setText("Date Created: " + (statementItem.getDateCreated() != null ? statementItem.getDateCreated().toString() : "N/A"));
//                tvEarningsAccount.setText("Earnings Account: " + formatter.format(statementItem.getEarningsAccount()));
//                tvEarningsCash.setText("Earnings Cash: " + formatter.format(statementItem.getEarningsCash()));
//                tvPaymentDue.setText("Payment Due: " + formatter.format(statementItem.getPaymentDue()));
//                tvTotalEarned.setText("Total Earned: " + formatter.format(statementItem.getTotalEarned()));
//                tvTotalJobs.setText("Total Jobs: " + statementItem.getTotalJobCount());
            } catch (Exception e) {
                e.printStackTrace();
                // Fallback values in case of data errors
                tvCommission.setText("Commission Due: N/A");
                tvEarningsAccount.setText("Earnings Account: N/A");
                tvEarningsCash.setText("Earnings Cash: N/A");
                tvPaymentDue.setText("Payment Due: N/A");
                tvTotalEarned.setText("Total Earned: N/A");
            }

            // Build Material Dialog
            AlertDialog dialog = new MaterialAlertDialogBuilder(context)
                    .setView(view)
                    .setCancelable(true)
                    .create();

            // Close button action
            btnClose.setOnClickListener(v -> {
                try {
                    dialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            // Show the dialog
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}