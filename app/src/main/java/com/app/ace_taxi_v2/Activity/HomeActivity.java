package com.app.ace_taxi_v2.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.app.ace_taxi_v2.Activity.HomeActivityHelper.BaseActivity;
import com.app.ace_taxi_v2.Activity.HomeActivityHelper.NavigationHandler;
import com.app.ace_taxi_v2.Activity.HomeActivityHelper.NotificationHandler;
import com.app.ace_taxi_v2.Components.CustomToast;
import com.app.ace_taxi_v2.Components.ShiftChangeModal;
import com.app.ace_taxi_v2.Fragments.HomeFragment;
import com.app.ace_taxi_v2.Fragments.SettingFragment;
import com.app.ace_taxi_v2.Helper.DeviceMode;
import com.app.ace_taxi_v2.Instance.RetrofitClient;
import com.app.ace_taxi_v2.JobModals.BottomSheetDialogs;
import com.app.ace_taxi_v2.JobModals.JobModal;
import com.app.ace_taxi_v2.Logic.JobApi.GetBookingById;
import com.app.ace_taxi_v2.Logic.JobApi.JobOfferNotification;
import com.app.ace_taxi_v2.Logic.Service.BackgroundPermissionHelper;
import com.app.ace_taxi_v2.Logic.Service.ConfigSessionManager;
import com.app.ace_taxi_v2.Logic.SessionManager;
import com.app.ace_taxi_v2.Models.Guid;
import com.app.ace_taxi_v2.Models.JobOfferNoticationResponse;
import com.app.ace_taxi_v2.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;

import io.sentry.android.core.SentryAndroid;

public class HomeActivity extends BaseActivity {
    private BottomNavigationView bottomNavigationView;
    private ImageView hamMenu;
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int nightModeFlags =
                    getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

            if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
                // Night mode: dark background, white status bar icons
                getWindow().setStatusBarColor(getResources().getColor(R.color.primaryDark));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    View decor = getWindow().getDecorView();
                    // Clear the LIGHT_STATUS_BAR flag to get white icons
                    decor.setSystemUiVisibility(0);
                }
            } else {
                // Day mode: light background, dark status bar icons
                getWindow().setStatusBarColor(getResources().getColor(R.color.light_gray));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    View decor = getWindow().getDecorView();
                    decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                }
            }
        }
        try {
            setContentView(R.layout.activity_home);
            DeviceMode.init(this);
        } catch (Exception e) {
            Log.e("HomeActivity", "Error setting content view", e);
            return;
        }

        initializeViews();
        setupSession();
        initializeHelpers();
        setupSentry();
        setupToolbar();
        setupNavigation();
        setupClickListeners();
        getNotificationData();

        try {
            if (savedInstanceState == null) {
                navigationHandler.loadFragment(new HomeFragment());
                navigationHandler.setSelectedItem(R.id.nav_home);
            }
        } catch (Exception e) {
            Log.e("HomeActivity", "Error loading initial fragment", e);
        }
    }

    private void initializeViews() {
        try {
            bottomNavigationView = findViewById(R.id.bottom_navigation);
            hamMenu = findViewById(R.id.ham_menu);
//            phoneIcon = findViewById(R.id.phone_icon);
//            messageIcon = findViewById(R.id.message_icon);

            notificationHandler = new NotificationHandler(this,
                    findViewById(R.id.notificationIcon),
                    findViewById(R.id.notificationCount));
        } catch (Exception e) {
            Log.e("HomeActivity", "Error initializing views", e);
        }
    }

    private void setupSession() {
        try {
            SessionManager sessionManager = new SessionManager(this);
            Log.e("token jwt : ", " jwt_token: "+sessionManager.getToken()+" \n "+ RetrofitClient.getInstance().baseUrl());
            if (!sessionManager.isLoggedIn()) {
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            }
        } catch (Exception e) {
            Log.e("HomeActivity", "Error setting up session", e);
        }
    }

    private void initializeHelpers() {
        try {
            permissionHelper = new BackgroundPermissionHelper(this);
            permissionHelper.requestLocationPermissions();
        } catch (Exception e) {
            Log.e("HomeActivity", "Error initializing helpers", e);
        }
    }

    private void setupSentry() {
        try {
            SentryAndroid.init(this, options -> {
                options.setDsn("https://aa346bb12052028e902fac3576466b52@o4508856621858816.ingest.us.sentry.io/4508856623038464");
                options.setBeforeSend((event, hint) ->
                        event.getLevel() == io.sentry.SentryLevel.DEBUG ? null : event);
            });
        } catch (Exception e) {
            Log.e("HomeActivity", "Error setting up Sentry", e);
        }
    }

    private void setupToolbar() {
        try {
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(""); // Using custom title from XML
            }
        } catch (Exception e) {
            Log.e("HomeActivity", "Error setting up toolbar", e);
        }
    }

    private void setupNavigation() {
        try {
            navigationHandler = new NavigationHandler(this, bottomNavigationView);
        } catch (Exception e) {
            Log.e("HomeActivity", "Error setting up navigation", e);
        }
    }

    private void setupClickListeners() {
        try {
            hamMenu.setOnClickListener(v -> new ShiftChangeModal(this, getSupportFragmentManager()).openModal());
//            setupPhoneButton();
//            setupMessageButton();
        } catch (Exception e) {
            Log.e("HomeActivity", "Error setting up click listeners", e);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        try {
            getMenuInflater().inflate(R.menu.options_menu, menu);
            return true;
        } catch (Exception e) {
            Log.e("HomeActivity", "Error creating options menu", e);
            return false;
        }
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        try {
//            int itemId = item.getItemId();
//            if (itemId == R.id.action_logout) {
//                navigationHandler.handleLogout();
//                return true;
//            } else if (itemId == R.id.action_settings) {
//                loadFragment(new SettingFragment());
//                return true;
//            }
//            return super.onOptionsItemSelected(item);
//        } catch (Exception e) {
//            Log.e("HomeActivity", "Error handling options item selected", e);
//            return false;
//        }
//    }

    private void loadFragment(Fragment fragment) {
        try {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        } catch (Exception e) {
            Log.e("HomeActivity", "Error loading fragment", e);
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        try {
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
        } catch (Exception e) {
            Log.e("HomeActivity", "Error handling back press", e);
        }
    }

    @Override
    protected void onResume() {
        try {
            super.onResume();
            notificationHandler.startNotificationUpdates();
        } catch (Exception e) {
            Log.e("HomeActivity", "Error in onResume", e);
        }
    }

    @Override
    protected void onPause() {
        try {
            super.onPause();
            notificationHandler.stopNotificationUpdates();
        } catch (Exception e) {
            Log.e("HomeActivity", "Error in onPause", e);
        }
    }

    @SuppressLint("NewApi")
    private void getNotificationData() {
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                return; // Early return for pre-Oreo devices
            }

            Intent intent = getIntent();
            int navId = -1;
            String message = intent.getStringExtra("message") != null ? intent.getStringExtra("message") : "";
            String passenger = "";
            String dateTime = intent.getStringExtra("datetime");
            String pickupAddress = intent.getStringExtra("pickupAddress");
            boolean accepted = intent.getBooleanExtra("accepted", false);
            boolean rejected = intent.getBooleanExtra("rejected", false);
            String guid = intent.getStringExtra("guid");
            String body = intent.getStringExtra("body");
            Log.d("guid","guid: "+guid);
            Guid guid1 = new Guid(this);
            guid1.setGuid(guid);

            Bundle extras = intent.getExtras();
            if (extras != null) {
                try {
                    Object navIdObj = extras.get("navId");
                    if (navIdObj != null) {
                        navId = Integer.parseInt(navIdObj.toString());
                    }
                    passenger = extras.getString("passenger", "");
                    message = extras.getString("message", message);
                } catch (NumberFormatException e) {
                    Log.e("HomeActivity", "Invalid number format in intent extras", e);
                }
            }

            Log.d("HomeActivity Intent Data", "NavId: " + navId + ", Message: " + message + ", DateTime: " + dateTime);

            if (guid != null) {
                JobOfferNotification jobOfferNotification = new JobOfferNotification(this);
                int finalNavId = navId;
                jobOfferNotification.getResponse(guid, new JobOfferNotification.jobOfferCallback() {
                    @Override
                    public void onJobOfferResponse(JobOfferNoticationResponse response) {
                        try {
                            int bookingId = Integer.parseInt(response.getBookingId());
                            Log.d("HomeActivity", "Booking ID: " + bookingId);
                            if (finalNavId == 1) {
                                GetBookingById getBookingById = new GetBookingById(HomeActivity.this);
                                getBookingById.getBookingDetails(bookingId);
                            }
                        } catch (NumberFormatException e) {
                            Log.e("HomeActivity", "Invalid bookingId format", e);
                            Toast.makeText(HomeActivity.this, "Error processing booking ID", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Log.e("HomeActivity", "Job offer notification error: " + errorMessage);
                        new CustomToast(getApplicationContext()).showCustomErrorToast("No Internet Connection");
                    }
                });
            }

            if (navId == 5 || navId == 6) {
                new JobModal(this).JobReadNotificationClick(message, dateTime);
            }
            if (navId == 2) {
                new JobModal(this).jobUnallocated(-1, passenger, dateTime);
            }
            if (navId == 3) {
                new JobModal(this).jobAmenedment(body, passenger, dateTime);
            }
            if (navId == 4) {
                new JobModal(this).jobCancel("-1", passenger, dateTime);
            }
            if (accepted) {
                new BottomSheetDialogs(this).openJobAccepted(passenger, pickupAddress);
            }
            if (rejected) {
                new BottomSheetDialogs(this).openJobRejected();
            }
        } catch (Exception e) {
            Log.e("HomeActivity", "Error processing notification data", e);
            Toast.makeText(this, "Error processing notification", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        try {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            permissionHelper.handlePermissionsResult(requestCode, permissions, grantResults);
        } catch (Exception e) {
            Log.e("HomeActivity", "Error handling permission result", e);
        }
    }

    public void updateShiftIcon() {
        try {
            // Add implementation if needed
        } catch (Exception e) {
            Log.e("HomeActivity", "Error updating shift icon", e);
        }
    }
}