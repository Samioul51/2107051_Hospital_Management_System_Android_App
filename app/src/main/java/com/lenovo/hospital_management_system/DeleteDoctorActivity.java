package com.lenovo.hospital_management_system;

import android.os.Bundle;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.HashMap;

public class DeleteDoctorActivity extends AppCompatActivity{

    private Spinner doctorSpinner;
    private Button deleteDoctorButton, unassignSchedulerButton;
    private DatabaseReference doctorDatabase;
    private ArrayAdapter<String> doctorAdapter;
    private HashMap<String, String> doctorMap;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_doctor);

        //initializing Firebase
        doctorDatabase = FirebaseDatabase.getInstance().getReference("doctors");

        //initializing UI elements
        doctorSpinner = findViewById(R.id.doctorSpinner);
        deleteDoctorButton = findViewById(R.id.deleteDoctorButton);
        unassignSchedulerButton = findViewById(R.id.unassignSchedulerButton);

        doctorAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>());
        doctorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        doctorSpinner.setAdapter(doctorAdapter);

        doctorMap = new HashMap<>();

        loadDoctors();

        //setting button actions
        deleteDoctorButton.setOnClickListener(v -> deleteDoctorFromDatabase());
        unassignSchedulerButton.setOnClickListener(v -> unassignScheduler());
    }

    private void loadDoctors(){
        doctorDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot){
                doctorAdapter.clear();
                doctorMap.clear();
                for(DataSnapshot child : snapshot.getChildren()){
                    String doctorId = child.getKey();
                    String doctorName = child.child("name").getValue(String.class);
                    if(doctorName != null){
                        doctorAdapter.add(doctorName);
                        doctorMap.put(doctorName, doctorId);
                    }
                }
                doctorAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error){
                Toast.makeText(DeleteDoctorActivity.this, "Failed to load doctors", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteDoctorFromDatabase(){
        String selectedDoctor = (String) doctorSpinner.getSelectedItem();
        if(selectedDoctor == null){
            Toast.makeText(this, "Please select a doctor", Toast.LENGTH_SHORT).show();
            return;
        }

        String doctorId = doctorMap.get(selectedDoctor);
        if(doctorId != null){
            doctorDatabase.child(doctorId).removeValue().addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    Toast.makeText(this, "Doctor deleted successfully", Toast.LENGTH_SHORT).show();
                    loadDoctors(); //refreshing the spinner
                }
                else{
                    Toast.makeText(this, "Failed to delete doctor from database", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void unassignScheduler(){
        String selectedDoctor = (String) doctorSpinner.getSelectedItem();
        if(selectedDoctor == null){
            Toast.makeText(this, "Please select a doctor", Toast.LENGTH_SHORT).show();
            return;
        }

        String doctorId = doctorMap.get(selectedDoctor);
        if(doctorId != null){
            doctorDatabase.child(doctorId).child("schedulerId").removeValue().addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    Toast.makeText(this, "Scheduler unassigned successfully", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(this, "Failed to unassign scheduler", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
