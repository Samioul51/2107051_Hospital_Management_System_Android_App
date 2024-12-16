package com.lenovo.hospital_management_system;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.HashMap;

public class DeleteStaffActivity extends AppCompatActivity{

    private Spinner staffTypeSpinner, staffSpinner;
    private Button deleteStaffButton;
    private DatabaseReference database;
    private ArrayAdapter<String> staffTypeAdapter, staffAdapter;
    private HashMap<String, String> staffMap;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_staff);

        //initializing Firebase
        database = FirebaseDatabase.getInstance().getReference("staff");

        //initializing UI elements
        staffTypeSpinner = findViewById(R.id.staffTypeSpinner);
        staffSpinner = findViewById(R.id.staffSpinner);
        deleteStaffButton = findViewById(R.id.deleteStaffButton);

        staffTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>());
        staffAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>());
        staffTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        staffAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        staffTypeSpinner.setAdapter(staffTypeAdapter);
        staffSpinner.setAdapter(staffAdapter);

        staffMap = new HashMap<>();

        loadStaffTypes();

        staffTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedType = (String) staffTypeSpinner.getSelectedItem();
                if(selectedType != null && !selectedType.equals("Select Staff Type")){
                    loadStaffByType(selectedType);
                }
                else{
                    staffAdapter.clear();
                    staffAdapter.add("Select Staff");
                    staffAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //will do later
            }
        });

        deleteStaffButton.setOnClickListener(v -> showDeleteConfirmation());
    }

    private void loadStaffTypes(){
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                staffTypeAdapter.clear();
                staffTypeAdapter.add("Select Staff Type");
                for(DataSnapshot staffSnapshot : snapshot.getChildren()){
                    String staffType = staffSnapshot.child("category").getValue(String.class); //updated field
                    if(staffType != null && staffTypeAdapter.getPosition(staffType) == -1){
                        staffTypeAdapter.add(staffType);
                    }
                }
                staffTypeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error){
                Toast.makeText(DeleteStaffActivity.this, "Failed to load staff types", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadStaffByType(String type){
        database.orderByChild("category").equalTo(type).addListenerForSingleValueEvent(new ValueEventListener() { //updated field
            @Override
            public void onDataChange(DataSnapshot snapshot){
                staffAdapter.clear();
                staffMap.clear();
                staffAdapter.add("Select Staff");
                for(DataSnapshot staffSnapshot : snapshot.getChildren()){
                    String staffId = staffSnapshot.getKey();
                    String staffName = staffSnapshot.child("name").getValue(String.class);
                    if(staffName != null){
                        staffAdapter.add(staffName);
                        staffMap.put(staffName, staffId);
                    }
                }
                staffAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error){
                Toast.makeText(DeleteStaffActivity.this, "Failed to load staff", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDeleteConfirmation(){
        new AlertDialog.Builder(this)
                .setTitle("Confirm Deletion")
                .setMessage("Are you sure you want to delete this staff?")
                .setPositiveButton("Yes", (dialog, which) -> deleteStaff())
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteStaff(){
        String selectedStaff = (String) staffSpinner.getSelectedItem();
        if(selectedStaff == null || selectedStaff.equals("Select Staff")){
            Toast.makeText(this, "Please select a staff member", Toast.LENGTH_SHORT).show();
            return;
        }

        String staffId = staffMap.get(selectedStaff);
        if(staffId != null){
            database.child(staffId).removeValue().addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    Toast.makeText(this, "Staff deleted successfully", Toast.LENGTH_SHORT).show();
                    loadStaffByType((String) staffTypeSpinner.getSelectedItem());
                    //finish();
                }
                else{
                    Toast.makeText(this, "Failed to delete staff", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
