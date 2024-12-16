package com.lenovo.hospital_management_system;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        //redirecting to SplashActivity
        startActivity(new Intent(this, SplashActivity.class));
        finish();
    }
}
