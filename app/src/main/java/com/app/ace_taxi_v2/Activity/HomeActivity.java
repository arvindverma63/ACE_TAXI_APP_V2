package com.app.ace_taxi_v2.Activity;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.app.ace_taxi_v2.Activity.HomeActivityHelper.BaseActivity;
import com.app.ace_taxi_v2.Activity.HomeActivityHelper.NavigationHandler;
import com.app.ace_taxi_v2.Activity.HomeActivityHelper.NotificationHandler;
import com.app.ace_taxi_v2.Components.CustomToast;
import com.app.ace_taxi_v2.Components.ShiftChangeModal;
import com.app.ace_taxi_v2.Fragments.HomeFragment;
import com.app.ace_taxi_v2.Fragments.SettingFragment;
import com.app.ace_taxi_v2.Helper.DeviceMode;
import com.app.ace_taxi_v2.Helper.LogHelperLaravel;
import com.app.ace_taxi_v2.Instance.RetrofitClient;
import com.app.ace_taxi_v2.JobModals.BottomSheetDialogs;
import com.app.ace_taxi_v2.JobModals.JobModal;
import com.app.ace_taxi_v2.Logic.JobApi.GetBookingById;
import com.app.ace_taxi_v2.Logic.JobApi.JobOfferNotification;
import com.app.ace_taxi_v2.Logic.Service.BackgroundPermissionHelper;
import com.app.ace_taxi_v2.Logic.SessionManager;
import com.app.ace_taxi_v2.Models.Guid;
import com.app.ace_taxi_v2.Models.JobOfferNoticationResponse;
import com.app.ace_taxi_v2.R;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;

import io.sentry.android.core.SentryAndroid;

public class HomeActivity extends BaseActivity {
    private BottomNavigationView bottomNavigationView;
    private ImageView hamMenu;
    private static final int UPDATE_REQUEST_CODE = 123;
    private NotificationHandler notificationHandler;
    public BackgroundPermissionHelper permissionHelper;
    private NavigationHandler navigationHandler;
    private int tapCount = 0;
    private static final int TAP_THRESHOLD = 2;
    private static final int TIMEOUT = 2000;
    private final Handler handlerBack = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set status bar based on theme
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
            if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
                getWindow().setStatusBarColor(getResources().getColor(R.color.primaryDark));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    View decor = getWindow().getDecorView();
                    decor.setSystemUiVisibility(0); // Clear LIGHT_STATUS_BAR for white icons
                }
            } else {
                getWindow().setStatusBarColor(getResources().getColor(R.color.light_gray));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    View decor = getWindow().getDecorView();
                    decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                }
            }
        }

        if (!setContentViewSafely()) {
            return;
        }

        initializeViews();
        setupSession();
        initializeHelpers();
        setupToolbar();
        setupNavigation();
        setupClickListeners();
        getNotificationData();
        checkForAppUpdate();

        if (savedInstanceState == null) {
            loadInitialFragment();
        }
    }

    private boolean setContentViewSafely() {
        if (getResources() == null) {
            LogHelperLaravel.getInstance().e("HomeActivity", "Resources not available");
            Toast.makeText(this, "Failed to initialize UI", Toast.LENGTH_SHORT).show();
            finish();
            return false;
        }
        setContentView(R.layout.activity_home);
        DeviceMode.init(this);
        LogHelperLaravel.getInstance().i("HomeActivity", "Content view set successfully");
        return true;
    }

    private void initializeViews() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        hamMenu = findViewById(R.id.notificationIcon);
        if (bottomNavigationView == null || hamMenu == null) {
            LogHelperLaravel.getInstance().e("HomeActivity", "Failed to initialize views: bottomNavigationView or hamMenu is null");
            Toast.makeText(this, "UI initialization failed", Toast.LENGTH_SHORT).show();
            return;
        }
        notificationHandler = new NotificationHandler(this,
                findViewById(R.id.notificationIcon),
                findViewById(R.id.notificationCount));
        if (notificationHandler == null) {
            LogHelperLaravel.getInstance().e("HomeActivity", "Failed to initialize NotificationHandler");
        } else {
            LogHelperLaravel.getInstance().i("HomeActivity", "Views initialized successfully");
        }
    }

    private void setupSession() {
        SessionManager sessionManager = new SessionManager(this);
        String token = sessionManager.getToken();
        if (token == null) {
            LogHelperLaravel.getInstance().d("HomeActivity", "No JWT token found, redirecting to login");
            Toast.makeText(this, "Session expired, please log in", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        LogHelperLaravel.getInstance().i("HomeActivity", "JWT token: " + token + ", Base URL: " + RetrofitClient.getInstance().baseUrl());
    }

    private void initializeHelpers() {
        permissionHelper = new BackgroundPermissionHelper(this);
        if (permissionHelper != null) {
            permissionHelper.requestLocationPermissions();
            LogHelperLaravel.getInstance().i("HomeActivity", "BackgroundPermissionHelper initialized");
        } else {
            LogHelperLaravel.getInstance().e("HomeActivity", "Failed to initialize BackgroundPermissionHelper");
            Toast.makeText(this, "Permission setup failed", Toast.LENGTH_SHORT).show();
        }
    }


    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar == null) {
            LogHelperLaravel.getInstance().e("HomeActivity", "Toolbar not found");
            Toast.makeText(this, "Failed to set up toolbar", Toast.LENGTH_SHORT).show();
            return;
        }
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
            LogHelperLaravel.getInstance().i("HomeActivity", "Toolbar set up successfully");
        } else {
            LogHelperLaravel.getInstance().d("HomeActivity", "SupportActionBar not available");
        }
    }

    private void setupNavigation() {
        if (bottomNavigationView == null) {
            LogHelperLaravel.getInstance().e("HomeActivity", "Cannot set up navigation: BottomNavigationView is null");
            Toast.makeText(this, "Navigation setup failed", Toast.LENGTH_SHORT).show();
            return;
        }
        navigationHandler = new NavigationHandler(this, bottomNavigationView);
        if (navigationHandler != null) {
            LogHelperLaravel.getInstance().i("HomeActivity", "Navigation handler initialized");
        } else {
            LogHelperLaravel.getInstance().e("HomeActivity", "Failed to initialize NavigationHandler");
        }
    }

    private void setupClickListeners() {
        if (hamMenu != null) {
            hamMenu.setOnClickListener(v -> {
                ShiftChangeModal modal = new ShiftChangeModal(this, getSupportFragmentManager());
                if (modal != null) {
                    modal.openModal();
                    LogHelperLaravel.getInstance().i("HomeActivity", "Shift change modal opened");
                } else {
                    LogHelperLaravel.getInstance().e("HomeActivity", "Failed to create ShiftChangeModal");
                    Toast.makeText(this, "Failed to open shift modal", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            LogHelperLaravel.getInstance().e("HomeActivity", "HamMenu is null, cannot set click listener");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (menu == null) {
            LogHelperLaravel.getInstance().e("HomeActivity", "Menu is null");
            Toast.makeText(this, "Failed to create menu", Toast.LENGTH_SHORT).show();
            return false;
        }
        getMenuInflater().inflate(R.menu.options_menu, menu);
        LogHelperLaravel.getInstance().i("HomeActivity", "Options menu created");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item == null) {
            LogHelperLaravel.getInstance().e("HomeActivity", "MenuItem is null");
            Toast.makeText(this, "Error in menu selection", Toast.LENGTH_SHORT).show();
            return false;
        }
        int itemId = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    private void loadFragment(Fragment fragment) {
        if (fragment == null) {
            LogHelperLaravel.getInstance().e("HomeActivity", "Cannot load null fragment");
            Toast.makeText(this, "Failed to load fragment", Toast.LENGTH_SHORT).show();
            return;
        }
        if (getSupportFragmentManager() != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
            LogHelperLaravel.getInstance().i("HomeActivity", "Fragment loaded: " + fragment.getClass().getSimpleName());
        } else {
            LogHelperLaravel.getInstance().e("HomeActivity", "FragmentManager is null");
            Toast.makeText(this, "Failed to load fragment", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadInitialFragment() {
        if (navigationHandler != null) {
            navigationHandler.loadFragment(new HomeFragment());
            navigationHandler.setSelectedItem(R.id.nav_home);
            LogHelperLaravel.getInstance().i("HomeActivity", "Initial HomeFragment loaded");
        } else {
            LogHelperLaravel.getInstance().e("HomeActivity", "NavigationHandler is null, cannot load initial fragment");
            Toast.makeText(this, "Failed to load home screen", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            LogHelperLaravel.getInstance().i("HomeActivity", "Popped back stack");
            return;
        }
        if (!(currentFragment instanceof HomeFragment)) {
            if (navigationHandler != null) {
                navigationHandler.loadFragment(new HomeFragment());
                navigationHandler.setSelectedItem(R.id.nav_home);
                LogHelperLaravel.getInstance().i("HomeActivity", "Navigated to HomeFragment");
            } else {
                LogHelperLaravel.getInstance().e("HomeActivity", "NavigationHandler is null, cannot navigate to HomeFragment");
                Toast.makeText(this, "Navigation error", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        tapCount++;
        if (tapCount == TAP_THRESHOLD) {
            LogHelperLaravel.getInstance().i("HomeActivity", "App exit triggered");
            finishAffinity();
        } else {
            Toast.makeText(this, "Tap again to exit", Toast.LENGTH_SHORT).show();
            handlerBack.postDelayed(() -> tapCount = 0, TIMEOUT);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (notificationHandler != null) {
            notificationHandler.startNotificationUpdates();
            LogHelperLaravel.getInstance().i("HomeActivity", "Notification updates started");
        } else {
            LogHelperLaravel.getInstance().e("HomeActivity", "NotificationHandler is null in onResume");
            Toast.makeText(this, "Error resuming notifications", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (notificationHandler != null) {
            notificationHandler.stopNotificationUpdates();
            LogHelperLaravel.getInstance().i("HomeActivity", "Notification updates stopped");
        } else {
            LogHelperLaravel.getInstance().e("HomeActivity", "NotificationHandler is null in onPause");
            Toast.makeText(this, "Error pausing notifications", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("NewApi")
    private void getNotificationData() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            LogHelperLaravel.getInstance().i("HomeActivity", "Skipping notification data for pre-Oreo device");
            return;
        }
        Intent intent = getIntent();
        if (intent == null) {
            LogHelperLaravel.getInstance().d("HomeActivity", "Intent is null in getNotificationData");
            return;
        }
        int navId = -1;
        String message = intent.getStringExtra("message") != null ? intent.getStringExtra("message") : "";
        String passenger = "";
        String dateTime = intent.getStringExtra("datetime");
        String pickupAddress = intent.getStringExtra("pickupAddress");
        boolean accepted = intent.getBooleanExtra("accepted", false);
        boolean rejected = intent.getBooleanExtra("rejected", false);
        String guid = intent.getStringExtra("guid");
        String body = intent.getStringExtra("body");
        Log.d("guid", "guid: " + guid);

        Guid guid1 = new Guid(this);
        if (guid != null) {
            guid1.setGuid(guid);
            LogHelperLaravel.getInstance().i("HomeActivity", "GUID set: " + guid);
        } else {
            LogHelperLaravel.getInstance().d("HomeActivity", "GUID is null");
        }

        Bundle extras = intent.getExtras();
        if (extras != null) {
            Object navIdObj = extras.get("navId");
            if (navIdObj != null) {
                try {
                    navId = Integer.parseInt(navIdObj.toString());
                } catch (NumberFormatException e) {
                    LogHelperLaravel.getInstance().e("HomeActivity", "Invalid navId format: " + navIdObj);
                }
                passenger = extras.getString("passenger", "");
                message = extras.getString("message", message);
            }
        }

        Log.d("HomeActivity Intent Data", "NavId: " + navId + ", Message: " + message + ", DateTime: " + dateTime);

        if (guid != null) {
            JobOfferNotification jobOfferNotification = new JobOfferNotification(this);
            int finalNavId = navId;
            jobOfferNotification.getResponse(guid, new JobOfferNotification.jobOfferCallback() {
                @Override
                public void onJobOfferResponse(JobOfferNoticationResponse response) {
                    if (response == null || response.getBookingId() == null) {
                        LogHelperLaravel.getInstance().e("HomeActivity", "Invalid or null JobOfferNotification response");
                        Toast.makeText(HomeActivity.this, "Error processing notification", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    try {
                        int bookingId = Integer.parseInt(response.getBookingId());
                        Log.d("HomeActivity", "Booking ID: " + bookingId);
                        if (finalNavId == 1) {
                            GetBookingById getBookingById = new GetBookingById(HomeActivity.this);
                            if (getBookingById != null) {
                                getBookingById.getBookingDetails(bookingId);
                                LogHelperLaravel.getInstance().i("HomeActivity", "Fetching booking details for ID: " + bookingId);
                            } else {
                                LogHelperLaravel.getInstance().e("HomeActivity", "GetBookingById is null");
                            }
                        }
                    } catch (NumberFormatException e) {
                        LogHelperLaravel.getInstance().e("HomeActivity", "Invalid bookingId format: " + response.getBookingId());
                        Toast.makeText(HomeActivity.this, "Error processing booking ID", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(String errorMessage) {
                    LogHelperLaravel.getInstance().e("HomeActivity", "Job offer notification error: " + errorMessage);
                    new CustomToast(getApplicationContext()).showCustomErrorToast("No Internet Connection");
                }
            });
        }

        if (navId == 5 || navId == 6) {
            new JobModal(this).JobReadNotificationClick(message, dateTime);
            LogHelperLaravel.getInstance().i("HomeActivity", "Job read notification clicked, navId: " + navId);
        } else if (navId == 2) {
            new JobModal(this).jobUnallocated(-1, passenger, dateTime);
            LogHelperLaravel.getInstance().i("HomeActivity", "Job unallocated, navId: " + navId);
        } else if (navId == 3) {
            new JobModal(this).jobAmenedment(body, passenger, dateTime);
            LogHelperLaravel.getInstance().i("HomeActivity", "Job amendment, navId: " + navId);
        } else if (navId == 4) {
            new JobModal(this).jobCancel("-1", passenger, dateTime);
            LogHelperLaravel.getInstance().i("HomeActivity", "Job cancelled, navId: " + navId);
        }
        if (accepted) {
            new BottomSheetDialogs(this).openJobAccepted(passenger, pickupAddress);
            LogHelperLaravel.getInstance().i("HomeActivity", "Job accepted notification");
        }
        if (rejected) {
            new BottomSheetDialogs(this).openJobRejected();
            LogHelperLaravel.getInstance().i("HomeActivity", "Job rejected notification");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (permissionHelper != null) {
            permissionHelper.handlePermissionsResult(requestCode, permissions, grantResults);
            LogHelperLaravel.getInstance().i("HomeActivity", "Permissions result handled, requestCode: " + requestCode);
        } else {
            LogHelperLaravel.getInstance().e("HomeActivity", "PermissionHelper is null in onRequestPermissionsResult");
            Toast.makeText(this, "Permission handling failed", Toast.LENGTH_SHORT).show();
        }
    }

    public void updateShiftIcon() {
        LogHelperLaravel.getInstance().i("HomeActivity", "Updating shift icon (no-op)");
        // Add implementation if needed
    }

    private void checkForAppUpdate() {
        AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(this);
        if (appUpdateManager == null) {
            LogHelperLaravel.getInstance().e("HomeActivity", "AppUpdateManager is null");
            return;
        }
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        if (appUpdateInfoTask == null) {
            LogHelperLaravel.getInstance().e("HomeActivity", "AppUpdateInfoTask is null");
            return;
        }
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo == null) {
                LogHelperLaravel.getInstance().e("HomeActivity", "AppUpdateInfo is null");
                return;
            }
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                try {
                    appUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo,
                            AppUpdateType.IMMEDIATE,
                            this,
                            UPDATE_REQUEST_CODE);
                    LogHelperLaravel.getInstance().i("HomeActivity", "Starting app update flow");
                } catch (IntentSender.SendIntentException e) {
                    LogHelperLaravel.getInstance().e("HomeActivity", "App update failed: " + e.getMessage());
                    Toast.makeText(this, "Failed to start app update", Toast.LENGTH_SHORT).show();
                }
            } else {
                LogHelperLaravel.getInstance().i("HomeActivity", "No app update available");
            }
        }).addOnFailureListener(e -> {
            LogHelperLaravel.getInstance().e("HomeActivity", "App update check failed: " + e.getMessage());
        });
    }
}