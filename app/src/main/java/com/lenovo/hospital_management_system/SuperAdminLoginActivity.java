package com.lenovo.hospital_management_system;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SuperAdminLoginActivity extends AppCompatActivity{

    private static final String TAG = "SuperAdminLogin";
    private EditText editTextEmail, editTextPassword;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_super_admin_login);

        //initializing Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        //initializing UI elements
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        Button buttonLogin = findViewById(R.id.buttonLogin);

        //setting OnClickListener for the login button
        buttonLogin.setOnClickListener(v -> loginSuperAdmin());
    }

    //Super Admin login logic
    private void loginSuperAdmin(){
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if(email.isEmpty() || password.isEmpty()){
            Toast.makeText(SuperAdminLoginActivity.this, "Please fill in both fields", Toast.LENGTH_SHORT).show();
            return;
        }

        //authenticating with Firebase using email and password
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if(task.isSuccessful()){
                        //sign-in successful, redirecting to the SuperAdmin Dashboard
                        FirebaseUser user = mAuth.getCurrentUser();
                        if(user != null){
                            Log.d(TAG, "SuperAdmin login successful: " + user.getEmail());
                            //starting SuperAdmin Dashboard Activity
                            Intent intent = new Intent(SuperAdminLoginActivity.this, SuperAdminDashboardActivity.class);
                            startActivity(intent);
                            finish(); //closing the login activity
                        }
                    }
                    else{
                        //sign-in failed, showing a message to the user
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        Toast.makeText(SuperAdminLoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
