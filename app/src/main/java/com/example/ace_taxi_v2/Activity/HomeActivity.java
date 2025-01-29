package com.example.ace_taxi_v2.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.ace_taxi_v2.Fragments.AvailabilityFragment;
import com.example.ace_taxi_v2.Fragments.HomeFragment;
import com.example.ace_taxi_v2.Fragments.JobFragment;
import com.example.ace_taxi_v2.Fragments.ProfileFragment;
import com.example.ace_taxi_v2.Fragments.ReportFragment;
import com.example.ace_taxi_v2.Fragments.ReportPageFragment;
import com.example.ace_taxi_v2.Fragments.SettingFragment;
import com.example.ace_taxi_v2.JobModals.JobModal;
import com.example.ace_taxi_v2.Logic.JobApi.GetBookingById;
import com.example.ace_taxi_v2.Logic.LoginManager;
import com.example.ace_taxi_v2.Logic.NotificationSessionManager;
import com.example.ace_taxi_v2.Logic.Service.NotificationModalSession;
import com.example.ace_taxi_v2.Logic.SessionManager;
import com.example.ace_taxi_v2.Models.NotificationModel;
import com.example.ace_taxi_v2.Models.UserProfileResponse;
import com.example.ace_taxi_v2.R;
import com.google.android.material.navigation.NavigationView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;
    private ImageView ham_menu;
    private Toolbar toolbar_menu;
    public ImageView notificationIcon;
    public TextView notificationCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        SessionManager sessionManager = new SessionManager(this);
        if(!sessionManager.isLoggedIn()){
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(intent);
        }
        // Load dark mode preference
        SharedPreferences sharedPreferences = getSharedPreferences("app_preferences", MODE_PRIVATE);
        boolean isDarkMode = sharedPreferences.getBoolean("dark_mode", false);



        // Force the app to follow the user's preference
        AppCompatDelegate.setDefaultNightMode(isDarkMode
                ? AppCompatDelegate.MODE_NIGHT_YES
                : AppCompatDelegate.MODE_NIGHT_NO);

        // Set up the Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        // Initialize DrawerLayout and NavigationView
        ham_menu = findViewById(R.id.ham_menu);
        ham_menu.setOnClickListener(view -> {
            showHamMenu(view);
        });
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        notificationIcon = findViewById(R.id.notificationIcon);
        notificationCount = findViewById(R.id.notificationCount);

        // Set up ActionBarDrawerToggle
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_open, R.string.navigation_close);

        // Attach toggle to DrawerLayout
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Set default fragment (HomeFragment)
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
            navigationView.setCheckedItem(R.id.nav_home);
        }

        // Handle NavigationView item clicks
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                handleMenuClick(item);
                return true;
            }
        });

        navigationHeader();
        notificationIcon();
    }

    private void handleMenuClick(MenuItem item) {
        int id = item.getItemId();

        // Load fragments based on menu item selection
        Fragment selectedFragment = null;

        if (id == R.id.nav_home) {
            selectedFragment = new HomeFragment();
        } else if (id == R.id.nav_job) {
           selectedFragment = new JobFragment();
        } else if (id == R.id.nav_availability) {
            selectedFragment = new AvailabilityFragment();
        } else if (id == R.id.nav_report) {
            selectedFragment = new ReportPageFragment();
        } else if (id == R.id.nav_profile) {
           selectedFragment = new ProfileFragment();
        } else if (id == R.id.nav_settings) {
            selectedFragment = new SettingFragment();
        } else if (id == R.id.nav_logout) {
            SessionManager sessionManager = new SessionManager(this);
            sessionManager.clearSession();
            Intent intent = new Intent(HomeActivity.this,LoginActivity.class);
            startActivity(intent);
        }

        // Load the selected fragment
        if (selectedFragment != null) {
            loadFragment(selectedFragment);
        }

        // Close the drawer after item selection
        drawerLayout.closeDrawers();
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    @SuppressLint("ResourceType")
    public void showHamMenu(View view) {
        // Create the PopupMenu and inflate the menu
        ContextThemeWrapper themeWrapper = new ContextThemeWrapper(this, R.style.CustomPopupMenu);
        PopupMenu popupMenu = new PopupMenu(themeWrapper, view);
        MenuInflater menuInflater = popupMenu.getMenuInflater();

        // Inflate the menu resource
        menuInflater.inflate(R.menu.toolbar_menu, popupMenu.getMenu());

        // Set the listener for menu item clicks
        popupMenu.setOnMenuItemClickListener(menuItem -> {
            // Uncheck all items and reset icons to unchecked state
            for (int i = 0; i < popupMenu.getMenu().size(); i++) {
                MenuItem item = popupMenu.getMenu().getItem(i);
                item.setChecked(false); // Uncheck all items
                item.setIcon(R.drawable.ic_radio_button_unchecked); // Default unchecked icon
            }

            // Check the selected item and set its icon
            menuItem.setChecked(true);
            menuItem.setIcon(R.drawable.ic_radio_button_checked); // Checked icon

            // Perform actions based on the selected item
            if (menuItem.getItemId() == R.id.start_shift) {
                Toast.makeText(getApplicationContext(), "Start Shift selected", Toast.LENGTH_SHORT).show();
            } else if (menuItem.getItemId() == R.id.finish_shift) {
                Toast.makeText(getApplicationContext(), "Finish Shift selected", Toast.LENGTH_SHORT).show();
            } else if (menuItem.getItemId() == R.id.on_break) {
                Toast.makeText(getApplicationContext(), "On Break selected", Toast.LENGTH_SHORT).show();
            } else if (menuItem.getItemId() == R.id.finish_break) {
                Toast.makeText(getApplicationContext(), "Finish Break selected", Toast.LENGTH_SHORT).show();
            } else if (menuItem.getItemId() == R.id.rank_pickup) {
                Toast.makeText(getApplicationContext(), "Rank Pickup selected", Toast.LENGTH_SHORT).show();
            }

            return true;
        });

        // Show the popup menu
        popupMenu.show();
    }

    public void navigationHeader(){
        View headerView = navigationView.getHeaderView(0);
        TextView username = headerView.findViewById(R.id.user_name);
        TextView useremail = headerView.findViewById(R.id.user_email);

        LoginManager loginManager = new LoginManager(this);
        loginManager.getProfile(new LoginManager.ProfileCallback() {
            @Override
            public void onSuccess(UserProfileResponse userProfileResponse) {
                username.setText(userProfileResponse.getFullname());
                useremail.setText(userProfileResponse.getEmail());
                Toast.makeText(HomeActivity.this,"userprofile getting successfullly",Toast.LENGTH_LONG);
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(HomeActivity.this,"An error accur while fetching profile",Toast.LENGTH_LONG);
            }
        });
    }


    public void notificationIcon() {
        NotificationModalSession notificationModalSession = new NotificationModalSession(this);
        String navid = notificationModalSession.getLatestNavId();
        String jobid = notificationModalSession.getLatestJobId();

        Log.d("NotificationIcon", "Nav ID: " + navid);
        Log.d("NotificationIcon", "Job ID: " + jobid);


        int count = notificationModalSession.getNotificationCount();
        if (count > 0) {
            notificationCount.setText(String.valueOf(count));
            notificationCount.setVisibility(View.VISIBLE);
        } else {
            notificationCount.setVisibility(View.GONE);
        }

        notificationIcon.setOnClickListener(view -> {
            if (navid == null || jobid == null) {
                Log.w("NotificationIcon", "Nav ID or Job ID is null. Navigation skipped.");
                return;
            }

            Intent intent = new Intent(this, NotificationModalActivity.class);
            intent.putExtra("navId", navid);
            intent.putExtra("jobid", jobid);

            notificationModalSession.clearNotificationCount();
            notificationCount.setVisibility(View.GONE);

            startActivity(intent);
        });
    }






}
