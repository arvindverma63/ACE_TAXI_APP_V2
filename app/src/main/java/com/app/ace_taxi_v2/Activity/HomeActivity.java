// HomeActivity.java
package com.app.ace_taxi_v2.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.app.ace_taxi_v2.Activity.HomeActivityHelper.BaseActivity;
import com.app.ace_taxi_v2.Activity.HomeActivityHelper.NavigationHandler;
import com.app.ace_taxi_v2.Activity.HomeActivityHelper.NotificationHandler;
import com.app.ace_taxi_v2.Components.ShiftChangeModal;
import com.app.ace_taxi_v2.Fragments.HomeFragment;
import com.app.ace_taxi_v2.Fragments.SettingFragment;
import com.app.ace_taxi_v2.Logic.Service.BackgroundPermissionHelper;
import com.app.ace_taxi_v2.Logic.SessionManager;
import com.app.ace_taxi_v2.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import io.sentry.android.core.SentryAndroid;

public class HomeActivity extends BaseActivity {
    private BottomNavigationView bottomNavigationView;
    private ImageView hamMenu, phoneIcon, messageIcon;
    private NotificationHandler notificationHandler;
    public BackgroundPermissionHelper permissionHelper;
    private NavigationHandler navigationHandler;
    private int tapCount = 0;
    private static final int TAP_THRESHOLD = 2;
    private static final int TIMEOUT = 2000;
    private final Handler handlerBack = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initializeViews();
        setupSession();
        initializeHelpers();
        setupSentry();
        setupToolbar();
        setupNavigation();
        setupClickListeners();

        if (savedInstanceState == null) {
            navigationHandler.loadFragment(new HomeFragment());
            navigationHandler.setSelectedItem(R.id.nav_home);
        }
    }

    private void initializeViews() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        hamMenu = findViewById(R.id.ham_menu);
        phoneIcon = findViewById(R.id.phone_icon);
        messageIcon = findViewById(R.id.message_icon);

        notificationHandler = new NotificationHandler(this,
                findViewById(R.id.notificationIcon),
                findViewById(R.id.notificationCount));
    }

    private void setupSession() {
        SessionManager sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    private void initializeHelpers() {
        permissionHelper = new BackgroundPermissionHelper(this);
        permissionHelper.requestLocationPermissions();
    }

    private void setupSentry() {
        SentryAndroid.init(this, options -> {
            options.setDsn("https://aa346bb12052028e902fac3576466b52@o4508856621858816.ingest.us.sentry.io/4508856623038464");
            options.setBeforeSend((event, hint) ->
                    event.getLevel() == io.sentry.SentryLevel.DEBUG ? null : event);
        });
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(""); // Using custom title from XML
        }
    }

    private void setupNavigation() {
        navigationHandler = new NavigationHandler(this, bottomNavigationView);
    }

    private void setupClickListeners() {
        hamMenu.setOnClickListener(v -> new ShiftChangeModal(this, getSupportFragmentManager()).openModal());

        phoneIcon.setOnClickListener(v -> {
            // Add phone call functionality here
            Toast.makeText(this, "Phone icon clicked", Toast.LENGTH_SHORT).show();
        });

        messageIcon.setOnClickListener(v -> {
            // Add messaging functionality here
            Toast.makeText(this, "Message icon clicked", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_logout) {
            navigationHandler.handleLogout();
            return true;
        } else if (itemId == R.id.action_settings) {
            loadFragment(new SettingFragment());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null) // Optional: adds fragment to back stack
                .commit();
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            return;
        }

        if (!(currentFragment instanceof HomeFragment)) {
            navigationHandler.loadFragment(new HomeFragment());
            navigationHandler.setSelectedItem(R.id.nav_home);
            return;
        }

        tapCount++;
        if (tapCount == TAP_THRESHOLD) {
            finishAffinity();
        } else {
            Toast.makeText(this, "Tap again to exit", Toast.LENGTH_SHORT).show();
            handlerBack.postDelayed(() -> tapCount = 0, TIMEOUT);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        notificationHandler.startNotificationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        notificationHandler.stopNotificationUpdates();
    }
}