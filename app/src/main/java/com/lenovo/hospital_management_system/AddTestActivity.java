package com.lenovo.hospital_management_system;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddTestActivity extends AppCompatActivity{

    private EditText testNameEditText, testDescriptionEditText, roomNumberEditText;
    private Button saveTestButton;

    private FirebaseDatabase database;
    private DatabaseReference physicalTestRef;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_test);

        database = FirebaseDatabase.getInstance();
        physicalTestRef = database.getReference("physicaltests");

        testNameEditText = findViewById(R.id.testNameEditText);
        testDescriptionEditText = findViewById(R.id.testDescriptionEditText);
        roomNumberEditText = findViewById(R.id.roomNumberEditText);
        saveTestButton = findViewById(R.id.saveTestButton);

        saveTestButton.setOnClickListener(v -> saveTest());
    }

    private void saveTest(){
        String name = testNameEditText.getText().toString().trim();
        String description = testDescriptionEditText.getText().toString().trim();
        String roomNumber = roomNumberEditText.getText().toString().trim();

        if(name.isEmpty() || description.isEmpty() || roomNumber.isEmpty()){
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        //generating a unique key and create the test object
        String testId = physicalTestRef.push().getKey();
        PhysicalTestAdmin test = new PhysicalTestAdmin(name, description, "9 AM - 8 PM", roomNumber);

        physicalTestRef.child(testId).setValue(test).addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Test added successfully", Toast.LENGTH_SHORT).show();
                    finish();
                }).addOnFailureListener(e -> Toast.makeText(this, "Failed to add test", Toast.LENGTH_SHORT).show());
    }
}