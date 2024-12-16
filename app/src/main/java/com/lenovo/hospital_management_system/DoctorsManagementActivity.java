package com.lenovo.hospital_management_system;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class DoctorsManagementActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctors_management);

        //initializing UI elements
        Button addDoctorButton = findViewById(R.id.addDoctorButton);
        Button updateDoctorButton = findViewById(R.id.updateDoctorButton);
        Button deleteDoctorButton = findViewById(R.id.deleteDoctorButton);
        Button assignRoomButton = findViewById(R.id.assignRoomButton);

        //setting button actions
        addDoctorButton.setOnClickListener(v -> startActivity(new Intent(this, AddDoctorActivity.class)));
        updateDoctorButton.setOnClickListener(v -> startActivity(new Intent(this, UpdateDoctorActivity.class)));
        deleteDoctorButton.setOnClickListener(v -> startActivity(new Intent(this, DeleteDoctorActivity.class)));
        assignRoomButton.setOnClickListener(v -> startActivity(new Intent(this, AssignRoomToDoctorActivity.class)));
    }
}
