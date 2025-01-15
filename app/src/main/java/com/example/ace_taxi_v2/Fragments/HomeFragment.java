package com.example.ace_taxi_v2.Fragments;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.example.ace_taxi_v2.R;

public class HomeFragment extends Fragment {

    private Switch location_switch;
    private TextView online_status_label;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        location_switch = view.findViewById(R.id.online_toggle);
        online_status_label = view.findViewById(R.id.online_status_label);
        location_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    location_switch.setTrackTintList(ColorStateList.valueOf(getResources().getColor(R.color.primaryColor)));
                    location_switch.setThumbTintList(ColorStateList.valueOf(getResources().getColor(R.color.primaryColor)));
                    online_status_label.setText("You Are Online");
                } else {
                    location_switch.setTrackTintList(ColorStateList.valueOf(getResources().getColor(R.color.gray)));
                    location_switch.setThumbTintList(ColorStateList.valueOf(getResources().getColor(R.color.gray)));
                    online_status_label.setText("You Are Offline");
                }
            }
        });

        return view;
    }
}
