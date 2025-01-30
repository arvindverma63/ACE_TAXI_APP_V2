package com.example.ace_taxi_v2.Logic.Service;

import android.content.Context;
import android.content.SharedPreferences;

public class ConfigSessionManager {

    private static final String PREF_NAME = "ConfigSession";
    private static final String PHONE_NUMBER_KEY = "phone_number";
    private static final String WHATSAPP_NUMBER_KEY = "whatsapp_number";

    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;

    public ConfigSessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    // Store Phone Number
    public void savePhoneNumber(String phoneNumber) {
        editor.putString(PHONE_NUMBER_KEY, phoneNumber);
        editor.apply();
    }

    // Retrieve Phone Number
    public String getPhoneNumber() {
        return sharedPreferences.getString(PHONE_NUMBER_KEY, null);
    }

    // Store WhatsApp Number
    public void saveWhatsAppNumber(String whatsappNumber) {
        editor.putString(WHATSAPP_NUMBER_KEY, whatsappNumber);
        editor.apply();
    }

    // Retrieve WhatsApp Number
    public String getWhatsAppNumber() {
        return sharedPreferences.getString(WHATSAPP_NUMBER_KEY, null);
    }

    // Clear all stored data
    public void clearConfig() {
        editor.clear();
        editor.apply();
    }
}
