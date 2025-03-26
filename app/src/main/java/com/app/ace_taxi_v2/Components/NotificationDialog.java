package com.app.ace_taxi_v2.Components;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.app.ace_taxi_v2.Fragments.Adapters.NotificationAdapter;
import com.app.ace_taxi_v2.Logic.Service.NotificationModalSession;
import com.app.ace_taxi_v2.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;

public class NotificationDialog {
    private Context context;
    private BottomSheetDialog bottomSheetDialog;
    private NotificationAdapter notificationAdapter;

    public NotificationDialog(Context context) {
        this.context = context;
    }

    public void openModal() {
        // Inflate layout
        View view = LayoutInflater.from(context).inflate(R.layout.notification_layout, null);

        // Ensure we are getting a RecyclerView and not a CardView
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        if (recyclerView == null) {
            throw new IllegalStateException("RecyclerView not found in layout. Check notification_layout.xml");
        }

        MaterialButton delete_all = view.findViewById(R.id.delete_all);
        delete_all.setOnClickListener(v -> {
            NotificationModalSession notificationModalSession = new NotificationModalSession(context);
            notificationModalSession.clearAllNotifications();
            dismissDialog();
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        // Set Adapter
        notificationAdapter = new NotificationAdapter(context);
        recyclerView.setAdapter(notificationAdapter);

        // Show BottomSheetDialog
        bottomSheetDialog = new BottomSheetDialog(context);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.setCancelable(true);
        bottomSheetDialog.show();
    }

    public void refreshNotifications() {
        if (notificationAdapter != null) {
            notificationAdapter.refreshNotifications();
        }
    }

    public void dismissDialog() {
        if (bottomSheetDialog != null && bottomSheetDialog.isShowing()) {
            bottomSheetDialog.dismiss();
        }
    }
}
