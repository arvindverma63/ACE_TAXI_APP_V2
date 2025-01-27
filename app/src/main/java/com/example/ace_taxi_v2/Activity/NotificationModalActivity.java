package com.example.ace_taxi_v2.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ace_taxi_v2.Logic.JobApi.GetBookingById;
import com.example.ace_taxi_v2.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NotificationModalActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notification_modal);

        Intent intent = getIntent();
        String jobId = intent.getStringExtra("jobid");
        Log.e("Notificatin Activity jobId",""+jobId);
        int jobid = Integer.parseInt(jobId);

        GetBookingById getBookingById = new GetBookingById(NotificationModalActivity.this);
        getBookingById.getBookingDetails(jobid);
    }
}