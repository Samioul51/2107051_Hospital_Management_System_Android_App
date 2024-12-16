package com.lenovo.hospital_management_system;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditProfileActivity extends AppCompatActivity{

    private EditText fullNameEditText, phoneEditText, addressEditText;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        fullNameEditText = findViewById(R.id.fullNameEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        addressEditText = findViewById(R.id.addressEditText);

        //getting reference to the current user's data
        userRef = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
    }

    //method to update profile information
    public void onUpdateProfile(View view) {
        String fullName = fullNameEditText.getText().toString();
        String phone = phoneEditText.getText().toString();
        String address = addressEditText.getText().toString();

        boolean isUpdated = false;

        //checking if fields have been modified and update them if necessary
        if(!fullName.isEmpty()){
            userRef.child("fullName").setValue(fullName);
            isUpdated = true;
        }

        if(!phone.isEmpty()){
            userRef.child("phone").setValue(phone);
            isUpdated = true;
        }

        if(!address.isEmpty()){
            userRef.child("address").setValue(address);
            isUpdated = true;
        }

        if(isUpdated){
            Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
            finish();
        }
        else{
            Toast.makeText(this, "Please fill at least one field to update", Toast.LENGTH_SHORT).show();
        }

    }
}
