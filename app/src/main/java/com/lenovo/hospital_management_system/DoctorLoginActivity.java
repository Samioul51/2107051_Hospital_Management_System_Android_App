package com.lenovo.hospital_management_system;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class DoctorLoginActivity extends AppCompatActivity{

    private static final String TAG = "DoctorLogin";
    private EditText editTextEmail, editTextPassword;
    private FirebaseAuth mAuth;
    private DatabaseReference doctorsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_login);

        //initializing firebase Auth and Realtime Database reference
        mAuth = FirebaseAuth.getInstance();
        doctorsRef = FirebaseDatabase.getInstance().getReference("doctors");

        //initializing UI elements
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        Button buttonLogin = findViewById(R.id.buttonLogin);

        //setting OnClickListener for the login button
        buttonLogin.setOnClickListener(v -> loginDoctor());
    }

    //doctor login logic
    private void loginDoctor(){
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if(email.isEmpty() || password.isEmpty()){
            Toast.makeText(DoctorLoginActivity.this, "Please fill in both fields", Toast.LENGTH_SHORT).show();
            return;
        }

        //authenticating with Firebase using email and password
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if(task.isSuccessful()){
                        //sign-in successful, getting the current user
                        FirebaseUser user = mAuth.getCurrentUser();
                        if(user != null){
                            Log.d(TAG, "Doctor login successful: " + user.getEmail());

                            //checking if the logged-in user is a doctor
                            checkIfDoctor(user.getUid());
                        }
                    }
                    else{
                        //sign-in failed, showing a message to the user
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        Toast.makeText(DoctorLoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //checking if the logged-in user is a doctor in firebase realtime database
    private void checkIfDoctor(String userId) {
        doctorsRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot){
                if(dataSnapshot.exists()){
                    //the user is a doctor, proceeding to Doctor Dashboard
                    Intent intent = new Intent(DoctorLoginActivity.this, DoctorDashboardActivity.class);
                    startActivity(intent);
                    finish(); //closing the login activity
                }
                else{
                    //the user is not a doctor
                    Toast.makeText(DoctorLoginActivity.this, "You are not a registered doctor.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError){
                Toast.makeText(DoctorLoginActivity.this, "Error checking doctor status.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
