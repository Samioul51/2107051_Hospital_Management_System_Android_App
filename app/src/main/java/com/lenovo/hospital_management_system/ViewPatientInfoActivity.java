package com.lenovo.hospital_management_system;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class ViewPatientInfoActivity extends AppCompatActivity{

    private Spinner patientSpinner;
    private TableLayout patientTable;
    private TextView patientFullName, patientEmail, patientPhone, patientDOB, patientBloodGroup, patientAddress, patientSex;

    private DatabaseReference database;
    private Map<String, String> patientMap;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_patient_info);

        //initializing Firebase reference
        database = FirebaseDatabase.getInstance().getReference("users");

        //initializing the patient map to store patient names and their respective IDs
        patientMap = new HashMap<>();

        //initializing the views
        patientSpinner = findViewById(R.id.patientSpinner);
        patientTable = findViewById(R.id.patientTable);
        patientFullName = findViewById(R.id.patientFullName);
        patientEmail = findViewById(R.id.patientEmail);
        patientPhone = findViewById(R.id.patientPhone);
        patientDOB = findViewById(R.id.patientDOB);
        patientBloodGroup = findViewById(R.id.patientBloodGroup);
        patientAddress = findViewById(R.id.patientAddress);
        patientSex = findViewById(R.id.patientSex);

        //loading patient names into the spinner
        loadPatientNames();

        //setting up the spinner's item selection listener
       patientSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id){
                String selectedPatient = (String) parentView.getItemAtPosition(position);
                if(!selectedPatient.equals("Select a Patient")){
                    showPatientInfo(selectedPatient);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView){
                //will do later
            }
        });
    }

    private void loadPatientNames() {
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot){
                //preparing a list for patient names
                String[] patientNames = new String[(int) snapshot.getChildrenCount() + 1];
                patientNames[0] = "Select a Patient"; // Default option

                int index = 1;
                for(DataSnapshot patientSnapshot : snapshot.getChildren()){
                    String patientName = patientSnapshot.child("fullName").getValue(String.class);
                    String patientId = patientSnapshot.getKey();
                    patientNames[index] = patientName;
                    patientMap.put(patientName, patientId);
                    index++;
                }

                //setting the adapter for the spinner
                ArrayAdapter<String> adapter = new ArrayAdapter<>(ViewPatientInfoActivity.this, android.R.layout.simple_spinner_item, patientNames);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                patientSpinner.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error){
                Toast.makeText(ViewPatientInfoActivity.this, "Failed to load patient names", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showPatientInfo(String patientName){
        //getting the patient ID from the map using the selected name
        String patientId = patientMap.get(patientName);

        if(patientId != null){
            database.child(patientId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    //retrieving patient data from firebase
                    String fullName = snapshot.child("fullName").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    String phone = snapshot.child("phone").getValue(String.class);
                    String dob = snapshot.child("dob").getValue(String.class);
                    String bloodGroup = snapshot.child("bloodGroup").getValue(String.class);
                    String address = snapshot.child("address").getValue(String.class);
                    String sex = snapshot.child("sex").getValue(String.class);

                    //setting the data to the corresponding textviews
                    patientFullName.setText(fullName);
                    patientEmail.setText(email);
                    patientPhone.setText(phone);
                    patientDOB.setText(dob);
                    patientBloodGroup.setText(bloodGroup);
                    patientAddress.setText(address);
                    patientSex.setText(sex);

                    //making the patient info table visible
                    patientTable.setVisibility(View.VISIBLE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error){
                    Toast.makeText(ViewPatientInfoActivity.this, "Failed to retrieve patient data", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
