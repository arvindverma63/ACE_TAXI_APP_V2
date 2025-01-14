package com.example.ace_taxi_v2.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ace_taxi_v2.Logic.LoginManager;
import com.example.ace_taxi_v2.R;

public class LoginActivity extends AppCompatActivity {

    EditText edit_username,edit_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);


        edit_username = (EditText) findViewById(R.id.edit_username);
        edit_password = (EditText) findViewById(R.id.edit_password);
    }
    public void login(View view){

        LoginManager loginManager = new LoginManager(this);
        String username = edit_username.getText().toString();
        String password = edit_password.getText().toString();
        loginManager.login(username,password);

    }
}