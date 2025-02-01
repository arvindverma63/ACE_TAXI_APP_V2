package com.example.ace_taxi_v2.Components;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.fragment.app.FragmentManager;

import com.example.ace_taxi_v2.Logic.JobStatusReply;
import com.example.ace_taxi_v2.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class JobStatusModal {
    private final Context context;
    private final FragmentManager fragmentManager; // Pass FragmentManager to avoid errors
    private BottomSheetDialog bottomSheetDialog;

    public JobStatusModal(Context context, FragmentManager fragmentManager) {
        this.context = context;
        this.fragmentManager = fragmentManager;
    }

    public void openModal(int jobno) {
        // Initialize BottomSheetDialog
        bottomSheetDialog = new BottomSheetDialog(context);
        View bottomSheetView = LayoutInflater.from(context).inflate(R.layout.today_job_status, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        Button btn_on_route,btn_pick_up,btn_pob,btn_stc,btn_clear,btn_reset;

        btn_on_route = bottomSheetView.findViewById(R.id.btn_on_route);
        btn_pick_up = bottomSheetView.findViewById(R.id.btn_pick_up);
        btn_pob = bottomSheetView.findViewById(R.id.btn_pob);
        btn_stc = bottomSheetView.findViewById(R.id.btn_stc);
        btn_clear = bottomSheetView.findViewById(R.id.btn_clear);
        btn_reset = bottomSheetView.findViewById(R.id.btn_reset);

        JobStatusReply jobStatusReply = new JobStatusReply(context);

        btn_on_route.setOnClickListener(v -> jobStatusReply.updateStatus(jobno,3003));
        btn_pick_up.setOnClickListener(v-> jobStatusReply.updateStatus(jobno,3004));
        btn_pob.setOnClickListener(v -> jobStatusReply.updateStatus(jobno,3005));
        btn_stc.setOnClickListener(v -> jobStatusReply.updateStatus(jobno,3006));
        btn_clear.setOnClickListener(v-> jobStatusReply.updateStatus(jobno,3007));
        btn_reset.setOnClickListener(v-> jobStatusReply.updateStatus(jobno,3008));


        bottomSheetDialog.show();
    }
    // Dismiss modal safely to avoid memory leaks
    private void dismissModal() {
        if (bottomSheetDialog != null && bottomSheetDialog.isShowing()) {
            bottomSheetDialog.dismiss();
        }
    }
}
