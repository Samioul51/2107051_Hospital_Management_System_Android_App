package com.lenovo.hospital_management_system;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ViewDoctorAppointmentsActivity extends AppCompatActivity{

    private Spinner doctorSpinner;
    private ListView appointmentListView;
    private ArrayAdapter<String> appointmentAdapter;
    private List<String> appointmentList;
    private DatabaseReference database;
    private HashMap<String, String> doctorMap;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_doctor_appointments);

        //initializing Firebase
        database = FirebaseDatabase.getInstance().getReference("appointments");

        //initializing UI elements
        doctorSpinner = findViewById(R.id.doctorSpinner);
        appointmentListView = findViewById(R.id.appointmentListView);

        appointmentList = new ArrayList<>();
        appointmentAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, appointmentList);
        appointmentListView.setAdapter(appointmentAdapter);

        doctorMap = new HashMap<>();

        loadDoctors();

        //loading appointments when a doctor is selected
        doctorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id){
                String selectedDoctor = (String) doctorSpinner.getSelectedItem();
                if(selectedDoctor != null){
                    loadAppointments(doctorMap.get(selectedDoctor));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView){
                appointmentList.clear();
                appointmentAdapter.notifyDataSetChanged();
            }
        });
    }

    private void loadDoctors(){
        database.child("doctors").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot){
                List<String> doctorList = new ArrayList<>();
                for(DataSnapshot child : snapshot.getChildren()){
                    String doctorId = child.getKey();
                    String doctorName = child.child("name").getValue(String.class);
                    if(doctorName != null){
                        doctorList.add(doctorName);
                        doctorMap.put(doctorName, doctorId);
                    }
                }
                ArrayAdapter<String> doctorAdapter = new ArrayAdapter<>(ViewDoctorAppointmentsActivity.this, android.R.layout.simple_spinner_item, doctorList);
                doctorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                doctorSpinner.setAdapter(doctorAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error){
                Toast.makeText(ViewDoctorAppointmentsActivity.this, "Failed to load doctors", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadAppointments(String doctorId){
        appointmentList.clear();

        database.child(doctorId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot appointmentSnapshot : snapshot.getChildren()){
                    String date = appointmentSnapshot.child("appointmentDate").getValue(String.class);
                    String session = appointmentSnapshot.child("session").getValue(String.class);
                    appointmentList.add("Date: " + date + "\nSession: " + session);
                }
                appointmentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error){
                Toast.makeText(ViewDoctorAppointmentsActivity.this, "Failed to load appointments", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
