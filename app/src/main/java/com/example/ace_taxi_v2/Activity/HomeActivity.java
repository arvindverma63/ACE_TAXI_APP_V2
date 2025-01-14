package com.example.ace_taxi_v2.Activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ace_taxi_v2.R;

public class HomeActivity extends AppCompatActivity {

    ImageView navIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        navIcon = findViewById(R.id.menu_icon); // Your menu icon (ImageView)

        navIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a PopupMenu
                PopupMenu popupMenu = new PopupMenu(HomeActivity.this, navIcon);
                popupMenu.getMenuInflater().inflate(R.menu.toolbar_menu, popupMenu.getMenu());

                // Handle menu item clicks using if-else
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int itemId = item.getItemId();

                        if (itemId == R.id.nav_home) {
                            Toast.makeText(HomeActivity.this, "Home clicked", Toast.LENGTH_SHORT).show();
                        } else if (itemId == R.id.nav_activity) {
                            Toast.makeText(HomeActivity.this, "Today clicked", Toast.LENGTH_SHORT).show();
                        } else if (itemId == R.id.nav_future) {
                            Toast.makeText(HomeActivity.this, "Future clicked", Toast.LENGTH_SHORT).show();
                        } else if (itemId == R.id.nav_history) {
                            Toast.makeText(HomeActivity.this, "History clicked", Toast.LENGTH_SHORT).show();
                        } else if (itemId == R.id.nav_earning) {
                            Toast.makeText(HomeActivity.this, "Earnings clicked", Toast.LENGTH_SHORT).show();
                        } else if (itemId == R.id.nav_statements) {
                            Toast.makeText(HomeActivity.this, "Statements clicked", Toast.LENGTH_SHORT).show();
                        } else if (itemId == R.id.nav_ava) {
                            Toast.makeText(HomeActivity.this, "Availability clicked", Toast.LENGTH_SHORT).show();
                        } else if (itemId == R.id.nav_profile) {
                            Toast.makeText(HomeActivity.this, "Profile clicked", Toast.LENGTH_SHORT).show();
                        } else if (itemId == R.id.nav_settings) {
                            Toast.makeText(HomeActivity.this, "Settings clicked", Toast.LENGTH_SHORT).show();
                        } else if (itemId == R.id.nav_logout) {
                            Toast.makeText(HomeActivity.this, "Logout clicked", Toast.LENGTH_SHORT).show();
                        } else {
                            return false; // If no match, do nothing
                        }
                        return true; // Indicate the event was handled
                    }
                });

                // Show the popup menu
                popupMenu.show();
            }
        });
    }
}
