package com.app.ace_taxi_v2.Components;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.app.ace_taxi_v2.R;

public class CustomToast {
    private final Context context;

    public CustomToast(Context context) {
        this.context = context;
    }

    public void showCustomToast(String message) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(R.layout.custom_toast_layout, null);

        TextView text = layout.findViewById(R.id.toastText);
        text.setText(message);

        TextView closeButton = layout.findViewById(R.id.closeButton);
        // Optional: just hide the button if you want to disable it

        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);

        // Positioning the toast
        int xOffset = (int) (context.getResources().getDisplayMetrics().density * 16);
        int yOffset = (int) (context.getResources().getDisplayMetrics().density * 50);
        toast.setGravity(Gravity.TOP | Gravity.END, xOffset, yOffset);

        toast.show();
        closeButton.setOnClickListener(v -> {
            toast.cancel();
        });
    }


}
