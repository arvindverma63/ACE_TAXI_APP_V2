package com.example.ace_taxi_v2.Components;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.example.ace_taxi_v2.Models.Reports.StatementItem;
import com.example.ace_taxi_v2.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class StatementDialog {

    public void showStatementDialog(Context context, StatementItem statementItem) {
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
        tvAccountJobs.setText("Account Jobs: " + statementItem.getAccountJobsTotalCount());
        tvCashJobs.setText("Cash Jobs: " + statementItem.getCashJobsTotalCount());
        tvCommission.setText("Commission Due: £" + statementItem.getCommissionDue());
        tvDateCreated.setText("Date Created: " + (statementItem.getDateCreated() != null ? statementItem.getDateCreated().toString() : "N/A"));
        tvEarningsAccount.setText("Earnings Account: £" + statementItem.getEarningsAccount());
        tvEarningsCash.setText("Earnings Cash: £" + statementItem.getEarningsCash());
        tvPaymentDue.setText("Payment Due: £" + statementItem.getPaymentDue());
        tvTotalEarned.setText("Total Earned: £" + statementItem.getTotalEarned());
        tvTotalJobs.setText("Total Jobs: " + statementItem.getTotalJobCount());

        // Build Material Dialog
        AlertDialog dialog = new MaterialAlertDialogBuilder(context)
                .setView(view)
                .setCancelable(true)
                .create();

        // Close button action
        btnClose.setOnClickListener(v -> dialog.dismiss());

        // Show the dialog
        dialog.show();
    }
}
