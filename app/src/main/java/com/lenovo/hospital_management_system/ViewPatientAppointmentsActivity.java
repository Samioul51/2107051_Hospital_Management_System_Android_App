package com.lenovo.hospital_management_system;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ViewPatientAppointmentsActivity extends AppCompatActivity{

    private Spinner patientSpinner;
    private ListView appointmentListView;
    private RadioGroup categorySelector;
    private ArrayAdapter<String> appointmentAdapter;
    private List<String> appointmentList;
    private DatabaseReference database;
    private HashMap<String, String> patientMap;
    private HashMap<String, AppointmentDetails> appointmentDetailsMap;
    private String currentCategory = "doctor"; // Default category

    private static class AppointmentDetails{
        String appointmentId;
        String userId;
        String doctorId;
        String dateKey;

        AppointmentDetails(String appointmentId, String userId, String doctorId, String dateKey){
            this.appointmentId = appointmentId;
            this.userId = userId;
            this.doctorId = doctorId;
            this.dateKey = dateKey;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_patient_appointments);

        //initializing Firebase
        database = FirebaseDatabase.getInstance().getReference();

        //initializing UI elements
        patientSpinner = findViewById(R.id.patientSpinner);
        appointmentListView = findViewById(R.id.appointmentListView);
        categorySelector = findViewById(R.id.categorySelector);
        appointmentList = new ArrayList<>();
        appointmentAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, appointmentList);
        appointmentListView.setAdapter(appointmentAdapter);

        patientMap = new HashMap<>();
        appointmentDetailsMap = new HashMap<>();

        loadPatients();

        categorySelector.setOnCheckedChangeListener((group, checkedId) -> {
            if(checkedId == R.id.radioDoctor){
                currentCategory = "doctor";
            }
            else if(checkedId == R.id.radioPhysicalTest){
                currentCategory = "physicaltest";
            }
            String selectedPatient = (String) patientSpinner.getSelectedItem();
            if(selectedPatient != null && !selectedPatient.equals("Select a patient")){
                loadAppointments(patientMap.get(selectedPatient));
            }
        });

        patientSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id){
                String selectedPatient = (String) patientSpinner.getSelectedItem();
                if(selectedPatient != null && !selectedPatient.equals("Select a patient")){
                    loadAppointments(patientMap.get(selectedPatient));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView){
                appointmentList.clear();
                appointmentAdapter.notifyDataSetChanged();
            }
        });

        appointmentListView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedAppointment = appointmentList.get(position);
            showAppointmentOptions(selectedAppointment);
        });
    }

    private void loadPatients(){
        //adding a default option to select a patient
        List<String> patientList = new ArrayList<>();
        patientList.add("Select a patient");
        ArrayAdapter<String> patientAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, patientList);
        patientAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        patientSpinner.setAdapter(patientAdapter);

        //loading actual patient data from Firebase
        database.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot){
                for(DataSnapshot child : snapshot.getChildren()){
                    String patientId = child.getKey();
                    String name = child.child("fullName").getValue(String.class);
                    if(name != null){
                        patientList.add(name);
                        patientMap.put(name, patientId);
                    }
                }
                patientAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error){
                Toast.makeText(ViewPatientAppointmentsActivity.this, "Failed to load patients", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadAppointments(String patientId){
        appointmentList.clear();
        long currentTimeMillis = System.currentTimeMillis(); // Get current time in milliseconds

        //checking the current category to load the correct data
        if(currentCategory.equals("doctor")){
            //loading doctor appointments
            loadDoctorAppointments(patientId, currentTimeMillis);
        }
        else if(currentCategory.equals("physicaltest")){
            //loading physical test appointments
            loadPhysicalTestAppointments(patientId, currentTimeMillis);
        }
    }

    private void loadDoctorAppointments(String patientId, long currentTimeMillis){
        database.child("appointments").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot){
                appointmentList.clear();
                appointmentDetailsMap.clear();
                boolean hasAppointments = false;

                for(DataSnapshot doctorSnapshot : snapshot.getChildren()){
                    String doctorId = doctorSnapshot.getKey();

                    for(DataSnapshot dateSnapshot : doctorSnapshot.getChildren()){
                        String dateKey = dateSnapshot.getKey();

                        for(DataSnapshot appointmentSnapshot : dateSnapshot.getChildren()){
                            String userId = appointmentSnapshot.child("userId").getValue(String.class);
                            String appointmentDate = appointmentSnapshot.child("appointmentDate").getValue(String.class);
                            String appointmentId = appointmentSnapshot.getKey();

                            if(userId != null && userId.equals(patientId) && isUpcomingAppointment(appointmentDate, currentTimeMillis)){
                                hasAppointments = true;

                                database.child("doctors").child(doctorId).child("name")
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot doctorNameSnapshot){
                                                String doctorName = doctorNameSnapshot.getValue(String.class);

                                                String session = appointmentSnapshot.child("session").getValue(String.class);
                                                Long serialNumber = appointmentSnapshot.child("serialNumber").getValue(Long.class);

                                                String appointmentDetails = "Doctor: " + doctorName +
                                                        "\nDate: " + appointmentDate +
                                                        "\nSession: " + session +
                                                        "\nSerial: " + (serialNumber != null ? serialNumber : "N/A");

                                                appointmentList.add(appointmentDetails);
                                                //storing complete appointment details
                                                appointmentDetailsMap.put(appointmentDetails,
                                                        new AppointmentDetails(appointmentId, userId, doctorId, dateKey));
                                                appointmentAdapter.notifyDataSetChanged();
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error){
                                                Toast.makeText(ViewPatientAppointmentsActivity.this, "Failed to load doctor's name", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }
                    }
                }

                if(!hasAppointments){
                    appointmentList.add("No appointments scheduled");
                    appointmentAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error){
                Toast.makeText(ViewPatientAppointmentsActivity.this, "Failed to load doctor appointments", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadPhysicalTestAppointments(String patientId, long currentTimeMillis){
        database.child("physicaltestappointments")
                .orderByChild("userId").equalTo(patientId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot){
                        final boolean[] hasAppointments = {false}; //using a single element array to track appointments
                        appointmentList.clear();
                        appointmentDetailsMap.clear();

                        for(DataSnapshot appointmentSnapshot : snapshot.getChildren()){
                            String appointmentDate = appointmentSnapshot.child("appointmentDate").getValue(String.class);
                            String testId = appointmentSnapshot.child("testId").getValue(String.class);
                            String appointmentId = appointmentSnapshot.getKey(); //getting the appointment ID

                            if(isUpcomingAppointment(appointmentDate, currentTimeMillis)){
                                hasAppointments[0] = true; //updating the array value

                                database.child("physicaltests").child(testId).child("name")
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot testNameSnapshot){
                                                String testName = testNameSnapshot.getValue(String.class);

                                                String appointmentDetails = "Test: " + (testName != null ? testName : "Unknown") +
                                                        "\nDate: " + appointmentDate +
                                                        "\nTime: 9 AM - 8 PM";

                                                //adding to the appointment list for display
                                                appointmentList.add(appointmentDetails);
                                                //storing the appointment in appointmentDetailsMap
                                                appointmentDetailsMap.put(appointmentDetails,
                                                        new AppointmentDetails(appointmentId, patientId, null, null)); // doctorId and dateKey are null for physical tests
                                                appointmentAdapter.notifyDataSetChanged();
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error){
                                                Toast.makeText(ViewPatientAppointmentsActivity.this,
                                                        "Failed to load test name", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }

                        //if no appointments were found, will show "No appointments scheduled"
                        if(!hasAppointments[0]){
                            appointmentList.add("No appointments scheduled");
                            appointmentAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error){
                        Toast.makeText(ViewPatientAppointmentsActivity.this,
                                "Failed to load physical test appointments", Toast.LENGTH_SHORT).show();
                    }
                });
    }



    private boolean isUpcomingAppointment(String appointmentDate, long currentTimeMillis){
        try{
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setLenient(false);
            long appointmentTimeMillis = sdf.parse(appointmentDate).getTime();
            return appointmentTimeMillis >= currentTimeMillis;
        } catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    private void showAppointmentOptions(String selectedAppointment){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Appointment Options");
        builder.setItems(new String[]{"Edit", "Delete"}, (dialog, which) -> {
            if(which == 0){
                editAppointment(selectedAppointment);
            }
            else if(which == 1){
                deleteAppointment(selectedAppointment);
            }
        });
        builder.show();
    }

    private void deleteAppointment(String selectedAppointment){
        //showing confirmation dialog before deleting
        new AlertDialog.Builder(this)
                .setTitle("Confirm Deletion")
                .setMessage("Are you sure you want to delete this appointment?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    //retrieving the appointment details
                    AppointmentDetails details = appointmentDetailsMap.get(selectedAppointment);

                    if(details == null){
                        Toast.makeText(this, "Failed to identify the appointment for deletion", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    DatabaseReference appointmentRef;
                    if(currentCategory.equals("doctor")){
                        //for doctor appointments, used the nested structure
                        appointmentRef = database.child("appointments")
                                .child(details.doctorId)
                                .child(details.dateKey)
                                .child(details.appointmentId);
                    }
                    else{
                        //for physical test appointments
                        appointmentRef = database.child("physicaltestappointments")
                                .child(details.appointmentId);
                    }

                    appointmentRef.removeValue().addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            Toast.makeText(this, "Appointment deleted successfully", Toast.LENGTH_SHORT).show();
                            String selectedPatient = (String) patientSpinner.getSelectedItem();
                            if(selectedPatient != null && !selectedPatient.equals("Select a patient")){
                                loadAppointments(patientMap.get(selectedPatient));
                            }
                        }
                        else{
                            Toast.makeText(this, "Failed to delete appointment", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void editAppointment(String selectedAppointment){
        AppointmentDetails details = appointmentDetailsMap.get(selectedAppointment);

        if(details == null){
            Toast.makeText(this, "Failed to identify the appointment for editing", Toast.LENGTH_SHORT).show();
            return;
        }

        if(currentCategory.equals("physicaltest")){
            //physical test appointment editing
            View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_appointment, null);
            EditText appointmentDateField = dialogView.findViewById(R.id.editAppointmentDate);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Edit Physical Test Appointment")
                    .setView(dialogView)
                    .setPositiveButton("Save", (dialog, which) -> {
                        String newDate = appointmentDateField.getText().toString().trim();

                        if(newDate.isEmpty()){
                            Toast.makeText(this, "Date cannot be empty", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        DatabaseReference appointmentRef = database.child("physicaltestappointments").child(details.appointmentId);
                        appointmentRef.child("appointmentDate").setValue(newDate)
                                .addOnCompleteListener(task -> {
                                    if(task.isSuccessful()){
                                        Toast.makeText(this, "Appointment updated successfully", Toast.LENGTH_SHORT).show();
                                        loadAppointments(details.userId);
                                    }
                                    else{
                                        Toast.makeText(this, "Failed to update appointment", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    })
                    .setNegativeButton("Cancel", null)
                    .create()
                    .show();
        }
        else{
            //doctor appointment editing
            View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_appointment, null);
            EditText appointmentDateField = dialogView.findViewById(R.id.editAppointmentDate);
            Spinner sessionSpinner = dialogView.findViewById(R.id.sessionSpinner);

            ArrayAdapter<CharSequence> sessionAdapter = ArrayAdapter.createFromResource(
                    this,
                    R.array.sessions_array,
                    android.R.layout.simple_spinner_item
            );
            sessionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sessionSpinner.setAdapter(sessionAdapter);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Edit Doctor Appointment")
                    .setView(dialogView)
                    .setPositiveButton("Save", (dialog, which) -> {
                        String newDate = appointmentDateField.getText().toString().trim();
                        String newSession = sessionSpinner.getSelectedItem().toString();

                        if(newDate.isEmpty()){
                            Toast.makeText(this, "Date cannot be empty", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        //updating the nested doctor appointment structure
                        DatabaseReference appointmentRef = database.child("appointments")
                                .child(details.doctorId)
                                .child(details.dateKey)
                                .child(details.appointmentId);

                        //creating a map to update multiple children at once
                        HashMap<String, Object> updateMap = new HashMap<>();
                        updateMap.put("appointmentDate", newDate);
                        updateMap.put("session", newSession);

                        appointmentRef.updateChildren(updateMap)
                                .addOnCompleteListener(task -> {
                                    if(task.isSuccessful()){
                                        Toast.makeText(this, "Appointment updated successfully", Toast.LENGTH_SHORT).show();
                                        loadAppointments(details.userId);
                                    }
                                    else{
                                        Toast.makeText(this, "Failed to update appointment", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    })
                    .setNegativeButton("Cancel", null)
                    .create()
                    .show();
        }
    }

}
