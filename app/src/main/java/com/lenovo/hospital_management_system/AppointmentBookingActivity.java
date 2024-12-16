package com.lenovo.hospital_management_system;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class AppointmentBookingActivity extends AppCompatActivity{

    private EditText editTextDate;
    private EditText editTextPatientName;
    private EditText editTextPatientPhone;
    private RadioGroup sessionRadioGroup;

    private DatabaseReference appointmentsRef;
    private String doctorId;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_booking);

        //initializing views
        EditText editTextDoctorName = findViewById(R.id.editTextDoctorName);
        editTextDate = findViewById(R.id.editTextDate);
        editTextPatientName = findViewById(R.id.editTextPatientName);
        editTextPatientPhone = findViewById(R.id.editTextPatientPhone);
        sessionRadioGroup = findViewById(R.id.sessionRadioGroup);
        Button btnBookAppointment = findViewById(R.id.btnBookAppointment);

        //getting data passed from previous activity(doctor information)
        doctorId = getIntent().getStringExtra("doctor_id");
        String doctorName = getIntent().getStringExtra("doctor_name");

        //setting doctor's name(read only)
        editTextDoctorName.setText(doctorName);

        //firebase database reference
        appointmentsRef = FirebaseDatabase.getInstance().getReference("appointments");

        //date picker for selecting appointment date
        editTextDate.setOnClickListener(v -> showDatePickerDialog());

        //handling book appointment button click
        btnBookAppointment.setOnClickListener(v -> bookAppointment());
    }

    private void showDatePickerDialog(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 3); //start date is 3 days from today
        long minDate = calendar.getTimeInMillis();

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(year, month, dayOfMonth);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    editTextDate.setText(sdf.format(selectedDate.getTime()));
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.getDatePicker().setMinDate(minDate);
        calendar.add(Calendar.DAY_OF_MONTH, 3); //allowing only up to 3 days of booking
        datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
        datePickerDialog.show();
    }

    private void bookAppointment(){
        //validating input fields
        String appointmentDate = editTextDate.getText().toString();
        String patientName = editTextPatientName.getText().toString().trim();
        String patientPhone = editTextPatientPhone.getText().toString().trim();
        int selectedSessionId = sessionRadioGroup.getCheckedRadioButtonId();

        if (appointmentDate.isEmpty() || patientName.isEmpty() || patientPhone.isEmpty() || selectedSessionId == -1){
            Toast.makeText(this, "Please fill in all fields and select a session.", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton selectedSessionButton = findViewById(selectedSessionId);
        String selectedSession = selectedSessionButton.getText().toString();

        //getting current user's id
        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        //query to count existing appointments for the doctor on the selected date
        Query query = appointmentsRef.child(doctorId).child(appointmentDate);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //counting the number of existing appointments for the doctor on this date
                int serialNumber = (int) dataSnapshot.getChildrenCount() + 1;  // serial number is count + 1

                //creating a new appointment id
                String appointmentId = appointmentsRef.push().getKey();
                String status = "Booked";
                long timestamp = System.currentTimeMillis();

                //creating an Appointment object with the serial number
                Appointment newAppointment = new Appointment(appointmentId, userId, doctorId, appointmentDate, timestamp, status, selectedSession, patientName, patientPhone, serialNumber);

                if (appointmentId != null) {
                    //storing the appointment in Firebase under the doctor and appointment date
                    appointmentsRef.child(doctorId).child(appointmentDate).child(appointmentId).setValue(newAppointment)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(AppointmentBookingActivity.this, "Appointment booked successfully.", Toast.LENGTH_SHORT).show();
                                finish(); //closing the activity after success
                            }).addOnFailureListener(e -> Toast.makeText(AppointmentBookingActivity.this, "Failed to book appointment. Please try again.", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //handling errors if the query is canceled
                Toast.makeText(AppointmentBookingActivity.this, "Failed to fetch data. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
