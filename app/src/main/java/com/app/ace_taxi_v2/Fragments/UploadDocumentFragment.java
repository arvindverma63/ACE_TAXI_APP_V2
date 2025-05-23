package com.app.ace_taxi_v2.Fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_REQUEST = 2;
    private static final int CAMERA_PERMISSION_REQUEST = 100;
    private Uri imageUri;
    private ImageView selectedUploadButton;
    private int selectedType = -1;
    private File photoFile; // To store camera image

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
            Log.e("UploadDocumentFragment", "One or more views not found in layout");
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
                Log.d("UploadDocumentFragment", "Clicked button with type: " + selectedType);
                try {
                    openImageOptions();
                } catch (Exception e) {
                    Log.e("UploadDocumentFragment", "Error opening image options: " + e.getMessage(), e);
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
        Log.d("UploadDocumentFragment", "Entering openImageOptions");
        try {
            // Check for camera permission
            String[] permissions = {Manifest.permission.CAMERA};
            boolean allPermissionsGranted = true;
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(requireContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    Log.d("UploadDocumentFragment", "Permission missing: " + permission);
                    ActivityCompat.requestPermissions(requireActivity(), permissions, CAMERA_PERMISSION_REQUEST);
                    break;
                }
            }

            if (allPermissionsGranted) {
                Log.d("UploadDocumentFragment", "All permissions granted, showing dialog");
                String[] options = {"Take Photo", "Choose from Gallery"};
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
                builder.setTitle("Select Image Source");
                builder.setItems(options, (dialog, which) -> {
                    Log.d("UploadDocumentFragment", "Dialog option selected: " + which);
                    if (which == 0) {
                        openCamera();
                    } else if (which == 1) {
                        openImageChooser();
                    }
                });
                builder.setNegativeButton("Cancel", (dialog, which) -> {
                    Log.d("UploadDocumentFragment", "Dialog cancelled");
                });
                builder.create().show();
                Log.d("UploadDocumentFragment", "Dialog shown");
            } else {
                Log.d("UploadDocumentFragment", "Permissions not granted, waiting for request result");
            }
        } catch (Exception e) {
            Log.e("UploadDocumentFragment", "Error in openImageOptions: " + e.getMessage(), e);
            new CustomToast(getContext()).showCustomErrorToast("Error showing image options: " + e.getMessage());
        }
    }

    private void openCamera() {
        Log.d("UploadDocumentFragment", "Opening camera");
        try {
            // Check if device has a camera
            if (!requireContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
                Log.w("UploadDocumentFragment", "Device has no camera hardware");
                new CustomToast(getContext()).showCustomToast("No camera available on this device. Please select an image from gallery.");
                openImageChooser();
                return;
            }

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Try alternative intent if standard one fails
            if (intent.resolveActivity(requireActivity().getPackageManager()) == null) {
                Log.w("UploadDocumentFragment", "No app found for ACTION_IMAGE_CAPTURE, trying ACTION_IMAGE_CAPTURE_SECURE");
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE_SECURE);
            }

            if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
                try {
                    photoFile = createImageFile();
                    imageUri = FileProvider.getUriForFile(requireContext(), "com.app.ace_taxi_v2.fileprovider", photoFile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(intent, CAMERA_REQUEST);
                    Log.d("UploadDocumentFragment", "Camera intent started with URI: " + imageUri);
                } catch (IOException ex) {
                    Log.e("UploadDocumentFragment", "Error creating image file: " + ex.getMessage(), ex);
                    new CustomToast(getContext()).showCustomErrorToast("Error preparing camera capture");
                }
            } else {
                Log.w("UploadDocumentFragment", "No camera app found for any capture intent");
                new CustomToast(getContext()).showCustomToast("No camera app found. Please select an image from gallery.");
                openImageChooser();
            }
        } catch (Exception e) {
            Log.e("UploadDocumentFragment", "Error in openCamera: " + e.getMessage(), e);
            new CustomToast(getContext()).showCustomErrorToast("Failed to open camera: " + e.getMessage());
            openImageChooser();
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    private void openImageChooser() {
        Log.d("UploadDocumentFragment", "Opening gallery");
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("UploadDocumentFragment", "onRequestPermissionsResult called with requestCode: " + requestCode);
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            if (allGranted) {
                Log.d("UploadDocumentFragment", "Permissions granted, opening image options");
                openImageOptions();
            } else {
                Log.d("UploadDocumentFragment", "Camera permission denied, falling back to gallery");
                new CustomToast(getContext()).showCustomToast("Camera permission denied. Please select an image from gallery.");
                openImageChooser();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("UploadDocumentFragment", "onActivityResult called with requestCode: " + requestCode + ", resultCode: " + resultCode);
        if (resultCode == Activity.RESULT_OK && selectedUploadButton != null) {
            ImageView displayImageView = uploadButtonToCardMap.get(selectedUploadButton);
            if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
                imageUri = data.getData();
                if (displayImageView != null) {
                    displayImageView.setImageURI(imageUri);
                    uploadImage(imageUri, selectedType);
                    Log.d("UploadDocumentFragment", "Gallery image selected: " + imageUri);
                }
            } else if (requestCode == CAMERA_REQUEST) {
                if (imageUri != null && photoFile != null && photoFile.exists()) {
                    if (displayImageView != null) {
                        displayImageView.setImageURI(imageUri);
                        uploadImage(imageUri, selectedType);
                        Log.d("UploadDocumentFragment", "Camera image captured: " + imageUri);
                    }
                } else {
                    Log.w("UploadDocumentFragment", "Failed to capture camera image");
                    new CustomToast(getContext()).showCustomErrorToast("Failed to capture image");
                }
            }
        } else {
            Log.d("UploadDocumentFragment", "onActivityResult: Invalid result or no button selected");
        }
    }

    public void uploadImage(Uri imageUri, int type) {
        Log.d("UploadDocumentFragment", "Uploading image with type: " + type);
        if (imageUri == null || type == -1) {
            Log.w("UploadDocumentFragment", "No image selected or invalid type");
            new CustomToast(getContext()).showCustomToast("No image selected");
            return;
        }

        File file = FileUtils.getFileFromUri(getContext(), imageUri);
        if (file == null || !file.exists()) {
            Log.e("UploadDocumentFragment", "File not found for URI: " + imageUri);
            new CustomToast(getContext()).showCustomErrorToast("File not found");
            return;
        }

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

        UploadDocumentApi uploadDocumentApi = new UploadDocumentApi(getContext());
        uploadDocumentApi.uploadDoc(body, type);

    }
}