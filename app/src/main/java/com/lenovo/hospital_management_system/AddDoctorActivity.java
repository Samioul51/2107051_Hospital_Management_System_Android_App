package com.lenovo.hospital_management_system;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.HashMap;
import java.util.Map;

public class AddDoctorActivity extends AppCompatActivity{

    private EditText nameInput, specialtyInput, roomInput, emailInput, passwordInput;
    private DatabaseReference database;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_doctor);

        //initializing firebase
        database = FirebaseDatabase.getInstance().getReference("doctors");
        auth = FirebaseAuth.getInstance();

        //initializing UI elements
        nameInput = findViewById(R.id.nameInput);
        specialtyInput = findViewById(R.id.specialtyInput);
        roomInput = findViewById(R.id.roomInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        Button addDoctorButton = findViewById(R.id.addDoctorButton);

        //adding doctor action
        addDoctorButton.setOnClickListener(v -> addDoctor());
    }

    private void addDoctor(){
        String name = nameInput.getText().toString().trim();
        String specialty = specialtyInput.getText().toString().trim();
        String room = roomInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if(TextUtils.isEmpty(name) || TextUtils.isEmpty(specialty) || TextUtils.isEmpty(room) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        //adding doctor to Firebase Authentication
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                String doctorId = task.getResult().getUser().getUid();
                addDoctorToDatabase(doctorId, name, specialty, room);
            }
            else{
                Toast.makeText(this, "Failed to create account: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addDoctorToDatabase(String doctorId, String name, String specialty, String room){
        Map<String, Object> doctorData = new HashMap<>();
        doctorData.put("name", name);
        doctorData.put("specialty", specialty);
        doctorData.put("roomNumber", room);

        database.child(doctorId).setValue(doctorData).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Toast.makeText(this, "Doctor added successfully", Toast.LENGTH_SHORT).show();
                //clearInputs();
                finish();
            }
            else{
                Toast.makeText(this, "Failed to save doctor: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
