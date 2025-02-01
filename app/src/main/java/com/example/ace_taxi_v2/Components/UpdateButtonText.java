package com.example.ace_taxi_v2.Components;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.core.content.ContextCompat;

import com.example.ace_taxi_v2.R;

public class UpdateButtonText {
    public Context context;

    public UpdateButtonText(Context context){
        this.context = context;
    }

    public void todayJobButtonStatus(String status) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View pageView = layoutInflater.inflate(R.layout.today_job_item, null);

        Button startBtn = pageView.findViewById(R.id.startButton);
        int primaryColor = ContextCompat.getColor(context, R.color.dark_blue);
        int rejectedColor = ContextCompat.getColor(context, R.color.primaryColor);
        // Use .equals() for string comparison
        if ("1".equals(status)) {
            startBtn.setText("STARTED");
            startBtn.setBackgroundColor(primaryColor);
        } else if ("2".equals(status)) {
            startBtn.setText("REJECTED");
            startBtn.setBackgroundColor(rejectedColor);
        } else if ("3".equals(status)) {
            startBtn.setText("COMPLETED");
            startBtn.setBackgroundColor(primaryColor);
        }else{

        }
    }
}
