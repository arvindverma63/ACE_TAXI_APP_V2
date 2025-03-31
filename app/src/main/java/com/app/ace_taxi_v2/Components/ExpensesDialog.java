package com.app.ace_taxi_v2.Components;

import android.content.Context;
import android.content.DialogInterface;

import androidx.core.content.ContextCompat;

import com.app.ace_taxi_v2.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class ExpensesDialog {
    public Context context;

    public ExpensesDialog(Context context){
        this.context = context;
    }

    public void openExpensesDescriptionDialog(String message){
        MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(context);
        materialAlertDialogBuilder.setTitle("Expenses Description");
        materialAlertDialogBuilder.setMessage(message);

        materialAlertDialogBuilder.setBackground(ContextCompat.getDrawable(context, R.color.white));
        materialAlertDialogBuilder.show();

        materialAlertDialogBuilder.setCancelable(true);
        materialAlertDialogBuilder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
            }
        });
    }
}
