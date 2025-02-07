package com.example.ace_taxi_v2.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.ace_taxi_v2.Logic.Service.FileUtils;
import com.example.ace_taxi_v2.Logic.UploadDocumentApi;
import com.example.ace_taxi_v2.R;
import com.google.android.material.appbar.MaterialToolbar;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class UploadDocumentFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private ImageView selectedUploadButton;
    private int selectedType = -1; // Track selected document type

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
        // Upload buttons (icons)
        ImageView licenceUploadButton = view.findViewById(R.id.select_lisence_img);
        ImageView insuranceUploadButton = view.findViewById(R.id.insurance_image);
        ImageView dbsCertUploadButton = view.findViewById(R.id.dbs_cert_image);
        ImageView vehicleLicenceUploadButton = view.findViewById(R.id.vehicle_licence_image);
        ImageView driversLicenceUploadButton = view.findViewById(R.id.drivers_licence_image);
        ImageView safeguardingCertUploadButton = view.findViewById(R.id.safe_guarding_cert_image);
        ImageView firstAidCertUploadButton = view.findViewById(R.id.first_aid_cert_image);

        // Display areas (where the selected image will be set)
        ImageView licenceDisplay = view.findViewById(R.id.licence_card);
        ImageView insuranceDisplay = view.findViewById(R.id.insurance_card);
        ImageView dbsCertDisplay = view.findViewById(R.id.dbs_cert_card);
        ImageView vehicleLicenceDisplay = view.findViewById(R.id.vehicle_licence_card);
        ImageView driversLicenceDisplay = view.findViewById(R.id.drivers_licence_card);
        ImageView safeguardingCertDisplay = view.findViewById(R.id.safe_guarding_cert_card);
        ImageView firstAidCertDisplay = view.findViewById(R.id.first_aid_cert_card);

        // Map upload buttons to corresponding display areas
        uploadButtonToCardMap.put(licenceUploadButton, licenceDisplay);
        uploadButtonToCardMap.put(insuranceUploadButton, insuranceDisplay);
        uploadButtonToCardMap.put(dbsCertUploadButton, dbsCertDisplay);
        uploadButtonToCardMap.put(vehicleLicenceUploadButton, vehicleLicenceDisplay);
        uploadButtonToCardMap.put(driversLicenceUploadButton, driversLicenceDisplay);
        uploadButtonToCardMap.put(safeguardingCertUploadButton, safeguardingCertDisplay);
        uploadButtonToCardMap.put(firstAidCertUploadButton, firstAidCertDisplay);

        // Map upload buttons to document types (0-7)
        uploadButtonToTypeMap.put(licenceUploadButton, 4); // Driver Licence
        uploadButtonToTypeMap.put(insuranceUploadButton, 0); // Insurance
        uploadButtonToTypeMap.put(dbsCertUploadButton, 2); // DBS
        uploadButtonToTypeMap.put(vehicleLicenceUploadButton, 3); // Vehicle Badge
        uploadButtonToTypeMap.put(driversLicenceUploadButton, 4); // Driver Licence
        uploadButtonToTypeMap.put(safeguardingCertUploadButton, 5); // Safe Guarding
        uploadButtonToTypeMap.put(firstAidCertUploadButton, 6); // First Aid Cert

        // Set click listeners on upload buttons
        for (ImageView uploadButton : uploadButtonToCardMap.keySet()) {
            uploadButton.setOnClickListener(v -> {
                selectedUploadButton = uploadButton;
                selectedType = uploadButtonToTypeMap.get(uploadButton);
                openImageChooser();
            });
        }
    }

    private void navigateToProfile() {
        Fragment selectedFragment = new ProfileFragment();
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, selectedFragment);
        fragmentTransaction.commit();
    }

    public void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            if (selectedUploadButton != null) {
                // Get the corresponding display ImageView
                ImageView displayImageView = uploadButtonToCardMap.get(selectedUploadButton);
                if (displayImageView != null) {
                    displayImageView.setImageURI(imageUri); // Set selected image to display area
                    uploadImage(imageUri, selectedType);
                }
            }
        }
    }

    public void uploadImage(Uri imageUri, int type) {
        if (imageUri == null || type == -1) {
            Toast.makeText(getContext(), "No image selected", Toast.LENGTH_SHORT).show();
            return;
        }

        File file = FileUtils.getFileFromUri(getContext(), imageUri); // Convert URI to File
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
