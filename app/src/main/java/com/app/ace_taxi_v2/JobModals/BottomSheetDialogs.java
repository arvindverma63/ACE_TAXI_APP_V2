package com.app.ace_taxi_v2.JobModals;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.app.ace_taxi_v2.Fragments.JobFragment;
import com.app.ace_taxi_v2.Fragments.ListAvailabillity;
import com.app.ace_taxi_v2.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class BottomSheetDialogs {
    private final Context context;

    public BottomSheetDialogs(Context context) {
        this.context = context;
    }

    public void openJobAccepted(String username, String pickup) {
        BottomSheetDialog bottomSheetDialogs = new BottomSheetDialog(context,R.style.BottomSheetDialogTransparent);
        View view = LayoutInflater.from(context).inflate(R.layout.job_accepted_bottom_sheet_dialog, null);
        bottomSheetDialogs.setContentView(view);

        TextView passenger = view.findViewById(R.id.tvUserName);
        TextView pickupAddress = view.findViewById(R.id.tvUserDetails);
        passenger.setText(username);
        pickupAddress.setText(pickup);

        Button btnBack = view.findViewById(R.id.btnBack);
        Button btnViewJobs = view.findViewById(R.id.btnAvail);
        view.setBackground(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        btnBack.setOnClickListener(v -> bottomSheetDialogs.dismiss());

        btnViewJobs.setOnClickListener(v -> {
            if (context instanceof FragmentActivity) {
                Fragment fragment = new JobFragment();
                FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
            bottomSheetDialogs.dismiss();
        });

        bottomSheetDialogs.show(); // Important: Show the BottomSheetDialog
    }

    public void openJobCompleted(String username,String pickup){
        BottomSheetDialog bottomSheetDialogs = new BottomSheetDialog(context,R.style.BottomSheetDialogTransparent);
        View view = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_dialog_complete_booking, null);
        bottomSheetDialogs.setContentView(view);

        TextView passenger = view.findViewById(R.id.tvUserName);
        TextView pickupAddress = view.findViewById(R.id.tvUserDetails);
        passenger.setText(username);
        pickupAddress.setText(pickup);

        Button btnBack = view.findViewById(R.id.btnBack);
        Button btnViewJobs = view.findViewById(R.id.btnAvail);
        view.setBackground(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        btnBack.setOnClickListener(v -> bottomSheetDialogs.dismiss());

        btnViewJobs.setOnClickListener(v -> {
            if (context instanceof FragmentActivity) {
                Fragment fragment = new JobFragment();
                FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
            bottomSheetDialogs.dismiss();
        });

        bottomSheetDialogs.show(); // Important: Show the BottomSheetDialog
    }
    public void openJobRejected(){
        BottomSheetDialog bottomSheetDialogs = new BottomSheetDialog(context,R.style.BottomSheetDialogTransparent);
        View view = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_job_rejected, null);
        bottomSheetDialogs.setContentView(view);
        Button btnBack = view.findViewById(R.id.btnBack);
        view.setBackground(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        btnBack.setOnClickListener(v -> bottomSheetDialogs.dismiss());

        bottomSheetDialogs.show(); // Important: Show the BottomSheetDialog
        btnBack.setOnClickListener(v -> {
            bottomSheetDialogs.dismiss();
        });
    }

    public void addAvail(){
        BottomSheetDialog bottomSheetDialogs = new BottomSheetDialog(context,R.style.BottomSheetDialogTransparent);
        View view = LayoutInflater.from(context).inflate(R.layout.availablitiy_add_bottom_sheet, null);
        bottomSheetDialogs.setContentView(view);
        Button btnBack = view.findViewById(R.id.btnBack);
        Button viewAvail = view.findViewById(R.id.btnAvail);

        view.setBackground(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        btnBack.setOnClickListener(v -> bottomSheetDialogs.dismiss());

        bottomSheetDialogs.show(); // Important: Show the BottomSheetDialog
        btnBack.setOnClickListener(v -> {
            bottomSheetDialogs.dismiss();
        });
        viewAvail.setOnClickListener(v -> {
            Fragment fragment = new ListAvailabillity();
            FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });
    }

    public void alreadyAdd(){
        BottomSheetDialog bottomSheetDialogs = new BottomSheetDialog(context,R.style.BottomSheetDialogTransparent);
        View view = LayoutInflater.from(context).inflate(R.layout.avail_error, null);
        bottomSheetDialogs.setContentView(view);
        Button btnBack = view.findViewById(R.id.btnBack);

        view.setBackground(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        btnBack.setOnClickListener(v -> bottomSheetDialogs.dismiss());

        bottomSheetDialogs.show(); // Important: Show the BottomSheetDialog
        btnBack.setOnClickListener(v -> {
            bottomSheetDialogs.dismiss();
        });

    }
}
