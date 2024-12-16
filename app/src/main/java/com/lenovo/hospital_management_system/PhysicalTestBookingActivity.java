package com.lenovo.hospital_management_system;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class PhysicalTestBookingActivity extends AppCompatActivity{

    private EditText editTextDate;
    private EditText editTextPatientName;
    private EditText editTextPatientPhone;

    private DatabaseReference physicalTestsRef;
    private String testId;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_physical_test_booking);

        //initializing views
        EditText editTextTestName = findViewById(R.id.editTextTestName);
        editTextDate = findViewById(R.id.editTextDate);
        editTextPatientName = findViewById(R.id.editTextPatientName);
        editTextPatientPhone = findViewById(R.id.editTextPatientPhone);
        Button btnBookTest = findViewById(R.id.btnBookTest);

        //getting data passed from previous activity
        testId = getIntent().getStringExtra("test_id");
        String testName = getIntent().getStringExtra("test_name");

        //setting test name(read-only)
        editTextTestName.setText(testName);

        //firebase database reference
        physicalTestsRef = FirebaseDatabase.getInstance().getReference("physicaltests");

        //date picker for selecting test date
        editTextDate.setOnClickListener(v -> showDatePickerDialog());

        //handling book test button click
        btnBookTest.setOnClickListener(v -> bookTest());
    }

    private void showDatePickerDialog(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1); //start date is 1 day from today
        long minDate = calendar.getTimeInMillis();

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(year, month, dayOfMonth);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    editTextDate.setText(sdf.format(selectedDate.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.getDatePicker().setMinDate(minDate);
        calendar.add(Calendar.DAY_OF_MONTH, 1); //allowing only up to the next day
        datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
        datePickerDialog.show();
    }

    private void bookTest(){
        //validating input
        String testDate = editTextDate.getText().toString();
        String patientName = editTextPatientName.getText().toString().trim();
        String patientPhone = editTextPatientPhone.getText().toString().trim();

        if(testDate.isEmpty() || patientName.isEmpty() || patientPhone.isEmpty()){
            Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        //getting current user's ID
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //reference for physical test appointments
        DatabaseReference appointmentsRef = FirebaseDatabase.getInstance().getReference("physicaltestappointments");

        //creating a new appointment ID
        String appointmentId = appointmentsRef.push().getKey();
        String status = "Booked";
        long timestamp = System.currentTimeMillis();

        //creating a new appointment object with userId
        PhysicalTestAppointment newTestBooking = new PhysicalTestAppointment(
                appointmentId, userId, testId, testDate, timestamp, status, patientName, patientPhone);

        //saving appointment to Firebase
        if(appointmentId != null){
            appointmentsRef.child(appointmentId).setValue(newTestBooking)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(PhysicalTestBookingActivity.this, "Test booked successfully.", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(PhysicalTestBookingActivity.this, "Failed to book test. Please try again.", Toast.LENGTH_SHORT).show()
                    );
        }
    }

}
