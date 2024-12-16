package com.lenovo.hospital_management_system;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class AdminLoginActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        //buttons initialization
        Button buttonSuperAdmin = findViewById(R.id.buttonSuperAdmin);
        Button buttonDoctor = findViewById(R.id.buttonDoctor);

        //setting OnClickListener for Super Admin Button
        buttonSuperAdmin.setOnClickListener(v -> {
            Log.d("AdminLoginActivity", "SuperAdmin button clicked");
            Intent intent = new Intent(AdminLoginActivity.this, SuperAdminLoginActivity.class);
            startActivity(intent);
            finish(); //finishing this activity if i want to prevent user from returning
        });

        //setting OnClickListener for Doctor button
        buttonDoctor.setOnClickListener(v -> {
            Log.d("AdminLoginActivity", "Doctor button clicked");
            Intent intent = new Intent(AdminLoginActivity.this, DoctorLoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
