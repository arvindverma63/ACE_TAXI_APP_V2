package com.app.ace_taxi_v2.Fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import com.app.ace_taxi_v2.Logic.Service.FileUtils;
import com.app.ace_taxi_v2.Logic.UploadDocumentApi;
import com.app.ace_taxi_v2.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.File;
import java.util.HashMap;
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

    private Map<ImageView, Integer> uploadButtonToTypeMap = new HashMap<>();
    private Map<ImageView, ImageView> uploadButtonToCardMap = new HashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upload_document, container, false);

        MaterialToolbar toolbar = view.findViewById(R.id.toolbar_header);
        toolbar.setNavigationOnClickListener(v -> navigateToProfile());

        setupImageUploaders(view);

        return view;
    }

    private void setupImageUploaders(View view) {
        // Same as your original setup
        ImageView licenceUploadButton = view.findViewById(R.id.select_lisence_img);
        ImageView insuranceUploadButton = view.findViewById(R.id.insurance_image);
        ImageView dbsCertUploadButton = view.findViewById(R.id.dbs_cert_image);
        ImageView vehicleLicenceUploadButton = view.findViewById(R.id.vehicle_licence_image);
        ImageView driversLicenceUploadButton = view.findViewById(R.id.drivers_licence_image);
        ImageView safeguardingCertUploadButton = view.findViewById(R.id.safe_guarding_cert_image);
        ImageView firstAidCertUploadButton = view.findViewById(R.id.first_aid_cert_image);

        ImageView licenceDisplay = view.findViewById(R.id.licence_card);
        ImageView insuranceDisplay = view.findViewById(R.id.insurance_card);
        ImageView dbsCertDisplay = view.findViewById(R.id.dbs_cert_card);
        ImageView vehicleLicenceDisplay = view.findViewById(R.id.vehicle_licence_card);
        ImageView driversLicenceDisplay = view.findViewById(R.id.drivers_licence_card);
        ImageView safeguardingCertDisplay = view.findViewById(R.id.safe_guarding_cert_card);
        ImageView firstAidCertDisplay = view.findViewById(R.id.first_aid_cert_card);

        uploadButtonToCardMap.put(licenceUploadButton, licenceDisplay);
        uploadButtonToCardMap.put(insuranceUploadButton, insuranceDisplay);
        uploadButtonToCardMap.put(dbsCertUploadButton, dbsCertDisplay);
        uploadButtonToCardMap.put(vehicleLicenceUploadButton, vehicleLicenceDisplay);
        uploadButtonToCardMap.put(driversLicenceUploadButton, driversLicenceDisplay);
        uploadButtonToCardMap.put(safeguardingCertUploadButton, safeguardingCertDisplay);
        uploadButtonToCardMap.put(firstAidCertUploadButton, firstAidCertDisplay);

        uploadButtonToTypeMap.put(licenceUploadButton, 4);
        uploadButtonToTypeMap.put(insuranceUploadButton, 0);
        uploadButtonToTypeMap.put(dbsCertUploadButton, 2);
        uploadButtonToTypeMap.put(vehicleLicenceUploadButton, 3);
        uploadButtonToTypeMap.put(driversLicenceUploadButton, 4);
        uploadButtonToTypeMap.put(safeguardingCertUploadButton, 5);
        uploadButtonToTypeMap.put(firstAidCertUploadButton, 6);

        for (ImageView uploadButton : uploadButtonToCardMap.keySet()) {
            uploadButton.setOnClickListener(v -> {
                selectedUploadButton = uploadButton;
                selectedType = uploadButtonToTypeMap.get(uploadButton);
                openImageOptions(); // Updated to offer both camera and gallery
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
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST);
        } else {
            // Show dialog to choose between camera and gallery
            String[] options = {"Take Photo", "Choose from Gallery"};
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
            builder.setTitle("Select Image Source");
            builder.setItems(options, (dialog, which) -> {
                if (which == 0) {
                    openCamera();
                } else if (which == 1) {
                    openImageChooser();
                }
            });
            builder.setNegativeButton("Cancel", null);
            builder.show();
        }
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivityForResult(intent, CAMERA_REQUEST);
        } else {
            Toast.makeText(getContext(), "No camera app found", Toast.LENGTH_SHORT).show();
        }
    }

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImageOptions(); // Re-show options after permission is granted
            } else {
                Toast.makeText(getContext(), "Camera permission denied", Toast.LENGTH_SHORT).show();
                openImageChooser(); // Fallback to gallery
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && selectedUploadButton != null) {
            ImageView displayImageView = uploadButtonToCardMap.get(selectedUploadButton);
            if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
                imageUri = data.getData();
                if (displayImageView != null) {
                    displayImageView.setImageURI(imageUri);
                    uploadImage(imageUri, selectedType);
                }
            } else if (requestCode == CAMERA_REQUEST && data != null) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    Bitmap bitmap = (Bitmap) extras.get("data");
                    if (bitmap != null) {
                        imageUri = FileUtils.saveBitmapToFile(requireContext(), bitmap);
                        if (displayImageView != null && imageUri != null) {
                            displayImageView.setImageURI(imageUri);
                            uploadImage(imageUri, selectedType);
                        }
                    }
                }
            }
        }
    }

    public void uploadImage(Uri imageUri, int type) {
        if (imageUri == null || type == -1) {
            Toast.makeText(getContext(), "No image selected", Toast.LENGTH_SHORT).show();
            return;
        }

        File file = FileUtils.getFileFromUri(getContext(), imageUri);
        if (file == null || !file.exists()) {
            Toast.makeText(getContext(), "File not found!", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

        UploadDocumentApi uploadDocumentApi = new UploadDocumentApi(getContext());
        uploadDocumentApi.uploadDoc(body, type);

        Toast.makeText(getContext(), "Uploading image...", Toast.LENGTH_SHORT).show();
    }
}