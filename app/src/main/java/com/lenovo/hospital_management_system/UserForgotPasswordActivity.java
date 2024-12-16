package com.lenovo.hospital_management_system;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class UserForgotPasswordActivity extends AppCompatActivity{
    private EditText emailEditText;
    private Button resetPasswordButton, loginButton;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_forgot_password);

        emailEditText = findViewById(R.id.emailEditText);
        resetPasswordButton = findViewById(R.id.resetPasswordButton);
        loginButton = findViewById(R.id.loginButton);
        auth = FirebaseAuth.getInstance();

        resetPasswordButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            if(TextUtils.isEmpty(email)){
                Toast.makeText(this, "Please enter your email address", Toast.LENGTH_SHORT).show();
                return;
            }
            auth.sendPasswordResetEmail(email).addOnSuccessListener(unused ->
                            Toast.makeText(this, "Password reset email sent", Toast.LENGTH_SHORT).show()).addOnFailureListener(e -> {
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });

        loginButton.setOnClickListener(v -> {
            Intent intent = new Intent(UserForgotPasswordActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}