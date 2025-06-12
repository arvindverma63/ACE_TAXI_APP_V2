package com.app.ace_taxi_v2.Fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.app.ace_taxi_v2.Components.CustomToast;
import com.app.ace_taxi_v2.Helper.LogHelperLaravel;
import com.app.ace_taxi_v2.Logic.Service.FileUtils;
import com.app.ace_taxi_v2.Logic.UploadDocumentApi;
import com.app.ace_taxi_v2.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class UploadDocumentFragment extends Fragment {

    private static final int PICK_FILE_REQUEST = 1; // Renamed for clarity
    private static final int CAMERA_REQUEST = 2;
    private static final int CAMERA_PERMISSION_REQUEST = 100;
    private Uri fileUri; // Renamed from imageUri
    private ImageView selectedUploadButton;
    private int selectedType = -1;
    private File photoFile; // For camera images

    private Map<ImageView, Integer> uploadButtonToTypeMap = new HashMap<>();
    private Map<ImageView, ImageView> uploadButtonToCardMap = new HashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upload_document, container, false);

        MaterialToolbar toolbar = view.findViewById(R.id.toolbar_header);
        toolbar.setNavigationOnClickListener(v -> navigateToProfile());

        setupImageUploaders(view);

        return view;
    }

    private void setupImageUploaders(View view) {
        // Initialize upload buttons
        ImageView licenceUploadButton = view.findViewById(R.id.select_lisence_img);
        ImageView insuranceUploadButton = view.findViewById(R.id.insurance_image);
        ImageView dbsCertUploadButton = view.findViewById(R.id.dbs_cert_image);
        ImageView vehicleLicenceUploadButton = view.findViewById(R.id.vehicle_licence_image);
        ImageView driversLicenceUploadButton = view.findViewById(R.id.drivers_licence_image);
        ImageView safeguardingCertUploadButton = view.findViewById(R.id.safe_guarding_cert_image);
        ImageView firstAidCertUploadButton = view.findViewById(R.id.first_aid_cert_image);

        // Initialize display ImageViews
        ImageView licenceDisplay = view.findViewById(R.id.licence_card);
        ImageView insuranceDisplay = view.findViewById(R.id.insurance_card);
        ImageView dbsCertDisplay = view.findViewById(R.id.dbs_cert_card);
        ImageView vehicleLicenceDisplay = view.findViewById(R.id.vehicle_licence_card);
        ImageView driversLicenceDisplay = view.findViewById(R.id.drivers_licence_card);
        ImageView safeguardingCertDisplay = view.findViewById(R.id.safe_guarding_cert_card);
        ImageView firstAidCertDisplay = view.findViewById(R.id.first_aid_cert_card);

        // Check for null views
        if (licenceUploadButton == null || insuranceUploadButton == null || dbsCertUploadButton == null ||
                vehicleLicenceUploadButton == null || driversLicenceUploadButton == null ||
                safeguardingCertUploadButton == null || firstAidCertUploadButton == null ||
                licenceDisplay == null || insuranceDisplay == null || dbsCertDisplay == null ||
                vehicleLicenceDisplay == null || driversLicenceDisplay == null ||
                safeguardingCertDisplay == null || firstAidCertDisplay == null) {
            LogHelperLaravel.getInstance().e("UploadDocumentFragment", "One or more views not found in layout");
            new CustomToast(getContext()).showCustomErrorToast("One or more views not found");
            return;
        }

        // Map upload buttons to display ImageViews
        uploadButtonToCardMap.put(licenceUploadButton, licenceDisplay);
        uploadButtonToCardMap.put(insuranceUploadButton, insuranceDisplay);
        uploadButtonToCardMap.put(dbsCertUploadButton, dbsCertDisplay);
        uploadButtonToCardMap.put(vehicleLicenceUploadButton, vehicleLicenceDisplay);
        uploadButtonToCardMap.put(driversLicenceUploadButton, driversLicenceDisplay);
        uploadButtonToCardMap.put(safeguardingCertUploadButton, safeguardingCertDisplay);
        uploadButtonToCardMap.put(firstAidCertUploadButton, firstAidCertDisplay);

        // Map upload buttons to document types
        uploadButtonToTypeMap.put(licenceUploadButton, 4);
        uploadButtonToTypeMap.put(insuranceUploadButton, 0);
        uploadButtonToTypeMap.put(dbsCertUploadButton, 2);
        uploadButtonToTypeMap.put(vehicleLicenceUploadButton, 3);
        uploadButtonToTypeMap.put(driversLicenceUploadButton, 4);
        uploadButtonToTypeMap.put(safeguardingCertUploadButton, 5);
        uploadButtonToTypeMap.put(firstAidCertUploadButton, 6);

        // Set click listeners
        for (ImageView uploadButton : uploadButtonToCardMap.keySet()) {
            uploadButton.setClickable(true);
            uploadButton.setOnClickListener(v -> {
                selectedUploadButton = uploadButton;
                selectedType = uploadButtonToTypeMap.get(uploadButton);
                LogHelperLaravel.getInstance().d("UploadDocumentFragment", "Clicked button with type: " + selectedType);
                try {
                    openImageOptions();
                } catch (Exception e) {
                    LogHelperLaravel.getInstance().e("UploadDocumentFragment", "Error opening image options: " + e.getMessage());
                    new CustomToast(getContext()).showCustomErrorToast("Failed to open image options: " + e.getMessage());
                }
            });
        }
    }

    private void navigateToProfile() {
        Fragment selectedFragment = new UserProfileFragment();
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, selectedFragment);
        fragmentTransaction.commit();
    }

    private void openImageOptions() {
        LogHelperLaravel.getInstance().d("UploadDocumentFragment", "Entering openImageOptions");
        try {
            // Check for camera permission
            String[] permissions = {Manifest.permission.CAMERA};
            boolean allPermissionsGranted = true;
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(requireContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    LogHelperLaravel.getInstance().d("UploadDocumentFragment", "Permission missing: " + permission);
                    ActivityCompat.requestPermissions(requireActivity(), permissions, CAMERA_PERMISSION_REQUEST);
                    break;
                }
            }

            if (allPermissionsGranted) {
                LogHelperLaravel.getInstance().d("UploadDocumentFragment", "All permissions granted, showing dialog");
                String[] options = {"Take Photo", "Choose from Gallery"};
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
                builder.setTitle("Select File Source");
                builder.setItems(options, (dialog, which) -> {
                    LogHelperLaravel.getInstance().d("UploadDocumentFragment", "Dialog option selected: " + which);
                    if (which == 0) {
                        openCamera();
                    } else if (which == 1) {
                        openFileChooser();
                    }
                });
                builder.setNegativeButton("Cancel", (dialog, which) -> {
                    LogHelperLaravel.getInstance().d("UploadDocumentFragment", "Dialog cancelled");
                });
                builder.create().show();
                LogHelperLaravel.getInstance().d("UploadDocumentFragment", "Dialog shown");
            } else {
                LogHelperLaravel.getInstance().d("UploadDocumentFragment", "Permissions not granted, waiting for request result");
            }
        } catch (Exception e) {
            LogHelperLaravel.getInstance().e("UploadDocumentFragment", "Error in openImageOptions: " + e.getMessage());
            new CustomToast(getContext()).showCustomErrorToast("Error showing file options: " + e.getMessage());
        }
    }

    private void openCamera() {
        LogHelperLaravel.getInstance().d("UploadDocumentFragment", "Attempting to open camera");
        try {
            // Check if device has a camera
            if (!requireContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
                LogHelperLaravel.getInstance().d("UploadDocumentFragment", "Device has no camera hardware");
                new CustomToast(getContext()).showCustomToast("No camera available on this device. Please select a file from gallery.");
                openFileChooser();
                return;
            }

            // Log device info for debugging
            LogHelperLaravel.getInstance().d("UploadDocumentFragment", "Device: " + Build.MANUFACTURER + ", Model: " + Build.MODEL + ", Android Version: " + Build.VERSION.RELEASE);

            // Try camera intents in order of preference
            Intent[] cameraIntents = new Intent[] {
                    new Intent(MediaStore.ACTION_IMAGE_CAPTURE),
                    new Intent(MediaStore.ACTION_IMAGE_CAPTURE_SECURE),
                    new Intent("android.media.action.IMAGE_CAPTURE") // Generic fallback
            };

            boolean intentAvailable = false;
            Intent selectedIntent = null;

            // Check which intent has an available activity
            for (Intent intent : cameraIntents) {
                if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
                    selectedIntent = intent;
                    intentAvailable = true;
                    LogHelperLaravel.getInstance().d("UploadDocumentFragment", "Found app for intent: " + intent.getAction());
                    break;
                } else {
                    LogHelperLaravel.getInstance().d("UploadDocumentFragment", "No app found for intent: " + intent.getAction());
                }
            }

            if (intentAvailable && selectedIntent != null) {
                try {
                    photoFile = createImageFile();
                    fileUri = FileProvider.getUriForFile(requireContext(), "com.app.ace_taxi_v2.fileprovider", photoFile);
                    selectedIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                    startActivityForResult(selectedIntent, CAMERA_REQUEST);
                    LogHelperLaravel.getInstance().d("UploadDocumentFragment", "Camera intent started with URI: " + fileUri + ", Action: " + selectedIntent.getAction());
                } catch (IOException ex) {
                    LogHelperLaravel.getInstance().e("UploadDocumentFragment", "Error creating image file: " + ex.getMessage());
                    new CustomToast(getContext()).showCustomErrorToast("Error preparing camera capture");
                    openFileChooser();
                }
            } else {
                LogHelperLaravel.getInstance().d("UploadDocumentFragment", "No camera app found for any capture intent");
                new CustomToast(getContext()).showCustomToast("No camera app available. Please select an image or PDF from gallery.");
                openFileChooser();
            }
        } catch (Exception e) {
            LogHelperLaravel.getInstance().e("UploadDocumentFragment", "Error in openCamera: " + e.getMessage());
            new CustomToast(getContext()).showCustomErrorToast("Failed to open camera: " + e.getMessage());
            openFileChooser();
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    private void openFileChooser() {
        LogHelperLaravel.getInstance().d("UploadDocumentFragment", "Opening file chooser");
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        // Allow both images and PDFs
        intent.setType("*/*");
        String[] mimeTypes = {"image/*", "application/pdf"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(Intent.createChooser(intent, "Select File"), PICK_FILE_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        LogHelperLaravel.getInstance().d("UploadDocumentFragment", "onRequestPermissionsResult called with requestCode: " + requestCode);
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            if (allGranted) {
                LogHelperLaravel.getInstance().d("UploadDocumentFragment", "Permissions granted, opening file options");
                openImageOptions();
            } else {
                LogHelperLaravel.getInstance().d("UploadDocumentFragment", "Camera permission denied, falling back to gallery");
                new CustomToast(getContext()).showCustomToast("Camera permission denied. Please select a file from gallery.");
                openFileChooser();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogHelperLaravel.getInstance().d("UploadDocumentFragment", "onActivityResult called with requestCode: " + requestCode + ", resultCode: " + resultCode);
        if (resultCode == Activity.RESULT_OK && selectedUploadButton != null) {
            ImageView displayImageView = uploadButtonToCardMap.get(selectedUploadButton);
            if (requestCode == PICK_FILE_REQUEST && data != null && data.getData() != null) {
                fileUri = data.getData();
                String mimeType = requireContext().getContentResolver().getType(fileUri);
                LogHelperLaravel.getInstance().d("UploadDocumentFragment", "Selected file MIME type: " + mimeType);

                if (mimeType != null && mimeType.startsWith("image/") && displayImageView != null) {
                    // Handle image
                    displayImageView.setImageURI(fileUri);
                    uploadFile(fileUri, selectedType);
                    LogHelperLaravel.getInstance().d("UploadDocumentFragment", "Gallery image selected: " + fileUri);
                } else if (mimeType != null && mimeType.equals("application/pdf")) {
                    // Handle PDF
                    // Optionally, update UI to indicate PDF selection (e.g., change icon or show toast)
                    if (displayImageView != null) {
                        // Set a placeholder or PDF icon since we can't display the PDF
                        displayImageView.setImageResource(R.drawable.ic_pdf); // Ensure you have a PDF icon in drawable
                    }
                    new CustomToast(getContext()).showCustomToast("PDF selected successfully");
                    uploadFile(fileUri, selectedType);
                    LogHelperLaravel.getInstance().d("UploadDocumentFragment", "PDF selected: " + fileUri);
                } else {
                    LogHelperLaravel.getInstance().d("UploadDocumentFragment", "Unsupported file type: " + mimeType);
                    new CustomToast(getContext()).showCustomErrorToast("Please select an image or PDF file");
                }
            } else if (requestCode == CAMERA_REQUEST) {
                if (fileUri != null && photoFile != null && photoFile.exists()) {
                    if (displayImageView != null) {
                        displayImageView.setImageURI(fileUri);
                        uploadFile(fileUri, selectedType);
                        LogHelperLaravel.getInstance().d("UploadDocumentFragment", "Camera image captured: " + fileUri);
                    }
                } else {
                    LogHelperLaravel.getInstance().d("UploadDocumentFragment", "Failed to capture camera image");
                    new CustomToast(getContext()).showCustomErrorToast("Failed to capture image");
                }
            }
        } else {
            LogHelperLaravel.getInstance().d("UploadDocumentFragment", "onActivityResult: Invalid result or no button selected");
        }
    }

    public void uploadFile(Uri fileUri, int type) {
        LogHelperLaravel.getInstance().d("UploadDocumentFragment", "Uploading file with type: " + type);
        if (fileUri == null || type == -1) {
            LogHelperLaravel.getInstance().d("UploadDocumentFragment", "No file selected or invalid type");
            new CustomToast(getContext()).showCustomToast("No file selected");
            return;
        }

        File file = FileUtils.getFileFromUri(getContext(), fileUri);
        if (file == null || !file.exists()) {
            LogHelperLaravel.getInstance().e("UploadDocumentFragment", "File not found for URI: " + fileUri);
            new CustomToast(getContext()).showCustomErrorToast("File not found");
            return;
        }

        // Validate file size (e.g., max 10MB)
        long maxFileSize = 10 * 1024 * 1024; // 10MB in bytes
        if (file.length() > maxFileSize) {
            LogHelperLaravel.getInstance().e("UploadDocumentFragment", "File size too large: " + file.length() + " bytes");
            new CustomToast(getContext()).showCustomErrorToast("File size exceeds 10MB limit");
            return;
        }

        // Determine MediaType based on file
        String mimeType = getMimeType(fileUri);
        if (mimeType == null) {
            LogHelperLaravel.getInstance().d("UploadDocumentFragment", "Unknown MIME type for URI: " + fileUri);
            mimeType = "application/octet-stream"; // Fallback
        }

        // Validate supported MIME types
        if (!mimeType.startsWith("image/") && !mimeType.equals("application/pdf")) {
            LogHelperLaravel.getInstance().e("UploadDocumentFragment", "Unsupported file type: " + mimeType);
            new CustomToast(getContext()).showCustomErrorToast("Please select an image or PDF file");
            return;
        }

        RequestBody requestFile = RequestBody.create(MediaType.parse(mimeType), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

        try {
            UploadDocumentApi uploadDocumentApi = new UploadDocumentApi(getContext());
            uploadDocumentApi.uploadDoc(body, type);
            LogHelperLaravel.getInstance().d("UploadDocumentFragment", "Upload initiated for file: " + file.getName() + ", MIME: " + mimeType);
        } catch (Exception e) {
            LogHelperLaravel.getInstance().e("UploadDocumentFragment", "Error initiating upload: " + e.getMessage());
            new CustomToast(getContext()).showCustomErrorToast("Failed to upload file: " + e.getMessage());
        }
    }

    private String getMimeType(Uri uri) {
        String mimeType = getContext().getContentResolver().getType(uri);
        if (mimeType == null) {
            // Fallback: Infer from file extension
            String fileName = null;
            Cursor cursor = getContext().getContentResolver().query(uri, null, null, null, null);
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
                String extension = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
                switch (extension) {
                    case ".jpg":
                    case ".jpeg":
                        return "image/jpeg";
                    case ".png":
                        return "image/png";
                    case ".pdf":
                        return "application/pdf";
                    default:
                        return null;
                }
            }
        }
        return mimeType;
    }
}
