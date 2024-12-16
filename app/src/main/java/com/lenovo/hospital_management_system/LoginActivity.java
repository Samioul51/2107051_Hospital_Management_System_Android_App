package com.lenovo.hospital_management_system;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity{
    private EditText editTextEmail, editTextPassword;
    private FirebaseAuth firebaseAuth;
    private TextView forgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        //initializing firebase authentication
        firebaseAuth = FirebaseAuth.getInstance();

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        forgotPassword = findViewById(R.id.textViewForgotPassword);
        Button buttonLogin = findViewById(R.id.buttonLogin);
        Button buttonAdminLogin = findViewById(R.id.buttonAdminLogin);
        Button buttonOurDoctors = findViewById(R.id.buttonOurDoctors);
        TextView textViewSignUp = findViewById(R.id.textViewSignUp);

        buttonLogin.setOnClickListener(v -> handleUserLogin());
        buttonAdminLogin.setOnClickListener(v -> handleAdminLogin());
        buttonOurDoctors.setOnClickListener(v -> handleOurDoctors());
        textViewSignUp.setOnClickListener(v -> handleSignUp());
        forgotPassword.setOnClickListener(v -> handleForgotPassword());
    }

    private void handleUserLogin(){
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        //validating email and password input
        if(TextUtils.isEmpty(email)){
            editTextEmail.setError("Please enter your email");
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextEmail.setError("Invalid email format");
            return;
        }
        if(TextUtils.isEmpty(password)){
            editTextPassword.setError("Please enter your password");
            return;
        }

        //authenticating user with firebase
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
                    if(task.isSuccessful()){
                        //login successful and navigating to UserDashboardActivity
                        Toast.makeText(LoginActivity.this, "Logged in successfully!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, UserDashboardActivity.class));
                        finish();
                    }
                    else{
                        //login failed
                        Toast.makeText(LoginActivity.this, "Wrong Credentials! ", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void handleAdminLogin(){
        startActivity(new Intent(LoginActivity.this, AdminLoginActivity.class));
    }

    private void handleOurDoctors(){
        startActivity(new Intent(LoginActivity.this, OurDoctorsActivity.class));
    }

    private void handleSignUp(){
        startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
    }
    private void handleForgotPassword() {
        startActivity(new Intent(LoginActivity.this, UserForgotPasswordActivity.class));
    }

    @Override
    public void onBackPressed(){
        //creating and showing an AlertDialog when the user presses the back button
        new AlertDialog.Builder(this).setTitle("Confirm Exit").setMessage("Are you sure you want to quit?").setPositiveButton("Yes", (dialog, which) -> {
                    //if Yes is clicked,exiting the activity
                    super.onBackPressed();
                })
                .setNegativeButton("No", (dialog, which) -> {
                    //if No is clicked,just dismissing the dialog
                    dialog.dismiss();
                })
                .show();
    }
}
