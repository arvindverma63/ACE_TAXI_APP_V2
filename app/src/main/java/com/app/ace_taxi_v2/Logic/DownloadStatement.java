package com.app.ace_taxi_v2.Logic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import com.app.ace_taxi_v2.ApiService.ApiService;
import com.app.ace_taxi_v2.Instance.RetrofitClient;

import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DownloadStatement {
    private final Context context;
    private final ActivityResultLauncher<Intent> filePickerLauncher;
    private String pendingFileName;
    private ResponseBody pendingResponseBody;

    public DownloadStatement(Fragment fragment) {
        this.context = fragment.requireContext();
        this.filePickerLauncher = fragment.registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        if (uri != null && pendingResponseBody != null) {
                            boolean saved = saveFileToUri(uri, pendingResponseBody);
                            if (saved) {
                                Toast.makeText(context, "Statement downloaded successfully", Toast.LENGTH_SHORT).show();
                                openFile(uri);
                            } else {
                                Toast.makeText(context, "Failed to save the statement", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    pendingResponseBody = null;
                    pendingFileName = null;
                }
        );
    }

    public void downloadStatement(int statementId, String fileName) {
        try {
            SessionManager sessionManager = new SessionManager(context);
            String token = sessionManager.getToken();

            if (token == null || token.isEmpty()) {
                Toast.makeText(context, "Please log in to download the statement", Toast.LENGTH_SHORT).show();
                return;
            }

            ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
            Call<ResponseBody> call = apiService.downloadStatement("Bearer " + token, statementId);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        pendingResponseBody = response.body();
                        pendingFileName = fileName;
                        promptForSaveLocation();
                    } else {
                        Toast.makeText(context, "Failed to download statement: " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(context, "Download failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Error initiating download", Toast.LENGTH_SHORT).show();
        }
    }

    private void promptForSaveLocation() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_TITLE, pendingFileName);
        filePickerLauncher.launch(intent);
    }

    private boolean saveFileToUri(Uri uri, ResponseBody body) {
        try {
            FileOutputStream fos = (FileOutputStream) context.getContentResolver().openOutputStream(uri);
            if (fos != null) {
                fos.write(body.bytes());
                fos.flush();
                fos.close();
                return true;
            }
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void openFile(Uri uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            context.startActivity(Intent.createChooser(intent, "Open PDF with"));
        } catch (Exception e) {
            Toast.makeText(context, "No PDF viewer found", Toast.LENGTH_SHORT).show();
        }
    }
}