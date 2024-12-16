package com.lenovo.hospital_management_system;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.HashMap;

public class AssignRoomToDoctorActivity extends AppCompatActivity{

    private Spinner doctorSpinner;
    private EditText roomInput;
    private DatabaseReference database;
    private ArrayAdapter<String> doctorAdapter;
    private HashMap<String, String> doctorMap;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_room_to_doctor);

        //initializing Firebase
        database = FirebaseDatabase.getInstance().getReference("doctors");

        //initializing UI elements
        doctorSpinner = findViewById(R.id.doctorSpinner);
        roomInput = findViewById(R.id.roomInput);
        Button assignRoomButton = findViewById(R.id.assignRoomButton);

        doctorAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>());
        doctorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        doctorSpinner.setAdapter(doctorAdapter);

        doctorMap = new HashMap<>();

        loadDoctors();

        //assigning or changing Room
        assignRoomButton.setOnClickListener(v -> assignRoomToDoctor());
    }

    private void loadDoctors(){
        database.addListenerForSingleValueEvent(new ValueEventListener(){
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
                Toast.makeText(AssignRoomToDoctorActivity.this, "Failed to load doctors", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void assignRoomToDoctor(){
        String selectedDoctor = (String) doctorSpinner.getSelectedItem();
        String room = roomInput.getText().toString().trim();

        if(selectedDoctor == null || TextUtils.isEmpty(room)){
            Toast.makeText(this, "Please select a doctor and enter a room", Toast.LENGTH_SHORT).show();
            return;
        }

        String doctorId = doctorMap.get(selectedDoctor);
        database.child(doctorId).child("roomNumber").setValue(room)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Toast.makeText(this, "Room assigned successfully", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(this, "Failed to assign room", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
