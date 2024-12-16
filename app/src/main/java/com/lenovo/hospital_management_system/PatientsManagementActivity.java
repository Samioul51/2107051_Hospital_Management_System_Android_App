package com.lenovo.hospital_management_system;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class PatientsManagementActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patients_management);

        //initializing UI elements(buttons)
        Button viewPatientButton = findViewById(R.id.viewPatientButton);
        Button updatePatientButton = findViewById(R.id.updatePatientButton);
        Button deletePatientButton = findViewById(R.id.deletePatientButton);
        Button viewAppointmentsButton = findViewById(R.id.viewAppointmentsButton);

        //setting button actions
        viewPatientButton.setOnClickListener(v -> startActivity(new Intent(this, ViewPatientInfoActivity.class)));
        updatePatientButton.setOnClickListener(v -> startActivity(new Intent(this, UpdatePatientInfoActivity.class)));
        deletePatientButton.setOnClickListener(v -> startActivity(new Intent(this, DeletePatientActivity.class)));
        viewAppointmentsButton.setOnClickListener(v -> startActivity(new Intent(this, ViewPatientAppointmentsActivity.class)));
    }
}
