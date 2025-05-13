package com.app.ace_taxi_v2.Components;

import android.content.Context;
import android.os.Build;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.app.ace_taxi_v2.Fragments.BookingFragment;
import com.app.ace_taxi_v2.Fragments.SettingFragment;
import com.app.ace_taxi_v2.R;

public class HamMenu {
    private final Context context;
    private final FragmentActivity activity; // Required for fragment transactions

    public HamMenu(Context context, FragmentActivity activity) {
        this.activity = activity;
        this.context = context;
    }

    public void openMenu(ImageView sideMenu) {
        if (context == null || sideMenu == null) {
            throw new IllegalArgumentException("Context and sideMenu cannot be null");
        }

        PopupMenu popup = new PopupMenu(context, sideMenu);
        popup.getMenuInflater().inflate(R.menu.options_menu, popup.getMenu());

        // Show icons if supported
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            popup.setForceShowIcon(true);
        }

        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_settings) {
                replaceFragment(new SettingFragment()); // Replace with your SettingsFragment
                return true;
            } else if (item.getItemId() == R.id.action_shift) {
                ShiftChangeModal shiftChangeModal = new ShiftChangeModal(context, activity.getSupportFragmentManager());
                shiftChangeModal.openModal();
                return true;
            }else if(item.getItemId() == R.id.rank_pickup){
                replaceFragment(new BookingFragment());
                return true;
            }
            else {

                return false;
            }
        });

        popup.show();
    }

    private void replaceFragment(Fragment fragment) {
        try {
            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        } catch (Exception e) {
            Toast.makeText(context, "Failed to load fragment", Toast.LENGTH_LONG).show();
        }
    }
}