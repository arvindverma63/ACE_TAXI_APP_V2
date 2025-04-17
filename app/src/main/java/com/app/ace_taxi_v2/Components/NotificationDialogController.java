package com.app.ace_taxi_v2.Components;

import android.content.Context;
import android.util.Log;

import com.app.ace_taxi_v2.JobModals.JobModal;
import com.app.ace_taxi_v2.Logic.JobApi.GetBookingById;
import com.app.ace_taxi_v2.Logic.JobApi.JobOfferNotification;
import com.app.ace_taxi_v2.Logic.Service.NotificationModalSession;

public class NotificationDialogController {
    private Context context;
    public NotificationModalSession notificationModalSession;

    public NotificationDialogController(Context context) {
        this.context = context;
        notificationModalSession = new NotificationModalSession(context);
    }

    public void openModal(String navId, String jobId,String message,String passenger,String datetime) {
        JobModal jobModal = new JobModal(context);

        // Convert navId and jobId safely
        int navid = parseInteger(navId, -1);
        int jobid = parseInteger(jobId, -1);

        Log.d("NotificationDialogController", "Processing Notification - navId: " + navid + ", jobId: " + jobid);

        switch (navid) {
            case 0:
                Log.d("NotificationDialogController", "Fetching booking details...");

                break;
            case 1:
                Log.d("NotificationDialogController", "Processing job amendment...");
                try{
                    new GetBookingById(context).getBookingDetails(jobid);
                    int bookingId = Integer.parseInt(notificationModalSession.getLatestJobId());
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
                break;
            case 2:
                Log.d("NotificationDialogController", "Processing job cancellation...");
                jobModal.jobUnallocated(jobid,passenger,datetime);
                break;
            case 3:
                Log.d("NotificationDialogController", "Processing job read...");
                jobModal.jobAmenedment(jobId,passenger,datetime); // Handles general/global messages
                break;
            case 4:
                Log.d("NotificationDialogController", "Processing job completion...");
                jobModal.jobCancel(jobId,passenger,datetime);
                break;
            case 5:
                Log.d("NotificationDialogController", "Processing unallocated job...");
                jobModal.readMessage(message,datetime);
                break;
            case 6:
                jobModal.readMessage(message,datetime);
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
