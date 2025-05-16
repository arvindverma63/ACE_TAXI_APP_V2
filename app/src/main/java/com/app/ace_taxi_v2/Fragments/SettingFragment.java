package com.app.ace_taxi_v2.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.app.ace_taxi_v2.Activity.LoginActivity;
import com.app.ace_taxi_v2.Components.ConfigModal;
import com.app.ace_taxi_v2.Components.CustomDialog;
import com.app.ace_taxi_v2.Helper.DeviceMode;
import com.app.ace_taxi_v2.Instance.RetrofitClient;
import com.app.ace_taxi_v2.Logic.LoginManager;
import com.app.ace_taxi_v2.Logic.Service.ScreenOnOffManager;
import com.app.ace_taxi_v2.Logic.SessionManager;
import com.app.ace_taxi_v2.Models.UserProfileResponse;
import com.app.ace_taxi_v2.R;
import com.app.ace_taxi_v2.SettingsPermission.CheckPermission;
import com.app.ace_taxi_v2.SettingsPermission.Permission;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textview.MaterialTextView;

public class SettingFragment extends Fragment {

    private static final String PREF_NAME = "app_preferences";
    private static final String KEY_DARK_MODE = "dark_mode";
    private static final String TAG = "SettingFragment";

    private TextView theme_switch_text, gps_switch_text, notification_switch_text, sms_switch_text, url_text, keep_alive_switch_text;
    private Switch switch_dark_mode, notification_swtich, gps_switch, sms_switch, keep_alive_switch, device_mode;
    private Button config;
    private Permission permission;
    private MaterialTextView driver_name, driver_email;
    private ScreenOnOffManager screenOnOffManager;
    private MaterialToolbar toolbar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        if (getContext() == null || getActivity() == null) {
            Log.e(TAG, "Context or Activity is null");
            Toast.makeText(getContext(), "Error initializing settings", Toast.LENGTH_SHORT).show();
            return view;
        }

        try {
            screenOnOffManager = new ScreenOnOffManager(requireContext(), requireActivity().getWindow());
        } catch (IllegalStateException e) {
            Log.e(TAG, "Failed to initialize ScreenOnOffManager", e);
        }

        initializeViews(view);
        initializePermissions();
        setupPermissionChecks();
        setDriverDetails();
        setupKeepAliveSwitch();
        setupSwitchListeners();
        setupToolbar(view);
        setupDarkModeSwitch();
        changeMode();

        config.setOnClickListener(v -> {
            try {
                ConfigModal configModal = new ConfigModal(getContext());
                configModal.openConfigModal();
            } catch (Exception e) {
                Log.e(TAG, "Failed to open config modal", e);
                Toast.makeText(getContext(), "Error opening configuration", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void initializeViews(View view) {
        theme_switch_text = view.findViewById(R.id.theme_switch_text);
        switch_dark_mode = view.findViewById(R.id.switch_dark_mode);
        notification_swtich = view.findViewById(R.id.notification_swtich);
        gps_switch = view.findViewById(R.id.gps_switch);
        sms_switch = view.findViewById(R.id.sms_switch);
        keep_alive_switch = view.findViewById(R.id.keep_alive_switch);
        config = view.findViewById(R.id.config);
        driver_email = view.findViewById(R.id.driver_email);
        device_mode = view.findViewById(R.id.app_mode);
        driver_name = view.findViewById(R.id.driver_name);
        notification_switch_text = view.findViewById(R.id.notification_switch_text);
        gps_switch_text = view.findViewById(R.id.gps_switch_text);
        sms_switch_text = view.findViewById(R.id.sms_switch_text);
        keep_alive_switch_text = view.findViewById(R.id.keep_alive_switch_text);
        url_text = view.findViewById(R.id.url_text);
        toolbar = view.findViewById(R.id.toolbar_header);

        // Accessibility
        switch_dark_mode.setContentDescription("Toggle dark mode");
        notification_swtich.setContentDescription("Toggle notifications");
        gps_switch.setContentDescription("Toggle GPS");
        sms_switch.setContentDescription("Toggle SMS");
        keep_alive_switch.setContentDescription("Toggle keep screen on");
        device_mode.setContentDescription("Toggle app mode");
        toolbar.setNavigationContentDescription("Navigate to home");
    }

    private void initializePermissions() {
        if (getContext() == null) {
            Log.e(TAG, "Context is null");
            return;
        }
        permission = new Permission(getContext());
    }

    private void setupPermissionChecks() {
        if (getContext() == null) {
            Log.e(TAG, "Context is null");
            return;
        }
        CheckPermission checkPermission = new CheckPermission(getContext());
        checkPermission.notificationPermission(notification_swtich);
        checkPermission.gpsPermission(gps_switch);
    }

    private void setupKeepAliveSwitch() {
        if (screenOnOffManager == null) return;
        boolean isScreenOnEnabled = screenOnOffManager.isScreenOnEnabled();
        keep_alive_switch.setChecked(isScreenOnEnabled);
        keepAliveSwitch(isScreenOnEnabled);
    }

    private void setupSwitchListeners() {
        notification_swtich.setOnCheckedChangeListener((button, isChecked) -> notificationSwitch(isChecked));
        gps_switch.setOnCheckedChangeListener((button, isChecked) -> gpsSwitch(isChecked));
        sms_switch.setOnCheckedChangeListener((button, isChecked) -> smsSwitch(isChecked));
        keep_alive_switch.setOnCheckedChangeListener((button, isChecked) -> {
            keepAliveSwitch(isChecked);
            if (screenOnOffManager != null) {
                screenOnOffManager.applyScreenOnBasedOnView(null);
            }
        });
    }

    private void setupToolbar(View view) {
        toolbar.setNavigationOnClickListener(v -> {
            FragmentManager fm = requireActivity().getSupportFragmentManager();
            if (fm.findFragmentByTag("HomeFragment") == null) {
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.fragment_container, new HomeFragment(), "HomeFragment");
                ft.addToBackStack(null);
                ft.commit();
            } else {
                fm.popBackStack("HomeFragment", 0);
            }
        });
    }

    private void setupDarkModeSwitch() {
        if (getContext() == null || getActivity() == null) {
            Log.e(TAG, "Context or Activity is null");
            return;
        }

        SharedPreferences prefs = requireActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        boolean isDarkMode = prefs.getBoolean(KEY_DARK_MODE, false);
        switch_dark_mode.setChecked(isDarkMode);
        updateThemeText(isDarkMode);

        switch_dark_mode.setOnCheckedChangeListener((button, isChecked) -> {
            try {
                AppCompatDelegate.setDefaultNightMode(isChecked
                        ? AppCompatDelegate.MODE_NIGHT_YES
                        : AppCompatDelegate.MODE_NIGHT_NO);
                updateThemeText(isChecked);
                prefs.edit().putBoolean(KEY_DARK_MODE, isChecked).apply();
            } catch (IllegalStateException e) {
                Log.e(TAG, "Failed to set night mode", e);
                Toast.makeText(getContext(), "Error changing theme", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (screenOnOffManager != null) {
            boolean isScreenOnEnabled = screenOnOffManager.isScreenOnEnabled();
            keep_alive_switch.setChecked(isScreenOnEnabled);
            keepAliveSwitch(isScreenOnEnabled);
            screenOnOffManager.applyScreenOnBasedOnView(null);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (screenOnOffManager != null) {
            screenOnOffManager.disableScreenOn();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        theme_switch_text = null;
        switch_dark_mode = null;
        notification_swtich = null;
        gps_switch = null;
        sms_switch = null;
        keep_alive_switch = null;
        config = null;
        driver_email = null;
        device_mode = null;
        driver_name = null;
        notification_switch_text = null;
        gps_switch_text = null;
        sms_switch_text = null;
        keep_alive_switch_text = null;
        url_text = null;
        toolbar = null;
    }

    private void updateThemeText(boolean isDarkMode) {
        try {
            if (isDarkMode) {
                theme_switch_text.setText("Dark Mode");
                Drawable darkModeIcon = getResources().getDrawable(R.drawable.ic_night_mode, null);
                theme_switch_text.setCompoundDrawablesWithIntrinsicBounds(addDrawableMargin(darkModeIcon, 16), null, null, null);
                theme_switch_text.setCompoundDrawableTintList(ColorStateList.valueOf(getResources().getColor(R.color.white, null)));
                updateSwitchColors(switch_dark_mode, true);
            } else {
                theme_switch_text.setText("Light Mode");
                Drawable lightModeIcon = getResources().getDrawable(R.drawable.ic_light_mode, null);
                theme_switch_text.setCompoundDrawablesWithIntrinsicBounds(addDrawableMargin(lightModeIcon, 16), null, null, null);
                theme_switch_text.setCompoundDrawableTintList(ColorStateList.valueOf(getResources().getColor(R.color.black, null)));
                updateSwitchColors(switch_dark_mode, false);
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to update theme text", e);
        }
    }

    private Drawable addDrawableMargin(Drawable drawable, int marginInPx) {
        return new InsetDrawable(drawable, 0, 0, marginInPx, 0);
    }

    private void updateSwitchColors(Switch switchView, boolean isOn) {
        int color = isOn ? R.color.primaryColor : R.color.gray;
        switchView.setTrackTintList(ColorStateList.valueOf(getResources().getColor(color)));
        switchView.setThumbTintList(ColorStateList.valueOf(getResources().getColor(color)));
    }

    private void notificationSwitch(boolean isOn) {
        updateSwitchColors(notification_swtich, isOn);
        if (permission != null) {
            permission.notificationPermission(isOn);
        }
    }

    private void gpsSwitch(boolean isOn) {
        updateSwitchColors(gps_switch, isOn);
        if (permission != null) {
            permission.locationPermission(isOn);
        }
    }

    private void smsSwitch(boolean isOn) {
        updateSwitchColors(sms_switch, isOn);
        if (permission != null) {
            permission.smsPermission(isOn);
        }
        sms_switch_text.setText(isOn ? "SMS On" : "SMS Off");
    }

    private void keepAliveSwitch(boolean isOn) {
        if (screenOnOffManager == null) return;
        updateSwitchColors(keep_alive_switch, isOn);
        if (isOn) {
            screenOnOffManager.enableScreenOn();
            keep_alive_switch_text.setText("Screen On");
        } else {
            screenOnOffManager.disableScreenOn();
            keep_alive_switch_text.setText("Screen Off");
        }
    }

    public void setDriverDetails() {
        if (getContext() == null) return;
        driver_name.setText("Loading...");
        driver_email.setText("Loading...");
        LoginManager loginManager = new LoginManager(getContext());
        loginManager.getProfile(new LoginManager.ProfileCallback() {
            @Override
            public void onSuccess(UserProfileResponse userProfileResponse) {
                if (userProfileResponse != null) {
                    driver_email.setText(userProfileResponse.getEmail());
                    driver_name.setText(userProfileResponse.getFullname());
                } else {
                    driver_name.setText("N/A");
                    driver_email.setText("N/A");
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                driver_name.setText("Error");
                driver_email.setText("Failed to load profile");
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void changeMode() {
        // Set initial state based on saved URL mode
        boolean isLiveMode = DeviceMode.getInstance().isLiveMode();
        device_mode.setChecked(isLiveMode);

        // Update UI initially based on the mode
        updateUIForMode(isLiveMode);

        device_mode.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            DeviceMode.getInstance().setBaseURL(isChecked);

            // Clear token/session as before
            SessionManager sessionManager = new SessionManager(getContext());
            sessionManager.clearToken();

            // Reset Retrofit so next calls use the new URL
            RetrofitClient.resetRetrofitInstance();

            updateUIForMode(isChecked);

            // Restart app
            Intent intent = new Intent(getContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        });

    }


    private void updateUIForMode(boolean isLive) {
        if (isLive) {
            url_text.setText("LIVE MODE");
            gps_switch.setTrackTintList(ColorStateList.valueOf(getResources().getColor(R.color.red)));
            gps_switch.setThumbTintList(ColorStateList.valueOf(getResources().getColor(R.color.red)));
            keep_alive_switch.setTrackTintList(ColorStateList.valueOf(getResources().getColor(R.color.red)));
            keep_alive_switch.setThumbTintList(ColorStateList.valueOf(getResources().getColor(R.color.red)));
        } else {
            url_text.setText("DEV MODE");
            gps_switch.setTrackTintList(ColorStateList.valueOf(getResources().getColor(R.color.gray)));
            gps_switch.setThumbTintList(ColorStateList.valueOf(getResources().getColor(R.color.gray)));
            keep_alive_switch.setTrackTintList(ColorStateList.valueOf(getResources().getColor(R.color.gray)));
            keep_alive_switch.setThumbTintList(ColorStateList.valueOf(getResources().getColor(R.color.gray)));
        }
    }

}