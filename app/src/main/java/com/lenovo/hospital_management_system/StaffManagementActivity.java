package com.lenovo.hospital_management_system;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class StaffManagementActivity extends AppCompatActivity{

    private Button addStaffButton, deleteStaffButton, viewStaffButton, updateStaffButton;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_management);

        //initializing buttons
        addStaffButton = findViewById(R.id.addStaffButton);
        deleteStaffButton = findViewById(R.id.deleteStaffButton);
        viewStaffButton = findViewById(R.id.viewStaffButton);
        updateStaffButton = findViewById(R.id.updateStaffButton);

        //setting button actions
        addStaffButton.setOnClickListener(v -> startActivity(new Intent(this, AddStaffActivity.class)));
        deleteStaffButton.setOnClickListener(v -> startActivity(new Intent(this, DeleteStaffActivity.class)));
        viewStaffButton.setOnClickListener(v -> startActivity(new Intent(this, ViewStaffByCategoryActivity.class)));
        updateStaffButton.setOnClickListener(v -> startActivity(new Intent(this, UpdateStaffActivity.class)));  // Navigate to UpdateStaffActivity
    }
}
