package com.example.ace_taxi_v2.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.ace_taxi_v2.Fragments.ProfileFragment;
import com.example.ace_taxi_v2.R;
import com.google.android.material.appbar.MaterialToolbar;

public class UploadDocumentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_document);

        // Set up the toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar_header);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UploadDocumentActivity.this,HomeActivity.class);
                startActivity(intent);
            }
        });
    }

}
