package com.app.ace_taxi_v2.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.app.ace_taxi_v2.Components.NotificationDialog;
import com.app.ace_taxi_v2.Components.ShiftChangeModal;
import com.app.ace_taxi_v2.Fragments.AvailabilityFragment;
import com.app.ace_taxi_v2.Fragments.BookingFragment;
import com.app.ace_taxi_v2.Fragments.HomeFragment;
import com.app.ace_taxi_v2.Fragments.JobFragment;
import com.app.ace_taxi_v2.Fragments.ProfileFragment;
import com.app.ace_taxi_v2.Fragments.ReportPageFragment;
import com.app.ace_taxi_v2.Fragments.SettingFragment;
import com.app.ace_taxi_v2.JobModals.JobModal;
import com.app.ace_taxi_v2.Logic.JobApi.GetBookingById;
import com.app.ace_taxi_v2.Logic.LoginManager;
import com.app.ace_taxi_v2.Logic.Service.BackgroundPermissionHelper;
import com.app.ace_taxi_v2.Logic.Service.BatteryOptimizationHelper;
import com.app.ace_taxi_v2.Logic.Service.ConfigSessionManager;
import com.app.ace_taxi_v2.Logic.Service.CurrentShiftStatus;
import com.app.ace_taxi_v2.Logic.Service.NotificationModalSession;
import com.app.ace_taxi_v2.Logic.SessionManager;
import com.app.ace_taxi_v2.Logic.UpdateDriverShiftApi;
import com.app.ace_taxi_v2.Models.UserProfileResponse;
import com.app.ace_taxi_v2.R;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import io.sentry.SentryLevel;
import io.sentry.android.core.SentryAndroid;

public class HomeActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;
    private ImageView ham_menu;
    private Toolbar toolbar_menu;
    public ImageView notificationIcon,phone_icon,message_icon;
    public TextView notificationCount;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final int INTERVAL = 5000; // 5 seconds
    private int tapCount = 0;
    private static final int TAP_THRESHOLD = 3; // Number of taps required to exit
    private static final int TIMEOUT = 2000; // 2 seconds timeout to reset count
    private Handler handlerBack = new Handler();
    public BackgroundPermissionHelper permissionHelper;
    public BatteryOptimizationHelper batteryHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        phone_icon = findViewById(R.id.phone_icon);
        message_icon = findViewById(R.id.message_icon);
        SessionManager sessionManager = new SessionManager(this);
        if(!sessionManager.isLoggedIn()){
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(intent);
        }
        permissionHelper = new BackgroundPermissionHelper(this);
        permissionHelper.requestLocationPermissions();
        batteryHelper = new BatteryOptimizationHelper(this);
        batteryHelper.showBatteryOptimizationDialog();
        SentryAndroid.init(this, options -> {
            options.setDsn("https://aa346bb12052028e902fac3576466b52@o4508856621858816.ingest.us.sentry.io/4508856623038464");
            // Add a callback that will be used before the event is sent to Sentry.
            // With this callback, you can modify the event or, when returning null, also discard the event.
            options.setBeforeSend((event, hint) -> {
                if (SentryLevel.DEBUG.equals(event.getLevel()))
                    return null;
                else
                    return event;
            });
        });
        // Load dark mode preference
        SharedPreferences sharedPreferences = getSharedPreferences("app_preferences", MODE_PRIVATE);
        boolean isDarkMode = sharedPreferences.getBoolean("dark_mode", false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getNotificationData();
        }

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

        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key)
                        ;
                Log.d("HomeActivity: ", "Key: " + key + " Value: " + value);
            }
        }
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                handleMenuClick(item);
                return true;
            }
        });
        navigationHeader();
        batteryOptimization();
        phoneBtn();
        messageBtn();
    }
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        if (!isTaskRoot()) {
            // If not at the root of the task, navigate back
            super.onBackPressed();
            return;
        }

        tapCount++;

        if (tapCount == 1) {
            // Open HomeActivity if it's not already open
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        } else if (tapCount == 2) {
            // Close the app on the second tap
            finishAffinity();
        }

        // Show toast for exit confirmation
        Toast.makeText(this, "Tap again to exit", Toast.LENGTH_SHORT).show();

        // Reset tap count after a timeout (2 seconds)
        handlerBack.postDelayed(() -> tapCount = 0, TIMEOUT);
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


    @SuppressLint("ResourceType")
    public void showHamMenu(View view) {
        ShiftChangeModal changeModal = new ShiftChangeModal(this,getSupportFragmentManager());
        changeModal.openModal();
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
    private final Runnable notificationRunnable = new Runnable() {
        @Override
        public void run() {
            runOnUiThread(() -> notificationIcon()); // Ensure UI updates on Main Thread
            handler.postDelayed(this, INTERVAL); // Repeat every 5 seconds
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        handler.post(notificationRunnable); // Start when fragment is visible
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(notificationRunnable); // Stop when fragment is not visible
    }

    public void notificationIcon() {
        NotificationModalSession notificationModalSession = new NotificationModalSession(this);
        String navid = notificationModalSession.getLatestNavId();
        String jobid = notificationModalSession.getLatestJobId();

        Log.d("NotificationIcon", "Nav ID: " + navid);
        Log.d("NotificationIcon", "Job ID: " + jobid);

        int count = notificationModalSession.getNotificationCount();


        runOnUiThread(() -> {

            if (count > 0) {
                notificationCount.setText(String.valueOf(count));
                notificationCount.setVisibility(View.VISIBLE);
            } else {
                notificationCount.setVisibility(View.GONE);
            }
        });

        notificationIcon.setOnClickListener(view -> {
            if (navid == null || jobid == null) {
                Log.w("NotificationIcon", "Nav ID or Job ID is null. Navigation skipped.");
                return;
            }

            NotificationDialog notificationDialog = new NotificationDialog(this);
            notificationDialog.openModal();

//            Intent intent = new Intent(this, NotificationModalActivity.class);
//            intent.putExtra("navId", navid);
//            intent.putExtra("jobid", jobid);
//
//            notificationModalSession.clearNotificationCount();
//            notificationCount.setVisibility(View.GONE);
//
//            startActivity(intent);
        });
    }

    public void batteryOptimization(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            if (!pm.isIgnoringBatteryOptimizations(getPackageName())) {
                Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionHelper.handlePermissionsResult(requestCode, permissions, grantResults);
    }

    public void phoneBtn() {
        phone_icon.setOnClickListener(v -> {
            ConfigSessionManager configSessionManager = new ConfigSessionManager(this);
            String phoneNumber = configSessionManager.getPhoneNumber();

            if (phoneNumber != null && !phoneNumber.isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + phoneNumber));
                v.getContext().startActivity(intent);
            } else {
                Toast.makeText(v.getContext(), "Phone number not found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void messageBtn() {
        message_icon.setOnClickListener(v -> {
            ConfigSessionManager configSessionManager = new ConfigSessionManager(this);
            String whatsappNumber = configSessionManager.getWhatsAppNumber();

            if (whatsappNumber != null && !whatsappNumber.isEmpty()) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("https://wa.me/" + whatsappNumber));
                    v.getContext().startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(v.getContext(), "WhatsApp not installed", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(v.getContext(), "WhatsApp number not found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void getNotificationData() {
        Intent intent = getIntent();
        int navId = -1, jobId = -1;
        String message = "", passenger = "";
        LocalDateTime dateTime = null; // Store parsed date-time

        Bundle extras = intent.getExtras();
        if (extras != null) {
            try {
                Object navIdObj = extras.get("navId");
                if (navIdObj != null) {
                    navId = Integer.parseInt(navIdObj.toString());
                }

                Object jobIdObj = extras.get("jobId");
                if (jobIdObj != null) {
                    jobId = Integer.parseInt(jobIdObj.toString());
                }

                if (extras.containsKey("passenger")) {
                    passenger = extras.getString("passenger", "");
                }

                if (extras.containsKey("datetime")) {
                    String dateString = extras.getString("datetime", "");
                    if (!dateString.isEmpty()) {
                        try {
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                            dateTime = LocalDateTime.parse(dateString, formatter);
                        } catch (DateTimeParseException e) {
                            Log.e("HomeActivity", "Invalid date format: " + dateString, e);
                        }
                    }
                }

                if (extras.containsKey("message")) {
                    message = extras.getString("message", "");
                }

            } catch (NumberFormatException e) {
                Log.e("HomeActivity", "Invalid number format in intent extras", e);
            }
        }

        Log.d("HomeActivity", "NavId: " + navId + ", JobId: " + jobId + ", Message: " + message + ", DateTime: " + dateTime);

        try {
            if (jobId > 0 && navId == 1) {
                new GetBookingById(this).getBookingDetails(jobId);
            }

            if (navId == 5 || navId == 6) {
                new JobModal(this).JobReadNotificationClick(message);
            }

            if (navId == 2) {
                new JobModal(this).jobUnallocated(jobId, passenger, dateTime != null ? dateTime.toString() : "");
            }
        } catch (Exception e) {
            Log.e("HomeActivity", "Error processing notification data", e);
        }
    }


}
