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
import com.app.ace_taxi_v2.R;
import com.app.ace_taxi_v2.SettingsPermission.CheckPermission;
import com.app.ace_taxi_v2.SettingsPermission.Permission;
import com.google.android.material.appbar.MaterialToolbar;

public class SettingFragment extends Fragment {

    private static final String PREF_NAME = "app_preferences";
    private static final String KEY_DARK_MODE = "dark_mode";
    private TextView theme_switch_text,gps_switch_text,notification_switch_text,url_text,keep_alive_switch_text;
    private Switch switch_dark_mode,notification_swtich,gps_switch,sms_switch,keep_alive_switch;
    public Button config;
    Permission permission;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        theme_switch_text = view.findViewById(R.id.theme_switch_text);
        switch_dark_mode = view.findViewById(R.id.switch_dark_mode);
        notification_swtich = view.findViewById(R.id.notification_swtich);
        gps_switch = view.findViewById(R.id.gps_switch);
        sms_switch = view.findViewById(R.id.sms_switch);
        keep_alive_switch = view.findViewById(R.id.keep_alive_switch);
        config = view.findViewById(R.id.config);
        permission = new Permission(getContext());

        notification_switch_text = view.findViewById(R.id.notification_switch_text);
        gps_switch_text = view.findViewById(R.id.gps_switch_text);
        keep_alive_switch_text = view.findViewById(R.id.keep_alive_switch_text);
        url_text = view.findViewById(R.id.url_text);

        CheckPermission checkPermission = new CheckPermission(getContext());
        checkPermission.notificationPermission(notification_swtich);
        checkPermission.gpsPermission(gps_switch);


        notification_swtich.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                notificationSwitch(b);
            }
        });
        gps_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                gpsSwitch(b);
            }
        });
        sms_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                smsSwitch(b);
            }
        });
        keep_alive_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                keepAliveSwitch(b);
            }
        });



        MaterialToolbar toolbar = view.findViewById(R.id.toolbar_header);
        toolbar.setNavigationOnClickListener(v -> {
            Fragment selectedFragment = new HomeFragment();
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, selectedFragment);
            fragmentTransaction.addToBackStack(null); // Add to back stack if needed
            fragmentTransaction.commit();
        });

        // Initialize the switch
        Switch switchDarkMode = view.findViewById(R.id.switch_dark_mode);

        // Load dark mode preference
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        boolean isDarkMode = sharedPreferences.getBoolean(KEY_DARK_MODE, false);

        // Set initial state of the switch
        switchDarkMode.setChecked(isDarkMode);
        updateThemeText(isDarkMode); // Update text and icon on load

        // Handle switch toggle
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            AppCompatDelegate.setDefaultNightMode(isChecked
                    ? AppCompatDelegate.MODE_NIGHT_YES
                    : AppCompatDelegate.MODE_NIGHT_NO);


            // Update theme text and icon
            updateThemeText(isChecked);

            // Save the preference
            sharedPreferences.edit()
                    .putBoolean(KEY_DARK_MODE, isChecked)
                    .apply();
        });

        configBtn();
        return view;
    }

    /**
     * Updates the theme_switch_text TextView based on the theme.
     *
     * @param isDarkMode Whether dark mode is enabled.
     */
    private void updateThemeText(boolean isDarkMode) {
        if (isDarkMode) {
            theme_switch_text.setText("Dark Mode");
            Drawable darkModeIcon = getResources().getDrawable(R.drawable.ic_night_mode, null);
            Drawable wrappedDarkModeIcon = addDrawableMargin(darkModeIcon, 16); // 16px margin
            theme_switch_text.setCompoundDrawablesWithIntrinsicBounds(wrappedDarkModeIcon, null, null, null);
            theme_switch_text.setCompoundDrawableTintList(ColorStateList.valueOf(getResources().getColor(R.color.white, null)));
            switch_dark_mode.setTrackTintList(ColorStateList.valueOf(getResources().getColor(R.color.primaryColor)));
            switch_dark_mode.setThumbTintList(ColorStateList.valueOf(getResources().getColor(R.color.primaryColor)));
        } else {
            theme_switch_text.setText("Light Mode");
            Drawable lightModeIcon = getResources().getDrawable(R.drawable.ic_light_mode, null);
            Drawable wrappedLightModeIcon = addDrawableMargin(lightModeIcon, 16); // 16px margin
            theme_switch_text.setCompoundDrawablesWithIntrinsicBounds(wrappedLightModeIcon, null, null, null);
            theme_switch_text.setCompoundDrawableTintList(ColorStateList.valueOf(getResources().getColor(R.color.black, null)));
            switch_dark_mode.setTrackTintList(ColorStateList.valueOf(getResources().getColor(R.color.gray)));
            switch_dark_mode.setThumbTintList(ColorStateList.valueOf(getResources().getColor(R.color.gray)));
        }
    }

    // Helper method to add margin to the drawable
    private Drawable addDrawableMargin(Drawable drawable, int marginInPx) {
        InsetDrawable insetDrawable = new InsetDrawable(drawable, 0, 0, marginInPx, 0);
        return insetDrawable;
    }


    private void notificationSwitch(boolean isOn){
        if(isOn){
            notification_swtich.setTrackTintList(ColorStateList.valueOf(getResources().getColor(R.color.primaryColor)));
            notification_swtich.setThumbTintList(ColorStateList.valueOf(getResources().getColor(R.color.primaryColor)));
            permission.notificationPermission(true);

        }else{
            notification_swtich.setTrackTintList(ColorStateList.valueOf(getResources().getColor(R.color.gray)));
            notification_swtich.setThumbTintList(ColorStateList.valueOf(getResources().getColor(R.color.gray)));
            permission.notificationPermission(false);
        }
    }
    private void gpsSwitch(boolean isOn){
        if(isOn){
            gps_switch.setTrackTintList(ColorStateList.valueOf(getResources().getColor(R.color.primaryColor)));
            gps_switch.setThumbTintList(ColorStateList.valueOf(getResources().getColor(R.color.primaryColor)));
            permission.locationPermission(true);
        }else{
            gps_switch.setTrackTintList(ColorStateList.valueOf(getResources().getColor(R.color.gray)));
            gps_switch.setThumbTintList(ColorStateList.valueOf(getResources().getColor(R.color.gray)));
            permission.locationPermission(false);
        }
    }
    private void smsSwitch(boolean isOn){
        if(isOn){
            sms_switch.setTrackTintList(ColorStateList.valueOf(getResources().getColor(R.color.primaryColor)));
            sms_switch.setThumbTintList(ColorStateList.valueOf(getResources().getColor(R.color.primaryColor)));
            permission.smsPermission(true);
        }else{
            sms_switch.setTrackTintList(ColorStateList.valueOf(getResources().getColor(R.color.gray)));
            sms_switch.setThumbTintList(ColorStateList.valueOf(getResources().getColor(R.color.gray)));
            permission.smsPermission(false);
        }
    }
    private void keepAliveSwitch(boolean isOn){
        if(isOn){
            keep_alive_switch.setTrackTintList(ColorStateList.valueOf(getResources().getColor(R.color.primaryColor)));
            keep_alive_switch.setThumbTintList(ColorStateList.valueOf(getResources().getColor(R.color.primaryColor)));
        }else{
            keep_alive_switch.setTrackTintList(ColorStateList.valueOf(getResources().getColor(R.color.gray)));
            keep_alive_switch.setThumbTintList(ColorStateList.valueOf(getResources().getColor(R.color.gray)));
        }
    }
    private void configBtn(){
        config.setOnClickListener(v->{
            ConfigModal configModal = new ConfigModal(getContext());
            configModal.openConfigModal();
        });
    }
}
