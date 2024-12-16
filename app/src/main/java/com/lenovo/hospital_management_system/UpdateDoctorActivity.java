package com.lenovo.hospital_management_system;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UpdateDoctorActivity extends AppCompatActivity{

    private Spinner doctorSpinner, schedulerSpinner;
    private EditText nameInput, specialtyInput, roomInput;
    private Button updateDoctorButton, assignSchedulerButton;
    private DatabaseReference doctorDatabase, staffDatabase;
    private ArrayAdapter<String> doctorAdapter, schedulerAdapter;
    private HashMap<String, String> doctorMap, schedulerMap;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_doctor);

        //initializing Firebase
        doctorDatabase = FirebaseDatabase.getInstance().getReference("doctors");
        staffDatabase = FirebaseDatabase.getInstance().getReference("staff");

        //initializing UI elements
        doctorSpinner = findViewById(R.id.doctorSpinner);
        schedulerSpinner = findViewById(R.id.schedulerSpinner);
        nameInput = findViewById(R.id.nameInput);
        specialtyInput = findViewById(R.id.specialtyInput);
        roomInput = findViewById(R.id.roomInput);
        updateDoctorButton = findViewById(R.id.updateDoctorButton);
        assignSchedulerButton = findViewById(R.id.assignSchedulerButton);

        doctorAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>());
        doctorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        doctorSpinner.setAdapter(doctorAdapter);

        schedulerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>());
        schedulerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        schedulerSpinner.setAdapter(schedulerAdapter);

        doctorMap = new HashMap<>();
        schedulerMap = new HashMap<>();

        loadDoctors();
        loadSchedulers();

        //updating specific doctor details
        updateDoctorButton.setOnClickListener(v -> updateDoctorDetails());

        //assigning a scheduler to a doctor
        assignSchedulerButton.setOnClickListener(v -> assignSchedulerToDoctor());
    }

    private void loadDoctors(){
        doctorDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot){
                doctorAdapter.clear();
                doctorMap.clear();
                for (DataSnapshot child : snapshot.getChildren()){
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
                Toast.makeText(UpdateDoctorActivity.this, "Failed to load doctors", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadSchedulers(){
        staffDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot){
                schedulerAdapter.clear();
                schedulerMap.clear();
                for(DataSnapshot child : snapshot.getChildren()){
                    String staffId = child.getKey();
                    String category = child.child("category").getValue(String.class);
                    String staffName = child.child("name").getValue(String.class);
                    if("Doctor Scheduler".equals(category) && staffName != null){
                        schedulerAdapter.add(staffName);
                        schedulerMap.put(staffName, staffId);
                    }
                }
                schedulerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error){
                Toast.makeText(UpdateDoctorActivity.this, "Failed to load schedulers", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateDoctorDetails(){
        String selectedDoctor = (String) doctorSpinner.getSelectedItem();
        if(selectedDoctor == null){
            Toast.makeText(this, "Please select a doctor", Toast.LENGTH_SHORT).show();
            return;
        }

        String doctorId = doctorMap.get(selectedDoctor);
        if(doctorId == null)
            return;

        Map<String, Object> updates = new HashMap<>();
        if(!TextUtils.isEmpty(nameInput.getText().toString().trim())){
            updates.put("name", nameInput.getText().toString().trim());
        }
        if(!TextUtils.isEmpty(specialtyInput.getText().toString().trim())){
            updates.put("specialty", specialtyInput.getText().toString().trim());
        }
        if(!TextUtils.isEmpty(roomInput.getText().toString().trim())){
            updates.put("roomNumber", roomInput.getText().toString().trim());
        }

        if(updates.isEmpty()){
            Toast.makeText(this, "No fields to update", Toast.LENGTH_SHORT).show();
            return;
        }

        doctorDatabase.child(doctorId).updateChildren(updates).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Toast.makeText(this, "Doctor details updated successfully", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(this, "Failed to update doctor details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void assignSchedulerToDoctor(){
        String selectedDoctor = (String) doctorSpinner.getSelectedItem();
        String selectedScheduler = (String) schedulerSpinner.getSelectedItem();

        if(selectedDoctor == null || selectedScheduler == null){
            Toast.makeText(this, "Please select both a doctor and a scheduler", Toast.LENGTH_SHORT).show();
            return;
        }

        String doctorId = doctorMap.get(selectedDoctor);
        String schedulerId = schedulerMap.get(selectedScheduler);

        if(doctorId != null && schedulerId != null){
            doctorDatabase.child(doctorId).child("schedulerId").setValue(schedulerId).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    Toast.makeText(this, "Scheduler assigned to doctor successfully", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(this, "Failed to assign scheduler", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
