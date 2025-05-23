package com.app.ace_taxi_v2.Fragments.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.ace_taxi_v2.JobModals.JobModal;
import com.app.ace_taxi_v2.Models.NotificationModel;
import com.app.ace_taxi_v2.R;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    public List<NotificationModel> messages;

    public MessageAdapter(List<NotificationModel> messages) {
        this.messages = messages;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        // Force the item to stretch to the full column width
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        view.setLayoutParams(layoutParams);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NotificationModel model = messages.get(position);
        holder.title.setText(model.getTitle());
        holder.message.setText(model.getMessage());
        holder.notificationNumber.setText("NOTIF-" + model.getNotificationNumber());
        holder.datetime.setText(model.getDatetime());
//        holder.cardView.setOnClickListener(v -> {
//            JobModal jobModal = new JobModal(v.getContext());
//            jobModal.readMessage(messages.get(position).getMessage(),messages.get(position).getDatetime());
//        });
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void updateMessages(List<NotificationModel> newMessages) {
        this.messages = newMessages;
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        messages.remove(position);
        notifyItemRemoved(position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, message, notificationNumber,datetime;
        MaterialCardView cardView;

        ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.notification_title);
            message = itemView.findViewById(R.id.notification_message);
            notificationNumber = itemView.findViewById(R.id.notification_number);
            cardView = itemView.findViewById(R.id.card_view);
            datetime = itemView.findViewById(R.id.datetime);
        }
    }
}