package com.app.ace_taxi_v2.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import com.app.ace_taxi_v2.JobModals.JobModal;
import com.app.ace_taxi_v2.Logic.JobApi.GetBookingById;
import com.app.ace_taxi_v2.Logic.Service.NotificationModalSession;
import com.app.ace_taxi_v2.R;

public class NotificationModalActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notification_modal);


        NotificationModalSession notificationModalSession = new NotificationModalSession(this);
//
//        String message = "";
//        String passenger = "";
//        String datetime = "";
//        // Retrieve Intent Data
//        Intent intent = getIntent();
//        String jobId = intent.getStringExtra("jobid");
//        String navId = intent.getStringExtra("navId");
//        message = intent.getStringExtra("message");
//        passenger = intent.getStringExtra("passenger");
//        datetime = intent.getStringExtra("datetime");
//
//
//        Log.e("NotificationActivity", "Job ID: " + jobId);
//        Log.d("NotificationActivity", "Nav ID: " + navId);
//
//        // Validate navId and redirect to HomeActivity if invalid
//        int navid = -1;
//        int jobid = -1;
//
//        try {
//            if (navId != null && !navId.isEmpty()) {
//                navid = Integer.parseInt(navId);
//            }
//            try {
//                if (jobId != null && !jobId.isEmpty()) {
//                    jobid = Integer.parseInt(jobId);
//                }
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//
//        } catch (NumberFormatException e) {
//            Log.e("NotificationActivity", "Invalid navId or jobId format", e);
//            redirectToHome(); // Redirect to HomeActivity if navId is invalid
//            return;
//        }
//
//        // Redirect if navId is missing or invalid
//        if (navid == -1) {
//            redirectToHome();
//            return;
//        }
//
//        // Process the modal action
//        JobModal jobModal = new JobModal(this);
//        switch (navid) {
//            case 1:
//                new GetBookingById(this).getBookingDetails(jobid); //job offer
//                break;
//            case 2:
////                jobModal.jobAmenedment();
//                break;
//            case 3:
////                jobModal.jobCancel();
//                break;
//            case 4:
//                jobModal.JobRead(); //will be navId 7
//                break;
//            case 5:
////                jobModal.jobComplete();
//                break;
//            case 6:
//                jobModal.jobUnallocated(jobid,passenger,datetime);
//                break;
//            case 7:
//                jobModal.JobRead();
//                break;
//            case 8:
//                jobModal.JobRead(); // for globel message
//                break;
//            default:
//                redirectToHome(); // Redirect if navId doesn't match any case
//                break;
//        }


    }

    private void redirectToHome() {
        Log.d("NotificationActivity", "Redirecting to HomeActivity...");
        Intent intent = new Intent(NotificationModalActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish(); // Ensure NotificationModalActivity is closed
    }
}
