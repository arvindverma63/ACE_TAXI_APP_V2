package com.app.ace_taxi_v2.Fragments.Adapters;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.app.ace_taxi_v2.R;

import java.util.List;

public class DestinationAdapter extends ArrayAdapter<String> {
    private final List<String> items;
    private String selectedItem;

    public DestinationAdapter(Context context, int resource, int textViewResourceId, List<String> items) {
        super(context, resource, textViewResourceId, items);
        this.items = items;
        Log.d(TAG, "DestinationAdapter initialized with " + items.size() + " items");
    }

    public void setSelectedItem(String selectedItem) {
        this.selectedItem = selectedItem;
        notifyDataSetChanged();
        Log.d(TAG, "Selected item set to: " + selectedItem);
    }

    @Override
    public int getCount() {
        int count = super.getCount();
        Log.d(TAG, "Adapter getCount: " + count);
        return count;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        ImageView checkMark = view.findViewById(R.id.checkMark);
        String item = getItem(position);
        checkMark.setVisibility(item != null && item.equals(selectedItem) ? View.VISIBLE : View.GONE);
        Log.d(TAG, "getView position " + position + ": " + item + ", checkmark visible: " + (checkMark.getVisibility() == View.VISIBLE));
        return view;
    }
}