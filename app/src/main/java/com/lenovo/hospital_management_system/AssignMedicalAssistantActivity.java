package com.lenovo.hospital_management_system;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class AssignMedicalAssistantActivity extends AppCompatActivity{

    private Spinner testSpinner, assistantSpinner;
    private Button assignAssistantButton;

    private FirebaseDatabase database;
    private DatabaseReference physicalTestRef, staffRef;

    private List<String> testIds = new ArrayList<>();
    private List<String> testNames = new ArrayList<>();
    private List<String> assistantIds = new ArrayList<>();
    private List<String> assistantNames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_medical_assistant);

        //initializing Firebase
        database = FirebaseDatabase.getInstance();
        physicalTestRef = database.getReference("physicaltests");
        staffRef = database.getReference("staff");

        //initializing views
        testSpinner = findViewById(R.id.testSpinner);
        assistantSpinner = findViewById(R.id.assistantSpinner);
        assignAssistantButton = findViewById(R.id.assignAssistantButton);

        //fetching data for spinners
        fetchTests();
        fetchMedicalAssistants();

        //setting button action
        assignAssistantButton.setOnClickListener(v -> assignAssistant());
    }

    private void fetchTests(){
        physicalTestRef.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot snapshot){
                testIds.clear();
                testNames.clear();

                for(DataSnapshot testSnapshot : snapshot.getChildren()){
                    String testId = testSnapshot.getKey();
                    String testName = testSnapshot.child("name").getValue(String.class);

                    if(testId != null && testName != null){
                        testIds.add(testId);
                        testNames.add(testName);
                    }
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(AssignMedicalAssistantActivity.this, android.R.layout.simple_spinner_item, testNames);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                testSpinner.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError error){
                Toast.makeText(AssignMedicalAssistantActivity.this, "Failed to fetch tests", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchMedicalAssistants(){
        staffRef.orderByChild("category").equalTo("Medical Assistant").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot){
                assistantIds.clear();
                assistantNames.clear();

                for(DataSnapshot assistantSnapshot : snapshot.getChildren()){
                    String assistantId = assistantSnapshot.getKey();
                    String assistantName = assistantSnapshot.child("name").getValue(String.class);

                    if(assistantId != null && assistantName != null){
                        assistantIds.add(assistantId);
                        assistantNames.add(assistantName);
                    }
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(AssignMedicalAssistantActivity.this, android.R.layout.simple_spinner_item, assistantNames);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                assistantSpinner.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError error){
                Toast.makeText(AssignMedicalAssistantActivity.this, "Failed to fetch assistants", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void assignAssistant(){
        int testPosition = testSpinner.getSelectedItemPosition();
        int assistantPosition = assistantSpinner.getSelectedItemPosition();

        if(testPosition == Spinner.INVALID_POSITION || assistantPosition == Spinner.INVALID_POSITION){
            Toast.makeText(this, "Please select a test and an assistant", Toast.LENGTH_SHORT).show();
            return;
        }

        String selectedTestId = testIds.get(testPosition);
        String selectedAssistantId = assistantIds.get(assistantPosition);

        physicalTestRef.child(selectedTestId).child("assignedAssistant").setValue(selectedAssistantId)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Medical Assistant assigned", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to assign assistant", Toast.LENGTH_SHORT).show());
    }
}
