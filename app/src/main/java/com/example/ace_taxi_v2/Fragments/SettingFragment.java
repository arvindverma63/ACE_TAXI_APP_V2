package com.example.ace_taxi_v2.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
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
import android.widget.Switch;
import android.widget.TextView;

import com.example.ace_taxi_v2.Components.CustomDialog;
import com.example.ace_taxi_v2.R;
import com.google.android.material.appbar.MaterialToolbar;

public class SettingFragment extends Fragment {

    private static final String PREF_NAME = "app_preferences";
    private static final String KEY_DARK_MODE = "dark_mode";
    private TextView theme_switch_text;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        theme_switch_text = view.findViewById(R.id.theme_switch_text);

        MaterialToolbar toolbar = view.findViewById(R.id.toolbar_header);
        toolbar.setNavigationOnClickListener(v -> {
            Fragment selectedFragment = new ProfileFragment();
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
            theme_switch_text.setCompoundDrawablesWithIntrinsicBounds(darkModeIcon, null, null, null);
            theme_switch_text.setCompoundDrawableTintList(ColorStateList.valueOf(getResources().getColor(R.color.white, null)));
        } else {
            theme_switch_text.setText("Light Mode");
            Drawable lightModeIcon = getResources().getDrawable(R.drawable.ic_light_mode, null);
            theme_switch_text.setCompoundDrawablesWithIntrinsicBounds(lightModeIcon, null, null, null);
            theme_switch_text.setCompoundDrawableTintList(ColorStateList.valueOf(getResources().getColor(R.color.black, null)));
        }

    }
}
