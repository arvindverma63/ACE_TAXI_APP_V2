package com.app.ace_taxi_v2.Fragments.HomeFragmentHelpers;

import android.content.Context;
import android.widget.TextView;

import com.app.ace_taxi_v2.Logic.LoginManager;
import com.app.ace_taxi_v2.Models.UserProfileResponse;

public class DashboardHelper {

    public Context context;
    public DashboardHelper(Context context){
        this.context = context;
    }
    public void updateMessage(TextView username,TextView email){
        LoginManager loginManager = new LoginManager(context);
        loginManager.getProfile(new LoginManager.ProfileCallback() {
            @Override
            public void onSuccess(UserProfileResponse userProfileResponse) {
                username.setText(userProfileResponse.getFullname());
                email.setText(userProfileResponse.getEmail());
            }

            @Override
            public void onFailure(String errorMessage) {

            }
        });
    }
}
