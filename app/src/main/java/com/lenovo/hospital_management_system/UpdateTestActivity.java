package com.lenovo.hospital_management_system;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdateTestActivity extends AppCompatActivity{

    private Spinner testSpinner;
    private EditText testNameEditText, testDescriptionEditText, roomNumberEditText;
    private Button updateTestButton;

    private FirebaseDatabase database;
    private DatabaseReference physicalTestRef;
    private Map<String, String> testNameToIdMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_test);

        database = FirebaseDatabase.getInstance();
        physicalTestRef = database.getReference("physicaltests");

        testSpinner = findViewById(R.id.testSpinner);
        testNameEditText = findViewById(R.id.testNameEditText);
        testDescriptionEditText = findViewById(R.id.testDescriptionEditText);
        roomNumberEditText = findViewById(R.id.roomNumberEditText);
        updateTestButton = findViewById(R.id.updateTestButton);

        updateTestButton.setOnClickListener(v -> updateTest());

        populateTestSpinner();
    }

    private void populateTestSpinner(){
        physicalTestRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot){
                testNameToIdMap.clear();
                List<String> testNames = new ArrayList<>();

                for(DataSnapshot testSnapshot : snapshot.getChildren()){
                    String testId = testSnapshot.getKey();
                    String testName = testSnapshot.child("name").getValue(String.class);
                    if(testName != null){
                        testNames.add(testName);
                        testNameToIdMap.put(testName, testId);
                    }
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        UpdateTestActivity.this,
                        android.R.layout.simple_spinner_item,
                        testNames
                );
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                testSpinner.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error){
                Toast.makeText(UpdateTestActivity.this,
                        "Failed to load test names",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateTest(){
        if(testNameToIdMap.isEmpty()){
            Toast.makeText(this, "No tests available", Toast.LENGTH_SHORT).show();
            return;
        }

        String selectedTestName = (String) testSpinner.getSelectedItem();
        String selectedTestId = testNameToIdMap.get(selectedTestName);

        String newTestName = testNameEditText.getText().toString().trim();
        String newTestDescription = testDescriptionEditText.getText().toString().trim();
        String newRoomNumber = roomNumberEditText.getText().toString().trim();

        //checking if at least one field is filled
        if(newTestName.isEmpty() && newTestDescription.isEmpty() && newRoomNumber.isEmpty()){
            Toast.makeText(this, "Please fill at least one field to update", Toast.LENGTH_SHORT).show();
            return;
        }

        //creating a map to store updates
        Map<String, Object> updates = new HashMap<>();

        //adding non empty fields to updates
        if (!newTestName.isEmpty()){
            updates.put("name", newTestName);
        }
        if(!newTestDescription.isEmpty()){
            updates.put("description", newTestDescription);
        }
        if(!newRoomNumber.isEmpty()){
            updates.put("roomNumber", newRoomNumber);
        }

        //performing update
        physicalTestRef.child(selectedTestId).updateChildren(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Test updated successfully", Toast.LENGTH_SHORT).show();
                    //clearing input fields
                    testNameEditText.setText("");
                    testDescriptionEditText.setText("");
                    roomNumberEditText.setText("");
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to update test", Toast.LENGTH_SHORT).show());
    }
}