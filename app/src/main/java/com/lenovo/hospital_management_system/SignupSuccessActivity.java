package com.lenovo.hospital_management_system;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class SignupSuccessActivity extends AppCompatActivity{

    private ImageView imageViewSuccess;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_success);

        //initialization of views
        MaterialButton btnBackToLogin = findViewById(R.id.btnBackToLogin);
        imageViewSuccess=findViewById(R.id.imageViewSuccess);
        btnBackToLogin.setOnClickListener(v -> goToLogin());
        //to handle back press
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true){
            @Override
            public void handleOnBackPressed(){
                goToLogin();
            }
        });
        successAnimation();
    }

    private void successAnimation(){
        ObjectAnimator animation=ObjectAnimator.ofFloat(imageViewSuccess, "translationY", 0f,-30f,30f,0f);
        animation.setDuration(1500); //1.5 seconds duration
        animation.setRepeatCount(ObjectAnimator.INFINITE);
        animation.setRepeatMode(ObjectAnimator.REVERSE);
        animation.start();
    }

    private void goToLogin(){
        Intent intent = new Intent(SignupSuccessActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}