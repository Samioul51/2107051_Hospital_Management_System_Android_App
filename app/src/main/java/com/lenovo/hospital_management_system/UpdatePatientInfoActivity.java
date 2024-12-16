package com.lenovo.hospital_management_system;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.*;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class UpdatePatientInfoActivity extends AppCompatActivity{

    private Spinner patientSpinner;
    private EditText nameInput, phoneInput, dobInput, addressInput;
    private DatabaseReference database;
    private ArrayAdapter<String> patientAdapter;
    private HashMap<String, String> patientMap;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_patient_info);

        //initializing Firebase
        database = FirebaseDatabase.getInstance().getReference("users");
        FirebaseAuth auth = FirebaseAuth.getInstance();

        //initializing UI elements
        patientSpinner = findViewById(R.id.patientSpinner);
        nameInput = findViewById(R.id.nameInput);
        phoneInput = findViewById(R.id.phoneInput);
        dobInput = findViewById(R.id.dobInput);
        addressInput = findViewById(R.id.addressInput);
        Button updatePatientButton = findViewById(R.id.updatePatientButton);

        patientAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>());
        patientAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        patientSpinner.setAdapter(patientAdapter);

        patientMap = new HashMap<>();

        loadPatients();

        //setting up DatePickerDialog for Date of Birth input
        dobInput.setOnClickListener(v -> openDatePicker());

        //updating patient details when the button is clicked
        updatePatientButton.setOnClickListener(v -> updatePatientInfo());
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
                Toast.makeText(UpdatePatientInfoActivity.this, "Failed to load patients", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openDatePicker(){
        //getting the current date
        Calendar calendar = Calendar.getInstance();
        int year1 = calendar.get(Calendar.YEAR);
        int month1 = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        //showing DatePickerDialog
        new DatePickerDialog(
                UpdatePatientInfoActivity.this,
                (view, year, month, dayOfMonth) -> {
                    month = month + 1;  // Month starts from 0 in DatePicker
                    String date = dayOfMonth + "/" + month + "/" + year;
                    dobInput.setText(date);  // Set the selected date in EditText
                },
                year1, month1, day
        ).show();
    }

    private void updatePatientInfo(){
        String selectedPatient = (String) patientSpinner.getSelectedItem();
        if(selectedPatient == null){
            Toast.makeText(this, "Please select a patient", Toast.LENGTH_SHORT).show();
            return;
        }

        String name = nameInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();
        String dob = dobInput.getText().toString().trim();
        String address = addressInput.getText().toString().trim();

        String patientId = patientMap.get(selectedPatient);

        //only updating non-empty fields
        Map<String, Object> updates = new HashMap<>();
        if (!TextUtils.isEmpty(name)) updates.put("fullName", name);
        if (!TextUtils.isEmpty(phone)) updates.put("phone", phone);
        if (!TextUtils.isEmpty(dob)) updates.put("dob", dob);  // Store the DOB
        if (!TextUtils.isEmpty(address)) updates.put("address", address);

        if(updates.isEmpty()){
            Toast.makeText(this, "Please fill at least one field", Toast.LENGTH_SHORT).show();
            return;
        }

        //updating the patient data in the database
        database.child(patientId).updateChildren(updates).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Toast.makeText(this, "Patient details updated successfully", Toast.LENGTH_SHORT).show();
                finish();
            }
            else{
                Toast.makeText(this, "Failed to update patient details", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
