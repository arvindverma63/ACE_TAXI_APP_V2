package com.app.ace_taxi_v2.Fragments;

import android.content.Context;
import android.util.Log;

import com.app.ace_taxi_v2.Activity.NotificationModalActivity;
import com.app.ace_taxi_v2.JobModals.JobModal;
import com.app.ace_taxi_v2.Logic.dashboard.CurrentBooking;
import com.app.ace_taxi_v2.Models.Jobs.TodayBooking;

import java.util.List;

public class NotificationJobDialog {
    public Context context;

    public NotificationJobDialog(Context context){
        this.context = context;
    }

    void openNotificationModalForJob(){
        Log.d("calling function","calling Notification Modal for job");
        CurrentBooking currentBooking = new CurrentBooking(context);
        currentBooking.getCurrentBooking(new CurrentBooking.CurrentJobCallback() {
            @Override
            public void onSuccess(List<TodayBooking> list) {

                for(int i=0;i<list.size();i++){
                    Log.e("status : ",list.get(i).getStatus());
                    if("0".equals(list.get(i).getStatus())){
                        JobModal jobModal = new JobModal(context);
                        jobModal.jobOfferModal(list.get(i).getPickupAddress(),
                                list.get(i).getDestinationAddress(),
                                list.get(i).getPrice(),
                                list.get(i).getPickupDateTime(),
                                list.get(i).getPassengerName(),
                                list.get(i).getBookingId());
                    }
                }
            }

            @Override
            public void onError(String error) {

            }
        });
    }

}
