package com.lenovo.hospital_management_system;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class PhysicalTestManagementActivity extends AppCompatActivity{

    private Button addTestButton, updateTestButton, assignAssistantButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_physical_test_management);

        //initializing buttons
        addTestButton = findViewById(R.id.addTestButton);
        updateTestButton = findViewById(R.id.updateTestButton);
        assignAssistantButton = findViewById(R.id.assignAssistantButton);

        //setting button actions
        addTestButton.setOnClickListener(v -> startActivity(new Intent(this, AddTestActivity.class)));
        updateTestButton.setOnClickListener(v -> startActivity(new Intent(this, UpdateTestActivity.class)));
        assignAssistantButton.setOnClickListener(v -> startActivity(new Intent(this, AssignMedicalAssistantActivity.class)));
    }
}
