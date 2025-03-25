package com.app.ace_taxi_v2.Components;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.graphics.drawable.Drawable;
import android.widget.Button;
import android.widget.Toast;
import androidx.core.content.ContextCompat;
import com.app.ace_taxi_v2.Logic.Service.ConfigSessionManager;
import com.app.ace_taxi_v2.R;
import com.google.android.material.textfield.TextInputEditText;

public class ConfigModal {

    private final Context context;
    private AlertDialog dialog; // Store reference to dismiss later

    public ConfigModal(Context context) {
        this.context = context;
    }

    public void openConfigModal() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View parent = inflater.inflate(R.layout.config_modal, null);

        // Default numbers
        String defaultNumber = "+0000000000";
        String defaultWhatsapp = "+0000000000";

        // Initialize session manager
        ConfigSessionManager configSessionManager = new ConfigSessionManager(context);

        // Retrieve stored numbers (use defaults if null)
        String phoneNumberSession = configSessionManager.getPhoneNumber() != null ? configSessionManager.getPhoneNumber() : defaultNumber;
        String whatsappNumberSession = configSessionManager.getWhatsAppNumber() != null ? configSessionManager.getWhatsAppNumber() : defaultWhatsapp;

        // UI elements
        TextInputEditText phoneNumber = parent.findViewById(R.id.phone_number);
        TextInputEditText whatsappNumber = parent.findViewById(R.id.whatsapp_number);
        Button btnSubmit = parent.findViewById(R.id.btnSubmit);
        Button btnReset = parent.findViewById(R.id.btn_reset);

        // Set current session values
        phoneNumber.setText(phoneNumberSession);
        whatsappNumber.setText(whatsappNumberSession);

        // Reset to default values
        btnReset.setOnClickListener(v -> {
//            configSessionManager.savePhoneNumber(defaultNumber);
//            configSessionManager.saveWhatsAppNumber(defaultWhatsapp);

            // Update UI
            phoneNumber.setText(defaultNumber);
            whatsappNumber.setText(defaultWhatsapp);

            // Close dialog & Show success message
//            closeConfigModal();
        });

        // Submit updated values
        btnSubmit.setOnClickListener(v -> {
            String phoneNumberUpdate = phoneNumber.getText().toString();
            String whatsappNumberUpdate = whatsappNumber.getText().toString();

            configSessionManager.savePhoneNumber(phoneNumberUpdate);
            configSessionManager.saveWhatsAppNumber(whatsappNumberUpdate);

            // Close dialog & Show success message
            closeConfigModal();
            Toast.makeText(context, "Settings saved successfully!", Toast.LENGTH_SHORT).show();
        });

        // Create and show dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(parent);
        builder.setCancelable(true);

        dialog = builder.create();

        // Set rounded background correctly
        if (dialog.getWindow() != null) {
            Drawable background = ContextCompat.getDrawable(context, R.drawable.rounded_dialog);
            dialog.getWindow().setBackgroundDrawable(background);
        }

        dialog.show();
    }

    // Method to close dialog manually
    private void closeConfigModal() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
