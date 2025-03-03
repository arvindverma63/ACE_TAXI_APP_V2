package com.app.ace_taxi_v2.Activity;


import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.app.ace_taxi_v2.R;


public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);

        int navId = -1;
        int jobId = -1;
        String message = "";
        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Log.d("MainActivity: ", "Key: " + key + " Value: " + value);
                if ("NavId".equals(key.toString()) && value != null) {
                    try {
                        navId = Integer.parseInt(value.toString());
                    } catch (NumberFormatException e) {
                        Log.e(TAG, "Invalid navId: " + value, e);
                    }
                }else if("bookingId".equals(key.toString()) && value!=null){
                    try {
                        {
                            jobId = Integer.parseInt(value.toString());
                        }
                    } catch (NumberFormatException e) {
                        Log.e(TAG,"Invalid BookingId"+value,e);
                    }
                }else if("message".equals(key.toString()) && value != null){
                    message = value.toString();
                }
            }
        }
        Log.d("MainActivity navId notification : ",navId+"");

        int finalNavId = navId;
        int finalJobId = jobId;
        String finalMessage = message;
        new Handler().postDelayed(() -> {
                Intent homeIntent = new Intent(SplashScreenActivity.this, HomeActivity.class);
                homeIntent.putExtra("navId", finalNavId);
                homeIntent.putExtra("jobId", finalJobId);
                homeIntent.putExtra("message", finalMessage);
                startActivity(homeIntent);
            finish();
        }, 1000);
    }


}
