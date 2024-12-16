package com.lenovo.hospital_management_system;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);

        ImageView imageView1 = findViewById(R.id.imageView1);
        ObjectAnimator animation = ObjectAnimator.ofFloat(imageView1, "TranslationY", 0f, -30f, 30f, 0f);
        animation.setDuration(1000); //for 1-second animation
        animation.setRepeatCount(ObjectAnimator.INFINITE);
        animation.setRepeatMode(ObjectAnimator.REVERSE);
        animation.start();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            //starting checking roles during the splash animation
            checkUserRoleAndRedirect();
        }
        else{
            //showing splash screen for 1.5 seconds if not logged in
            new Handler().postDelayed(this::redirectToLogin, 1500);
        }
    }

    private void checkUserRoleAndRedirect(){
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        //checking if the user is a super admin
        database.child("superadmin").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot){
                if (snapshot.exists()) {
                    //user is a super admin
                    startActivity(new Intent(SplashActivity.this, SuperAdminDashboardActivity.class));
                    finish();
                }
                else{
                    //checking if the user is a doctor
                    checkIfDoctor(database, userId);
                }
            }

            @Override
            public void onCancelled(DatabaseError error){
                redirectToLogin();
            }
        });
    }

    private void checkIfDoctor(DatabaseReference database, String userId){
        database.child("doctors").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot){
                if(snapshot.exists()){
                    //user is a doctor
                    startActivity(new Intent(SplashActivity.this, DoctorDashboardActivity.class));
                    finish();
                }
                else{
                    //checking if the user is a regular user
                    checkIfRegularUser(database, userId);
                }
            }

            @Override
            public void onCancelled(DatabaseError error){
                redirectToLogin();
            }
        });
    }

    private void checkIfRegularUser(DatabaseReference database, String userId){
        database.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot){
                if (snapshot.exists()) {
                    //user is a regular user
                    startActivity(new Intent(SplashActivity.this, UserDashboardActivity.class));
                }
                else{
                    //user data not found, redirecting to login
                    redirectToLogin();
                }
                finish();
            }

            @Override
            public void onCancelled(DatabaseError error){
                redirectToLogin();
            }
        });
    }

    private void redirectToLogin(){
        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
        finish();
    }
}
