package com.app.ace_taxi_v2.Logic.Service;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.OpenableColumns;
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

        // Handle content:// URIs (e.g., from gallery or file picker)
        String fileName = null;
        String mimeType = context.getContentResolver().getType(uri);
        String extension = getExtensionFromMimeType(mimeType, uri, context);

        // Get file name from URI
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex >= 0) {
                        fileName = cursor.getString(nameIndex);
                    }
                }
            } finally {
                cursor.close();
            }
        }

        // Fallback file name if not found
        if (fileName == null) {
            fileName = "temp_file_" + System.currentTimeMillis() + (extension != null ? extension : "");
        } else if (!fileName.contains(".")) {
            fileName += extension != null ? extension : "";
        }

        // Copy content to a temp file
        try {
            File tempFile = new File(context.getCacheDir(), fileName);
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
                    Log.d("FileUtils", "Successfully created temp file: " + tempFile.getAbsolutePath());
                    return tempFile;
                }
            }
        } catch (IOException e) {
            Log.e("FileUtils", "Error copying URI to file: " + e.getMessage(), e);
        }

        Log.e("FileUtils", "Failed to create file for URI: " + uri.toString());
        return null;
    }

    private static String getExtensionFromMimeType(String mimeType, Uri uri, Context context) {
        if (mimeType != null) {
            switch (mimeType) {
                case "image/jpeg":
                    return ".jpg";
                case "image/png":
                    return ".png";
                case "application/pdf":
                    return ".pdf";
                default:
                    Log.w("FileUtils", "Unknown MIME type: " + mimeType);
            }
        }

        // Fallback: Infer extension from file name in URI
        String fileName = null;
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex >= 0) {
                        fileName = cursor.getString(nameIndex);
                    }
                }
            } finally {
                cursor.close();
            }
        }

        if (fileName != null && fileName.contains(".")) {
            return fileName.substring(fileName.lastIndexOf("."));
        }

        return "";
    }

    public static Uri saveBitmapToFile(Context context, Bitmap bitmap) {
        File file = null;
        FileOutputStream fos = null;
        try {
            file = new File(context.getCacheDir(), "camera_image_" + System.currentTimeMillis() + ".jpg");
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos); // Reduced quality to 90 to save space
            fos.flush();
            Log.d("FileUtils", "Bitmap saved to: " + file.getAbsolutePath());
            return Uri.fromFile(file);
        } catch (IOException e) {
            Log.e("FileUtils", "Error saving bitmap to file: " + e.getMessage(), e);
            return null;
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    Log.e("FileUtils", "Error closing FileOutputStream: " + e.getMessage(), e);
                }
            }
        }
    }
}