package com.lenovo.hospital_management_system;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UpdateStaffActivity extends AppCompatActivity{

    private Spinner categorySpinner, staffSpinner;
    private EditText nameInput, phoneInput, emailInput, nidInput, addressInput;
    private Button updateStaffButton;
    private DatabaseReference database;
    private HashMap<String, String> staffMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_staff);

        //initializing Firebase
        database = FirebaseDatabase.getInstance().getReference("staff");

        //initializing UI elements
        categorySpinner = findViewById(R.id.categorySpinner);
        staffSpinner = findViewById(R.id.staffSpinner);
        nameInput = findViewById(R.id.nameInput);
        phoneInput = findViewById(R.id.phoneInput);
        emailInput = findViewById(R.id.emailInput);
        nidInput = findViewById(R.id.nidInput);
        addressInput = findViewById(R.id.addressInput);
        updateStaffButton = findViewById(R.id.updateStaffButton);

        staffMap = new HashMap<>();

        //populating category spinner
        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(this,
                R.array.staff_categories, android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        //setting listener for category spinner to populate staffSpinner
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadStaffForCategory(categorySpinner.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent){
                staffSpinner.setAdapter(null);
            }
        });

        //setting listener for update staff button
        updateStaffButton.setOnClickListener(v -> updateStaff());
    }

    private void loadStaffForCategory(String category){
        database.orderByChild("category").equalTo(category).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot){
                ArrayList<String> staffList = new ArrayList<>();
                staffMap.clear();
                for(DataSnapshot staffSnapshot : snapshot.getChildren()){
                    String staffName = staffSnapshot.child("name").getValue(String.class);
                    String staffId = staffSnapshot.getKey();
                    if(staffName != null){
                        staffList.add(staffName);
                        staffMap.put(staffName, staffId);
                    }
                }

                ArrayAdapter<String> staffAdapter = new ArrayAdapter<>(UpdateStaffActivity.this, android.R.layout.simple_spinner_item, staffList);
                staffAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                staffSpinner.setAdapter(staffAdapter);
            }

            @Override
            public void onCancelled(DatabaseError error){
                Toast.makeText(UpdateStaffActivity.this, "Failed to load staff", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateStaff(){
        String selectedStaff = staffSpinner.getSelectedItem().toString();
        String staffId = staffMap.get(selectedStaff);

        if(staffId == null){
            Toast.makeText(this, "Please select a staff member", Toast.LENGTH_SHORT).show();
            return;
        }

        String name = nameInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String nid = nidInput.getText().toString().trim();
        String address = addressInput.getText().toString().trim();

        Map<String, Object> staffData = new HashMap<>();
        if(!name.isEmpty())
            staffData.put("name", name);
        if(!phone.isEmpty())
            staffData.put("phone", phone);
        if(!email.isEmpty())
            staffData.put("email", email);
        if(!nid.isEmpty())
            staffData.put("nid", nid);
        if(!address.isEmpty())
            staffData.put("address", address);

        if(!staffData.isEmpty()){
            database.child(staffId).updateChildren(staffData).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    Toast.makeText(this, "Staff updated successfully", Toast.LENGTH_SHORT).show();
                    finish();
                }
                else{
                    Toast.makeText(this, "Failed to update staff", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else{
            Toast.makeText(this, "Please provide at least one detail to update", Toast.LENGTH_SHORT).show();
        }
    }
}
