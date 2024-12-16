package com.lenovo.hospital_management_system;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DoctorBookingFragment extends Fragment{

    private RecyclerView doctorRecyclerView;
    private DoctorBookingAdapter doctorBookingAdapter;
    private ArrayList<Doctor> doctorList;
    private DatabaseReference doctorRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.fragment_doctor_booking, container, false);

        doctorRecyclerView = rootView.findViewById(R.id.doctorRecyclerView);
        doctorRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        doctorList = new ArrayList<>();
        doctorRef = FirebaseDatabase.getInstance().getReference().child("doctors");

        fetchDoctors();

        return rootView;
    }

    private void fetchDoctors(){
        doctorRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                doctorList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Doctor doctor = snapshot.getValue(Doctor.class);
                    if(doctor != null){
                        doctor.setId(snapshot.getKey());
                        doctorList.add(doctor);
                    }
                }

                doctorBookingAdapter = new DoctorBookingAdapter(doctorList, doctor -> openBookingPage(doctor));
                doctorRecyclerView.setAdapter(doctorBookingAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError){
                //error handling
            }
        });
    }

    private void openBookingPage(Doctor doctor){
        Intent intent = new Intent(getActivity(), AppointmentBookingActivity.class);
        intent.putExtra("doctor_id", doctor.getId());
        intent.putExtra("doctor_name", doctor.getName());
        intent.putExtra("doctor_specialty", doctor.getSpecialty());
        startActivity(intent);
    }
}
