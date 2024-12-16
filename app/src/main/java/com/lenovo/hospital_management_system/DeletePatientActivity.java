package com.lenovo.hospital_management_system;

import android.os.Bundle;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.HashMap;

public class DeletePatientActivity extends AppCompatActivity{

    private Spinner patientSpinner;
    private Button deletePatientButton;
    private DatabaseReference database;
    private ArrayAdapter<String> patientAdapter;
    private HashMap<String, String> patientMap;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_patient);

        //initializing Firebase
        database = FirebaseDatabase.getInstance().getReference("users");

        //initializing UI elements
        patientSpinner = findViewById(R.id.patientSpinner);
        deletePatientButton = findViewById(R.id.deletePatientButton);

        patientAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>());
        patientAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        patientSpinner.setAdapter(patientAdapter);

        patientMap = new HashMap<>();

        loadPatients();

        //deleting selected patient
        deletePatientButton.setOnClickListener(v -> showDeleteConfirmationDialog());
    }

    private void loadPatients(){
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot){
                patientAdapter.clear();
                patientMap.clear();
                for(DataSnapshot child : snapshot.getChildren()){
                    String patientId = child.getKey();
                    String name = child.child("fullName").getValue(String.class);
                    if(name != null){
                        patientAdapter.add(name);
                        patientMap.put(name, patientId);
                    }
                }
                patientAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error){
                Toast.makeText(DeletePatientActivity.this, "Failed to load patients", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDeleteConfirmationDialog(){
        String selectedPatient = (String) patientSpinner.getSelectedItem();
        if(selectedPatient == null){
            Toast.makeText(this, "Please select a patient", Toast.LENGTH_SHORT).show();
            return;
        }

        //showing confirmation dialog
        new AlertDialog.Builder(this)
                .setTitle("Confirm Delete")
                .setMessage("Are you sure you want to delete the patient?")
                .setPositiveButton("Yes", (dialog, which) -> deletePatient(selectedPatient))
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void deletePatient(String selectedPatient){
        String patientId = patientMap.get(selectedPatient);
        if(patientId != null){
            database.child(patientId).removeValue().addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    Toast.makeText(this, "Patient deleted successfully", Toast.LENGTH_SHORT).show();
                    finish();
                }
                else{
                    Toast.makeText(this, "Failed to delete patient", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
