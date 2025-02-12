package com.app.ace_taxi_v2.Components;

import android.content.Context;
import android.util.Log;

import com.app.ace_taxi_v2.JobModals.JobModal;
import com.app.ace_taxi_v2.Logic.JobApi.GetBookingById;

public class NotificationDialogController {
    private Context context;

    public NotificationDialogController(Context context) {
        this.context = context;
    }

    public void openModal(String navId, String jobId) {
        JobModal jobModal = new JobModal(context);

        // Convert navId and jobId safely
        int navid = parseInteger(navId, -1);
        int jobid = parseInteger(jobId, -1);

        Log.d("NotificationDialogController", "Processing Notification - navId: " + navid + ", jobId: " + jobid);

        switch (navid) {
            case 1:
                Log.d("NotificationDialogController", "Fetching booking details...");
                new GetBookingById(context).getBookingDetails(jobid); // Job Offer
                break;
            case 2:
                Log.d("NotificationDialogController", "Processing job amendment...");
                jobModal.jobAmenedment();
                break;
            case 3:
                Log.d("NotificationDialogController", "Processing job cancellation...");
                jobModal.jobCancel();
                break;
            case 4:
            case 7:
            case 8:
                Log.d("NotificationDialogController", "Processing job read...");
                jobModal.JobRead(); // Handles general/global messages
                break;
            case 5:
                Log.d("NotificationDialogController", "Processing job completion...");
                jobModal.jobComplete();
                break;
            case 6:
                Log.d("NotificationDialogController", "Processing unallocated job...");
                jobModal.jobUnallocated();
                break;
            default:
                Log.w("NotificationDialogController", "Unknown navId: " + navid);
                break;
        }
    }

    // ✅ Safe Integer Parsing
    private int parseInteger(String value, int defaultValue) {
        try {
            return (value != null && !value.isEmpty()) ? Integer.parseInt(value) : defaultValue;
        } catch (NumberFormatException e) {
            Log.e("NotificationDialogController", "Invalid number format: " + value, e);
            return defaultValue;
        }
    }
}
