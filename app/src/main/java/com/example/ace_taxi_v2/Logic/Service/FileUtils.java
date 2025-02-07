package com.example.ace_taxi_v2.Logic.Service;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;

public class FileUtils {
    public static File getFileFromUri(Context context, Uri uri) {
        String filePath = null;
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        if (cursor != null) {
            int columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
            if (columnIndex != -1) {
                cursor.moveToFirst();
                filePath = cursor.getString(columnIndex);
            }
            cursor.close();
        }
        if (filePath != null) {
            return new File(filePath);
        } else {
            Log.e("FileUtils", "File path not found");
            return null;
        }
    }
}
