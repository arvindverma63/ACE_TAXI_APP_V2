package com.example.ace_taxi_v2.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ace_taxi_v2.R;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);

        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        if (title != null) {
            Intent notificationIntent = new Intent(this, NotificationModalActivity.class);
            notificationIntent.putExtra("title", title);
            startActivity(notificationIntent);
        }
        Intent incomingIntent = getIntent();
        Log.d("SplashScreenActivity", "Intent Extras: " + incomingIntent.getExtras());

        Log.d("SplashScreenActivity", "Title from notification: " + title);

        new Handler().postDelayed(() -> {
            if (title != null) {
                Intent notificationIntent = new Intent(SplashScreenActivity.this, NotificationModalActivity.class);
                notificationIntent.putExtra("title", title);
                startActivity(notificationIntent);
            } else {
                Intent homeIntent = new Intent(SplashScreenActivity.this, HomeActivity.class);
                startActivity(homeIntent);
            }
            finish();
        }, 1000);
    }


}
