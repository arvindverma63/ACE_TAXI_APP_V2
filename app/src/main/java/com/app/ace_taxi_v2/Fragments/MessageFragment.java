package com.app.ace_taxi_v2.Fragments;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.app.ace_taxi_v2.Fragments.Adapters.MessageAdapter;
import com.app.ace_taxi_v2.Logic.Service.NotificationModalSession;
import com.app.ace_taxi_v2.Models.NotificationModel;
import com.app.ace_taxi_v2.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;

public class MessageFragment extends Fragment {

    private RecyclerView recyclerView;
    private MessageAdapter adapter;
    private NotificationModalSession session;
    private TextView badgeView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true); // Enable menu
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_message, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recycler_view_notifications);
        MaterialToolbar toolbar = view.findViewById(R.id.toolbar);
        session = new NotificationModalSession(requireContext());

        // Use GridLayoutManager with 1 column (change to 2 if needed)
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 1);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setPadding(0, 0, 0, 0); // Remove any padding

        List<NotificationModel> filteredMessages = getFilteredMessages();
        adapter = new MessageAdapter(filteredMessages);
        recyclerView.setAdapter(adapter);

        setupSwipeToDelete();

        toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());
    }

    private List<NotificationModel> getFilteredMessages() {
        List<NotificationModel> allNotifications = session.getAllNotifications();
        List<NotificationModel> filtered = new ArrayList<>();
        for (NotificationModel model : allNotifications) {
            String navId = model.getNavId();
            if ("5".equals(navId) || "6".equals(navId)) {
                filtered.add(model);
            }
        }
        return filtered;
    }

    private void setupSwipeToDelete() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            private final Paint backgroundPaint = new Paint();
            private final Paint textPaint = new Paint();

            {
                backgroundPaint.setColor(Color.RED);
                backgroundPaint.setStyle(Paint.Style.FILL);
                textPaint.setColor(Color.WHITE);
                textPaint.setTextSize(40); // Adjust text size as needed
                textPaint.setAntiAlias(true);
                textPaint.setTextAlign(Paint.Align.CENTER);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();
                // Reset the swipe (so the item returns to its original position)
                adapter.notifyItemChanged(position);

                // Inflate the custom dialog layout
                View dialogView = LayoutInflater.from(getContext())
                        .inflate(R.layout.delete_confirm_dialog, null);

                // Create the Material dialog
                MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(getContext())
                        .setView(dialogView);

                AlertDialog dialog = dialogBuilder.create();

                MaterialButton cancelButton = dialogView.findViewById(R.id.cancel_button);
                MaterialButton deleteButton = dialogView.findViewById(R.id.delete_button);

                cancelButton.setOnClickListener(v -> {
                    dialog.dismiss();
                    // Item remains in place
                });

                deleteButton.setOnClickListener(v -> {
                    // When confirmed, delete the item
                    String notificationNumber = adapter.messages.get(position).getNotificationNumber();
                    session.deleteNotificationByNumber(notificationNumber);
                    adapter.removeItem(position);
                    dialog.dismiss();
                });

                // Show the dialog
                dialog.show();
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                    int actionState, boolean isCurrentlyActive) {
                View itemView = viewHolder.itemView;
                View swipeBackground = itemView.findViewById(R.id.swipe_background);

                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    // Optionally hide the original background view if you are drawing your own
                    if (swipeBackground != null) {
                        swipeBackground.setVisibility(View.GONE);
                    }

                    float left, right;
                    if (dX > 0) { // Swipe right
                        left = itemView.getLeft();
                        right = itemView.getLeft() + dX;
                    } else { // Swipe left
                        left = itemView.getRight() + dX;
                        right = itemView.getRight();
                    }

                    // Draw the red background
                    RectF background = new RectF(left, itemView.getTop(), right, itemView.getBottom());
                    c.drawRect(background, backgroundPaint);

                    // Draw the "Delete" text in the center of the swiped area
                    String text = "Delete";
                    float textX = (left + right) / 2;
                    float textY = itemView.getTop() + (itemView.getBottom() - itemView.getTop()) / 2 + textPaint.getTextSize() / 3;
                    c.drawText(text, textX, textY, textPaint);
                } else {
                    if (swipeBackground != null) {
                        swipeBackground.setVisibility(View.GONE);
                    }
                }

                // Apply translation to the item view to create the swipe effect
                itemView.setTranslationX(dX);

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }
}
