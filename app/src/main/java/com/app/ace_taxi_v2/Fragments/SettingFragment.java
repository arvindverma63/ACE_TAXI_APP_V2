package com.app.ace_taxi_v2.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import com.app.ace_taxi_v2.Components.ConfigModal;
import com.app.ace_taxi_v2.Components.CustomDialog;
import com.app.ace_taxi_v2.Logic.LoginManager;
import com.app.ace_taxi_v2.Logic.Service.ScreenOnOffManager;
import com.app.ace_taxi_v2.Models.UserProfileResponse;
import com.app.ace_taxi_v2.R;
import com.app.ace_taxi_v2.SettingsPermission.CheckPermission;
import com.app.ace_taxi_v2.SettingsPermission.Permission;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textview.MaterialTextView;

public class SettingFragment extends Fragment {

    private static final String PREF_NAME = "app_preferences";
    private static final String KEY_DARK_MODE = "dark_mode";
    private TextView theme_switch_text, gps_switch_text, notification_switch_text, url_text, keep_alive_switch_text;
    private Switch switch_dark_mode, notification_swtich, gps_switch, sms_switch, keep_alive_switch;
    private Button config;
    private Permission permission;
    private MaterialTextView driver_name, driver_email;
    private ScreenOnOffManager screenOnOffManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = null;
        try {
            view = inflater.inflate(R.layout.fragment_setting, container, false);

            try {
                screenOnOffManager = new ScreenOnOffManager(requireContext(), requireActivity().getWindow());
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                initializeViews(view);
                initializePermissions();
                setupPermissionChecks();
                setDriverDetails();
                setupKeepAliveSwitch();
                setupSwitchListeners();
                setupToolbar(view);
                setupDarkModeSwitch();
                configBtn();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    private void initializeViews(View view) {
        try {
            theme_switch_text = view.findViewById(R.id.theme_switch_text);
            switch_dark_mode = view.findViewById(R.id.switch_dark_mode);
            notification_swtich = view.findViewById(R.id.notification_swtich);
            gps_switch = view.findViewById(R.id.gps_switch);
            sms_switch = view.findViewById(R.id.sms_switch);
            keep_alive_switch = view.findViewById(R.id.keep_alive_switch);
            config = view.findViewById(R.id.config);
            driver_email = view.findViewById(R.id.driver_email);
            driver_name = view.findViewById(R.id.driver_name);
            notification_switch_text = view.findViewById(R.id.notification_switch_text);
            gps_switch_text = view.findViewById(R.id.gps_switch_text);
            keep_alive_switch_text = view.findViewById(R.id.keep_alive_switch_text);
            url_text = view.findViewById(R.id.url_text);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializePermissions() {
        try {
            permission = new Permission(getContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupPermissionChecks() {
        try {
            CheckPermission checkPermission = new CheckPermission(getContext());
            checkPermission.notificationPermission(notification_swtich);
            checkPermission.gpsPermission(gps_switch);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupKeepAliveSwitch() {
        try {
            boolean isScreenOnEnabled = screenOnOffManager.isScreenOnEnabled();
            keep_alive_switch.setChecked(isScreenOnEnabled);
            keepAliveSwitch(isScreenOnEnabled);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupSwitchListeners() {
        try {
            notification_swtich.setOnCheckedChangeListener((compoundButton, b) -> {
                try {
                    notificationSwitch(b);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            gps_switch.setOnCheckedChangeListener((compoundButton, b) -> {
                try {
                    gpsSwitch(b);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            sms_switch.setOnCheckedChangeListener((compoundButton, b) -> {
                try {
                    smsSwitch(b);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            keep_alive_switch.setOnCheckedChangeListener((compoundButton, b) -> {
                try {
                    keepAliveSwitch(b);
                    screenOnOffManager.applyScreenOnBasedOnView(null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupToolbar(View view) {
        try {
            MaterialToolbar toolbar = view.findViewById(R.id.toolbar_header);
            toolbar.setNavigationOnClickListener(v -> {
                try {
                    Fragment selectedFragment = new HomeFragment();
                    FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, selectedFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupDarkModeSwitch() {
        try {
            SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            boolean isDarkMode = sharedPreferences.getBoolean(KEY_DARK_MODE, false);
            switch_dark_mode.setChecked(isDarkMode);
            updateThemeText(isDarkMode);

            switch_dark_mode.setOnCheckedChangeListener((buttonView, isChecked) -> {
                try {
                    AppCompatDelegate.setDefaultNightMode(isChecked
                            ? AppCompatDelegate.MODE_NIGHT_YES
                            : AppCompatDelegate.MODE_NIGHT_NO);
                    updateThemeText(isChecked);
                    sharedPreferences.edit().putBoolean(KEY_DARK_MODE, isChecked).apply();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            boolean isScreenOnEnabled = screenOnOffManager.isScreenOnEnabled();
            keep_alive_switch.setChecked(isScreenOnEnabled);
            keepAliveSwitch(isScreenOnEnabled);
            screenOnOffManager.applyScreenOnBasedOnView(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            screenOnOffManager.disableScreenOn();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateThemeText(boolean isDarkMode) {
        try {
            if (isDarkMode) {
                theme_switch_text.setText("Dark Mode");
                Drawable darkModeIcon = getResources().getDrawable(R.drawable.ic_night_mode, null);
                Drawable wrappedDarkModeIcon = addDrawableMargin(darkModeIcon, 16);
                theme_switch_text.setCompoundDrawablesWithIntrinsicBounds(wrappedDarkModeIcon, null, null, null);
                theme_switch_text.setCompoundDrawableTintList(ColorStateList.valueOf(getResources().getColor(R.color.white, null)));
                switch_dark_mode.setTrackTintList(ColorStateList.valueOf(getResources().getColor(R.color.primaryColor)));
                switch_dark_mode.setThumbTintList(ColorStateList.valueOf(getResources().getColor(R.color.primaryColor)));
            } else {
                theme_switch_text.setText("Light Mode");
                Drawable lightModeIcon = getResources().getDrawable(R.drawable.ic_light_mode, null);
                Drawable wrappedLightModeIcon = addDrawableMargin(lightModeIcon, 16);
                theme_switch_text.setCompoundDrawablesWithIntrinsicBounds(wrappedLightModeIcon, null, null, null);
                theme_switch_text.setCompoundDrawableTintList(ColorStateList.valueOf(getResources().getColor(R.color.black, null)));
                switch_dark_mode.setTrackTintList(ColorStateList.valueOf(getResources().getColor(R.color.gray)));
                switch_dark_mode.setThumbTintList(ColorStateList.valueOf(getResources().getColor(R.color.gray)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Drawable addDrawableMargin(Drawable drawable, int marginInPx) {
        try {
            return new InsetDrawable(drawable, 0, 0, marginInPx, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return drawable;
        }
    }

    private void notificationSwitch(boolean isOn) {
        try {
            if (isOn) {
                notification_swtich.setTrackTintList(ColorStateList.valueOf(getResources().getColor(R.color.primaryColor)));
                notification_swtich.setThumbTintList(ColorStateList.valueOf(getResources().getColor(R.color.primaryColor)));
                permission.notificationPermission(true);
            } else {
                notification_swtich.setTrackTintList(ColorStateList.valueOf(getResources().getColor(R.color.gray)));
                notification_swtich.setThumbTintList(ColorStateList.valueOf(getResources().getColor(R.color.gray)));
                permission.notificationPermission(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void gpsSwitch(boolean isOn) {
        try {
            if (isOn) {
                gps_switch.setTrackTintList(ColorStateList.valueOf(getResources().getColor(R.color.primaryColor)));
                gps_switch.setThumbTintList(ColorStateList.valueOf(getResources().getColor(R.color.primaryColor)));
                permission.locationPermission(true);
            } else {
                gps_switch.setTrackTintList(ColorStateList.valueOf(getResources().getColor(R.color.gray)));
                gps_switch.setThumbTintList(ColorStateList.valueOf(getResources().getColor(R.color.gray)));
                permission.locationPermission(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void smsSwitch(boolean isOn) {
        try {
            if (isOn) {
                sms_switch.setTrackTintList(ColorStateList.valueOf(getResources().getColor(R.color.primaryColor)));
                sms_switch.setThumbTintList(ColorStateList.valueOf(getResources().getColor(R.color.primaryColor)));
                permission.smsPermission(true);
            } else {
                sms_switch.setTrackTintList(ColorStateList.valueOf(getResources().getColor(R.color.gray)));
                sms_switch.setThumbTintList(ColorStateList.valueOf(getResources().getColor(R.color.gray)));
                permission.smsPermission(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void keepAliveSwitch(boolean isOn) {
        try {
            if (isOn) {
                screenOnOffManager.enableScreenOn();
                keep_alive_switch.setTrackTintList(ColorStateList.valueOf(getResources().getColor(R.color.primaryColor)));
                keep_alive_switch.setThumbTintList(ColorStateList.valueOf(getResources().getColor(R.color.primaryColor)));
                keep_alive_switch_text.setText("Screen On");
            } else {
                screenOnOffManager.disableScreenOn();
                keep_alive_switch.setTrackTintList(ColorStateList.valueOf(getResources().getColor(R.color.gray)));
                keep_alive_switch.setThumbTintList(ColorStateList.valueOf(getResources().getColor(R.color.gray)));
                keep_alive_switch_text.setText("Screen Off");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void configBtn() {
        try {
            config.setOnClickListener(v -> {
                try {
                    ConfigModal configModal = new ConfigModal(getContext());
                    configModal.openConfigModal();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setDriverDetails() {
        try {
            LoginManager loginManager = new LoginManager(getContext());
            loginManager.getProfile(new LoginManager.ProfileCallback() {
                @Override
                public void onSuccess(UserProfileResponse userProfileResponse) {
                    try {
                        if (userProfileResponse != null) {
                            driver_email.setText(userProfileResponse.getEmail());
                            driver_name.setText(userProfileResponse.getFullname());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(String errorMessage) {
                    try {
                        // Optionally show error to user
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}