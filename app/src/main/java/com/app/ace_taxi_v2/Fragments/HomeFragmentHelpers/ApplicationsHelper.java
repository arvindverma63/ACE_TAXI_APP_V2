package com.app.ace_taxi_v2.Fragments.HomeFragmentHelpers;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.app.ace_taxi_v2.Fragments.SettingFragment;
import com.app.ace_taxi_v2.Logic.Service.ConfigSessionManager;
import com.google.android.material.card.MaterialCardView;

public class ApplicationsHelper {
    private Context context;
    private int containerId; // The ID of the container where fragments will be displayed

    public ApplicationsHelper(Context context, int containerId) {
        this.context = context;
        this.containerId = containerId;
    }

    public void applicationEvents(MaterialCardView messageBtn, MaterialCardView phoneBtn,
                             MaterialCardView settingsBtn) {
        // Check if context is an Activity and get FragmentManager
        if (!(context instanceof AppCompatActivity)) {
            throw new IllegalArgumentException("Context must be an instance of AppCompatActivity");
        }
        FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();

        // Set click listeners with null checks
        if (messageBtn != null) {
            messageBtn.setOnClickListener(v -> {
                ConfigSessionManager configSessionManager = new ConfigSessionManager(context);
                String whatsappNumber = configSessionManager.getWhatsAppNumber();

                if (whatsappNumber != null && !whatsappNumber.isEmpty()) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("https://wa.me/" + whatsappNumber));
                        context.startActivity(intent);
                    } catch (Exception e) {
                        Toast.makeText(context, "WhatsApp not installed", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "WhatsApp number not found", Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (phoneBtn != null) {
            phoneBtn.setOnClickListener(v -> {
                ConfigSessionManager configSessionManager = new ConfigSessionManager(context);
                String phoneNumber = configSessionManager.getPhoneNumber();

                if (phoneNumber != null && !phoneNumber.isEmpty()) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + phoneNumber));
                    context.startActivity(intent);
                } else {
                    Toast.makeText(context, "Phone number not found", Toast.LENGTH_SHORT).show();
                }
            });

        }

        if (settingsBtn != null) {
            settingsBtn.setOnClickListener(v -> {
                Fragment newExpense = new SettingFragment();
                replaceFragment(fragmentManager, newExpense);
            });
        }

    }

    private void replaceFragment(FragmentManager fragmentManager, Fragment fragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(containerId, fragment);
        transaction.addToBackStack(null); // Optional: adds fragment to back stack
        transaction.commit();
    }
}