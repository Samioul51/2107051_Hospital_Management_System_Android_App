package com.lenovo.hospital_management_system;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DoctorDashboardActivity extends AppCompatActivity{

    private RecyclerView recyclerViewAppointments;
    private DoctorAppointmentsAdapter appointmentAdapter;
    private List<AppointmentDoctorView> appointmentList;

    private FirebaseDatabase database;
    private DatabaseReference doctorRef, appointmentRef;
    private String doctorId;

    private TextView textDoctorName, textRoomInfo, textSpecialtyInfo;
    private TextView textSchedulerName;
    private Button resetPasswordButton, logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_dashboard);

        //initializing views
        recyclerViewAppointments = findViewById(R.id.recyclerViewAppointments);
        recyclerViewAppointments.setLayoutManager(new LinearLayoutManager(this));

        textDoctorName=findViewById(R.id.textDoctorName);
        textRoomInfo = findViewById(R.id.textRoomInfo);
        textSpecialtyInfo = findViewById(R.id.textSpecialtyInfo);
        textSchedulerName = findViewById(R.id.textSchedulerName);
        resetPasswordButton = findViewById(R.id.resetPasswordButton);
        logoutButton = findViewById(R.id.logoutButton);

        appointmentList = new ArrayList<>();
        appointmentAdapter = new DoctorAppointmentsAdapter(appointmentList);
        recyclerViewAppointments.setAdapter(appointmentAdapter);

        database = FirebaseDatabase.getInstance();
        doctorRef = database.getReference("doctors");
        appointmentRef = database.getReference("appointments");
        doctorId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        fetchDoctorInfo();
        fetchAppointments();

        //resetting password functionality
        resetPasswordButton.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setMessage("Are you sure you want to reset your password?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialog, id) -> {
                        FirebaseAuth.getInstance().sendPasswordResetEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Password reset email sent.", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e -> Toast.makeText(this, "Failed to send reset email.", Toast.LENGTH_SHORT).show());
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        //logout functionality
        logoutButton.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setMessage("Are you sure you want to log out?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialog, id) -> {
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    private void fetchDoctorInfo(){
        doctorRef.child(doctorId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String roomNumber = snapshot.child("roomNumber").getValue(String.class);
                    String specialty = snapshot.child("specialty").getValue(String.class);
                    String schedulerId = snapshot.child("schedulerId").getValue(String.class);
                    String doctorName = snapshot.child("name").getValue(String.class);  // Fetch doctor's name

                    textRoomInfo.setText("Room: " + roomNumber);
                    textSpecialtyInfo.setText("Specialty: " + specialty);
                    textDoctorName.setText("Doctor: " + doctorName);  // Set doctor's name

                    //fetching the scheduler's name using schedulerId
                    fetchSchedulerInfo(schedulerId);
                }
            }

            @Override
            public void onCancelled(DatabaseError error){
                Toast.makeText(DoctorDashboardActivity.this, "Failed to load doctor info", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void fetchSchedulerInfo(String schedulerId){
        if(schedulerId == null || schedulerId.isEmpty()){
            textSchedulerName.setText("Scheduler: Not Assigned");
            return;
        }

        DatabaseReference schedulerRef = database.getReference("staff").child(schedulerId);
        schedulerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot){
                if(snapshot.exists()){
                    String schedulerName = snapshot.child("name").getValue(String.class);
                    textSchedulerName.setText("Scheduler: " + schedulerName);
                }
                else{
                    textSchedulerName.setText("Scheduler: Not Found");
                }
            }

            @Override
            public void onCancelled(DatabaseError error){
                textSchedulerName.setText("Scheduler: Error Loading");
                Toast.makeText(DoctorDashboardActivity.this, "Failed to load scheduler info", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void fetchAppointments(){
        appointmentList.clear();

        Calendar calendar = Calendar.getInstance();
        List<String> datesToFetch = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            String date = String.format("%04d-%02d-%02d",
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.DAY_OF_MONTH));
            datesToFetch.add(date);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        for(String date : datesToFetch){
            appointmentRef.child(doctorId).child(date)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                for(DataSnapshot snap : snapshot.getChildren()){
                                    AppointmentDoctorView appointment = snap.getValue(AppointmentDoctorView.class);
                                    if(appointment != null){
                                        appointmentList.add(appointment);
                                    }
                                }
                            }

                            if(date.equals(datesToFetch.get(datesToFetch.size() - 1))){
                                appointmentAdapter.notifyDataSetChanged();
                                if(appointmentList.isEmpty()){
                                    Toast.makeText(DoctorDashboardActivity.this, "No appointments for the next 7 days", Toast.LENGTH_LONG).show();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError error){
                            Toast.makeText(DoctorDashboardActivity.this, "Error fetching appointments", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    @Override
    public void onBackPressed(){
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to quit?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> finishAffinity())
                .setNegativeButton("No", null)
                .show();
    }
}
