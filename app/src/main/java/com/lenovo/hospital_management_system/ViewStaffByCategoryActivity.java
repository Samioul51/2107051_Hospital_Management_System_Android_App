package com.lenovo.hospital_management_system;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class ViewStaffByCategoryActivity extends AppCompatActivity{

    private Spinner categorySpinner;
    private ListView staffListView;
    private StaffAdapter staffAdapter;
    private List<Staff> staffList;
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_staff_by_category);

        //initializing Firebase
        database = FirebaseDatabase.getInstance().getReference("staff");

        //initializing UI elements
        categorySpinner = findViewById(R.id.categorySpinner);
        staffListView = findViewById(R.id.staffListView);
        staffList = new ArrayList<>();
        staffAdapter = new StaffAdapter(this, staffList);
        staffListView.setAdapter(staffAdapter);

        //populating categories in Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.staff_categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        //setting listener for Spinner item selection
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id){
                loadStaffByCategory();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent){
                staffList.clear();
                staffAdapter.notifyDataSetChanged();
            }
        });
    }

    private void loadStaffByCategory(){
        String category = categorySpinner.getSelectedItem().toString();

        //skipping loading if "Select Staff Type" is chosen
        if(category.equals("Select Staff Type")){
            staffList.clear();
            staffAdapter.notifyDataSetChanged();
            return;
        }

        staffList.clear();

        //query firebase database for staff members in the selected category
        database.orderByChild("category").equalTo(category).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot){
                for(DataSnapshot staffSnapshot : snapshot.getChildren()){
                    Staff staff = staffSnapshot.getValue(Staff.class);
                    if(staff != null){
                        staffList.add(staff);
                    }
                }
                staffAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error){
                Toast.makeText(ViewStaffByCategoryActivity.this, "Failed to load staff", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

