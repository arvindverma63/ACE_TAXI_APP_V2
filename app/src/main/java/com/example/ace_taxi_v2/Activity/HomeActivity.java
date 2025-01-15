package com.example.ace_taxi_v2.Activity;

import android.content.ContextWrapper;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.ace_taxi_v2.Fragments.HomeFragment;
import com.example.ace_taxi_v2.R;
import com.google.android.material.navigation.NavigationView;

public class HomeActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;
    private ImageView ham_menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

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

        // Set up ActionBarDrawerToggle
        toggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar, // Pass the toolbar here
                R.string.navigation_open,
                R.string.navigation_close
        );

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
    }

    private void handleMenuClick(MenuItem item) {
        int id = item.getItemId();

        // Load fragments based on menu item selection
        Fragment selectedFragment = null;

        if (id == R.id.nav_home) {
            selectedFragment = new HomeFragment();
        } else if (id == R.id.nav_activity) {
            // Replace this with your "Today" fragment
            showToast("Today selected");
        } else if (id == R.id.nav_future) {
            // Replace this with your "Future" fragment
            showToast("Future selected");
        } else if (id == R.id.nav_history) {
            // Replace this with your "History" fragment
            showToast("History selected");
        } else if (id == R.id.nav_earning) {
            // Replace this with your "Earnings" fragment
            showToast("Earnings selected");
        } else if (id == R.id.nav_settings) {
            // Replace this with your "Settings" fragment
            showToast("Settings selected");
        } else if (id == R.id.nav_logout) {
            showToast("Logout selected");
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
    public void showHamMenu(View view) {
        // Create the PopupMenu and inflate the menu
        ContextThemeWrapper themeWrapper = new ContextThemeWrapper(this,R.style.CustomPopupMenu);
        PopupMenu popupMenu = new PopupMenu(themeWrapper, view);
        MenuInflater menuInflater = popupMenu.getMenuInflater();
        menuInflater.inflate(R.menu.toolbar_menu, popupMenu.getMenu());

        // Set the listener for menu item clicks
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                // Ensure only one item is checked at a time
                for (int i = 0; i < popupMenu.getMenu().size(); i++) {
                    popupMenu.getMenu().getItem(i).setChecked(false);
                    break;
                }

                // Check the selected item
                menuItem.setChecked(true);

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
            }
        });

        // Show the popup menu
        popupMenu.show();
    }


}
