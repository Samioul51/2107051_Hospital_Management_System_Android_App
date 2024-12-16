package com.lenovo.hospital_management_system;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.*;

import java.util.HashMap;
import java.util.Map;

public class AddStaffActivity extends AppCompatActivity{

    private EditText nameInput, phoneInput, emailInput, nidInput, addressInput;
    private Spinner categorySpinner;
    private Button addStaffButton;
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_staff);

        //initializing firebase
        database = FirebaseDatabase.getInstance().getReference("staff");

        //initializing UI elements
        nameInput = findViewById(R.id.nameInput);
        phoneInput = findViewById(R.id.phoneInput);
        emailInput = findViewById(R.id.emailInput);
        nidInput = findViewById(R.id.nidInput);
        addressInput = findViewById(R.id.addressInput);
        categorySpinner = findViewById(R.id.categorySpinner);
        addStaffButton = findViewById(R.id.addStaffButton);

        //populating categories in spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.staff_categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        //adding staff
        addStaffButton.setOnClickListener(v -> addStaff());
    }

    private void addStaff(){
        String name = nameInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String nid = nidInput.getText().toString().trim();
        String address = addressInput.getText().toString().trim();
        String category = categorySpinner.getSelectedItem().toString();

        if(TextUtils.isEmpty(name) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(email) || TextUtils.isEmpty(nid) || TextUtils.isEmpty(address) || TextUtils.isEmpty(category)){
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String staffId = database.push().getKey(); //generating unique id for the staff

        Map<String, Object> staffData = new HashMap<>();
        staffData.put("name", name);
        staffData.put("phone", phone);
        staffData.put("email", email);
        staffData.put("nid", nid);
        staffData.put("address", address);
        staffData.put("category", category);

        if(staffId != null){
            database.child(staffId).setValue(staffData).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    Toast.makeText(this, "Staff added successfully", Toast.LENGTH_SHORT).show();
                    finish();
                }
                else{
                    Toast.makeText(this, "Failed to add staff", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
