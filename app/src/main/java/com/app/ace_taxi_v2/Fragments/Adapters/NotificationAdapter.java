package com.app.ace_taxi_v2.Fragments.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.app.ace_taxi_v2.Components.NotificationDialogController;
import com.app.ace_taxi_v2.Logic.Service.NotificationModalSession;
import com.app.ace_taxi_v2.Models.NotificationModel;
import com.app.ace_taxi_v2.R;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private NotificationModalSession notificationModalSession;
    private Context context;
    private List<NotificationModel> list;

    // Constructor
    public NotificationAdapter(Context context) {
        this.context = context;
        notificationModalSession = new NotificationModalSession(context);
        list = notificationModalSession.getAllNotifications();

        // Prevent null pointer issues by initializing an empty list if null
        if (list == null) {
            list = new java.util.ArrayList<>();
        }
    }

    @NonNull
    @Override
    public NotificationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.ViewHolder holder, int position) {
        NotificationModel notification = list.get(position);

        // Null safety check
        if (notification != null) {
            holder.jobTitle.setText(notification.getTitle() != null ? notification.getTitle() : "No Title");
            holder.message.setText(notification.getMessage() != null ? notification.getMessage() : "No Message");
            holder.type.setText("Job ID: " + (notification.getJobId() != null ? notification.getJobId() : "N/A"));

            // Handle button click to open the modal
            holder.viewJobsButton.setOnClickListener(v -> {
                Log.i("Notification clicked data : ","data: "+notification.getJobId()+" "+notification.getNavId());
                NotificationDialogController notificationDialogController = new NotificationDialogController(context);
                notificationDialogController.openModal(notification.getNavId(), notification.getJobId());
            });
        }
    }

    @Override
    public int getItemCount() {
        return (list != null) ? list.size() : 0;
    }

    // ✅ Method to refresh list when new notifications are added
    public void refreshNotifications() {
        list = notificationModalSession.getAllNotifications();
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView jobTitle, message, type;
        Button viewJobsButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            jobTitle = itemView.findViewById(R.id.jobTitle);
            message = itemView.findViewById(R.id.message);
            type = itemView.findViewById(R.id.type);
            viewJobsButton = itemView.findViewById(R.id.viewJobsButton);
        }
    }
}
