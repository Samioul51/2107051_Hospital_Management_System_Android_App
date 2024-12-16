package com.lenovo.hospital_management_system;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class ScheduleFragment extends Fragment{

    private RecyclerView upcomingRecyclerView;
    private AppointmentsAdapter upcomingAdapter;
    private ArrayList<AppointmentHistory> upcomingAppointmentsList;
    private DatabaseReference appointmentsRef;
    private DatabaseReference physicalTestAppointmentsRef;
    private String currentUserId;
    private ProgressBar loadingProgressBar;

    public ScheduleFragment() {
        //required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);
        upcomingRecyclerView = view.findViewById(R.id.recycler_upcoming_appointments);
        loadingProgressBar = view.findViewById(R.id.loadingProgressBar);
        upcomingAppointmentsList = new ArrayList<>();
        upcomingRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        upcomingAdapter = new AppointmentsAdapter(getContext(), upcomingAppointmentsList);
        upcomingRecyclerView.setAdapter(upcomingAdapter);
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        appointmentsRef = FirebaseDatabase.getInstance().getReference("appointments");
        physicalTestAppointmentsRef = FirebaseDatabase.getInstance().getReference("physicaltestappointments");
        loadingProgressBar.setVisibility(View.VISIBLE);
        fetchAppointments();
        return view;
    }

    private void fetchAppointments(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String currentDateString = sdf.format(new Date());
        Log.d("ScheduleFragment", "Fetching appointments from: " + currentDateString);
        DatabaseReference doctorsRef = FirebaseDatabase.getInstance().getReference("doctors");
        doctorsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot doctorsSnapshot){
                for (DataSnapshot doctorSnapshot : doctorsSnapshot.getChildren()){
                    String doctorId = doctorSnapshot.getKey();
                    String doctorName = doctorSnapshot.child("name").getValue(String.class);
                    String roomNumber = doctorSnapshot.child("roomNumber").getValue(String.class);
                    DatabaseReference doctorAppointmentsRef = appointmentsRef.child(doctorId);
                    doctorAppointmentsRef.orderByKey().startAt(currentDateString)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dateSnapshot){
                                    for(DataSnapshot appointmentDateSnapshot : dateSnapshot.getChildren()){
                                        for(DataSnapshot appointmentSnapshot : appointmentDateSnapshot.getChildren()){
                                            try{
                                                String userId = appointmentSnapshot.child("userId").getValue(String.class);
                                                if(currentUserId.equals(userId)){
                                                    AppointmentHistory appointment = new AppointmentHistory();
                                                    appointment.setAppointmentId(appointmentSnapshot.getKey());
                                                    appointment.setDoctorId(appointmentSnapshot.child("doctorId").getValue(String.class));
                                                    appointment.setAppointmentDate(appointmentSnapshot.child("appointmentDate").getValue(String.class));
                                                    appointment.setPatientName(appointmentSnapshot.child("patientName").getValue(String.class));
                                                    appointment.setPatientPhone(appointmentSnapshot.child("patientPhone").getValue(String.class));
                                                    appointment.setStatus(appointmentSnapshot.child("status").getValue(String.class));
                                                    appointment.setSession(appointmentSnapshot.child("session").getValue(String.class));
                                                    Long serialLong = appointmentSnapshot.child("serialNumber").getValue(Long.class);
                                                    if(serialLong != null){
                                                        appointment.setSerialNumber(serialLong.intValue());
                                                    }
                                                    appointment.setDoctorName(doctorName);
                                                    appointment.setDoctorRoom(roomNumber);
                                                    upcomingAppointmentsList.add(appointment);
                                                }
                                            }catch (Exception e){
                                                Log.e("ScheduleFragment", "Error processing doctor appointment", e);
                                            }
                                        }
                                    }
                                    upcomingAdapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError){
                                    Log.e("ScheduleFragment", "Failed to load appointments for doctor: " + doctorId, databaseError.toException());
                                }
                            });
                }
                loadingProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError){
                Log.e("ScheduleFragment", "Failed to load doctors", databaseError.toException());
                loadingProgressBar.setVisibility(View.GONE);
                if(getContext() != null){
                    Toast.makeText(getContext(), "Failed to load appointments", Toast.LENGTH_SHORT).show();
                }
            }
        });
        physicalTestAppointmentsRef.orderByChild("appointmentDate").startAt(currentDateString)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot){
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                            try{
                                String userId = snapshot.child("userId").getValue(String.class);
                                if(currentUserId.equals(userId)){
                                    AppointmentHistory appointment = snapshot.getValue(AppointmentHistory.class);
                                    if(appointment != null){
                                        String testId = appointment.getTestId();
                                        if(testId != null){
                                            DatabaseReference testRef = FirebaseDatabase.getInstance().getReference("physicaltests").child(testId);
                                            testRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot testSnapshot) {
                                                    String testName = testSnapshot.child("name").getValue(String.class);
                                                    String roomNumber = testSnapshot.child("roomNumber").getValue(String.class);
                                                    if(testName != null){
                                                        appointment.setTestName(testName);
                                                    }
                                                    if(roomNumber != null){
                                                        appointment.setTestRoom(roomNumber);
                                                    }
                                                    upcomingAppointmentsList.add(appointment);
                                                    upcomingAdapter.notifyDataSetChanged();
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError){
                                                    Log.e("ScheduleFragment", "Failed to load test details", databaseError.toException());
                                                }
                                            });
                                        }
                                    }
                                }
                            }catch (Exception e){
                                Log.e("ScheduleFragment", "Error processing physical test appointment", e);
                            }
                        }
                        loadingProgressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError){
                        Log.e("ScheduleFragment", "Failed to load physical test appointments", databaseError.toException());
                        loadingProgressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Failed to load physical test appointments", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}