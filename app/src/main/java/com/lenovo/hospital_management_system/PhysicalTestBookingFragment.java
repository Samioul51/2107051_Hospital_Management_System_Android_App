package com.lenovo.hospital_management_system;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PhysicalTestBookingFragment extends Fragment{

    private RecyclerView testRecyclerView;
    private PhysicalTestBookingAdapter physicalTestBookingAdapter;
    private ArrayList<PhysicalTest> testList;
    private DatabaseReference testRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.fragment_physical_test_booking, container, false);

        testRecyclerView = rootView.findViewById(R.id.testRecyclerView);
        testRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        testList = new ArrayList<>();
        testRef = FirebaseDatabase.getInstance().getReference().child("physicaltests");

        fetchTests();

        return rootView;
    }

    private void fetchTests(){
        testRef.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                testList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    PhysicalTest test = snapshot.getValue(PhysicalTest.class);
                    if(test != null){
                        test.setTestId(snapshot.getKey());
                        testList.add(test);
                    }
                }

                physicalTestBookingAdapter = new PhysicalTestBookingAdapter(testList, test -> openTestBookingPage(test));
                testRecyclerView.setAdapter(physicalTestBookingAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError){
                Log.e("FirebaseError", "Error fetching data: " + databaseError.getMessage());

                //showing a Toast to inform the user about the failure
                Toast.makeText(getContext(), "Failed to load data. Please try again.", Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void openTestBookingPage(PhysicalTest test) {
        Intent intent = new Intent(getActivity(), PhysicalTestBookingActivity.class);
        intent.putExtra("test_id", test.getTestId());
        intent.putExtra("test_name", test.getName());
        intent.putExtra("test_availability", test.getAvailability());
        intent.putExtra("test_description", test.getDescription());
        startActivity(intent);
    }
}
