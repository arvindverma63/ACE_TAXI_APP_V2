package com.app.ace_taxi_v2.Activity;


import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.app.ace_taxi_v2.Helper.DeviceMode;
import com.app.ace_taxi_v2.R;


public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);
        DeviceMode.init(this);
        int navId = -1;
        int jobId = -1;
        String message = "";
        String passenger = "";
        String datetime = "";
        String guid = "";
        String body = "";

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
                }else if("passenger".equals(key.toString()) && value!= null){
                    passenger = value.toString();
                }else if("datetime".equals(key.toString()) && value!=null){
                    datetime = value.toString();
                }else if("guid".equals(key.toString()) && value!= null){
                    guid = value.toString();
                }else if("body".equals(key.toString()) && value!=null){
                     body = value.toString();
                }
            }
        }
        Log.d("MainActivity navId notification : ",navId+"");

        int finalNavId = navId;
        int finalJobId = jobId;
        String finalMessage = message;
        String finalPassenger = passenger;
        String finalDatetime = datetime;
        String finalGuid = guid;
        String finalBody = body;
        new Handler().postDelayed(() -> {
                Intent homeIntent = new Intent(SplashScreenActivity.this, HomeActivity.class);
                homeIntent.putExtra("navId", finalNavId);
                homeIntent.putExtra("jobId", finalJobId);
                homeIntent.putExtra("message", finalMessage);
                homeIntent.putExtra("passenger", finalPassenger);
                homeIntent.putExtra("datetime", finalDatetime);
                homeIntent.putExtra("body", finalBody);
                homeIntent.putExtra("guid", finalGuid);
                startActivity(homeIntent);
            finish();
        }, 1000);
    }
}
