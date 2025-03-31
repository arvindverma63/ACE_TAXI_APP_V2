package com.app.ace_taxi_v2.Logic.Service;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {

    public static File getFileFromUri(Context context, Uri uri) {
        if (uri == null) {
            Log.e("FileUtils", "URI is null");
            return null;
        }

        // Handle file:// URIs (e.g., from camera)
        if ("file".equals(uri.getScheme())) {
            String filePath = uri.getPath();
            if (filePath != null) {
                File file = new File(filePath);
                if (file.exists()) {
                    return file;
                } else {
                    Log.e("FileUtils", "File does not exist at path: " + filePath);
                    return null;
                }
            }
        }

        // Handle content:// URIs (e.g., from gallery)
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

        if (filePath != null && new File(filePath).exists()) {
            return new File(filePath);
        } else {
            // Fallback: Copy the content to a temp file if direct path isn’t available
            try {
                File tempFile = new File(context.getCacheDir(), "temp_image_" + System.currentTimeMillis() + ".jpg");
                InputStream inputStream = context.getContentResolver().openInputStream(uri);
                if (inputStream != null) {
                    OutputStream outputStream = new FileOutputStream(tempFile);
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                    inputStream.close();
                    outputStream.close();
                    if (tempFile.exists()) {
                        return tempFile;
                    }
                }
            } catch (IOException e) {
                Log.e("FileUtils", "Error copying URI to file: " + e.getMessage());
            }
            Log.e("FileUtils", "File path not found or inaccessible for URI: " + uri.toString());
            return null;
        }
    }

    public static Uri saveBitmapToFile(Context context, Bitmap bitmap) {
        File file = null;
        FileOutputStream fos = null;
        try {
            file = new File(context.getCacheDir(), "camera_image_" + System.currentTimeMillis() + ".jpg");
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            return Uri.fromFile(file);
        } catch (IOException e) {
            Log.e("FileUtils", "Error saving bitmap to file: " + e.getMessage());
            return null;
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    Log.e("FileUtils", "Error closing FileOutputStream: " + e.getMessage());
                }
            }
        }
    }
}