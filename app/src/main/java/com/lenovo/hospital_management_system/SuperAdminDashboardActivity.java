package com.lenovo.hospital_management_system;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SuperAdminDashboardActivity extends AppCompatActivity{

    private TextView textSuperAdminName, textSuperAdminEmail, textSuperAdminPosition;
    private Button doctorsButton, patientsButton, staffButton, inventoryButton, physicalTestButton, logoutButton, resetPasswordButton;

    private FirebaseDatabase database;
    private DatabaseReference superAdminRef;
    private String superAdminId;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_super_admin);

        //initializing Firebase
        database = FirebaseDatabase.getInstance();
        superAdminId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        superAdminRef = database.getReference("superadmin").child(superAdminId);

        //initializing views
        textSuperAdminName = findViewById(R.id.textSuperAdminName);
        textSuperAdminEmail = findViewById(R.id.textSuperAdminEmail);
        textSuperAdminPosition = findViewById(R.id.textSuperAdminPosition);
        doctorsButton = findViewById(R.id.doctorsButton);
        patientsButton = findViewById(R.id.patientsButton);
        staffButton = findViewById(R.id.staffButton);
        inventoryButton = findViewById(R.id.inventoryButton);
        physicalTestButton = findViewById(R.id.physicalTestButton);
        logoutButton = findViewById(R.id.logoutButton);
        resetPasswordButton = findViewById(R.id.resetPasswordButton);

        //fetching Super Admin informations
        fetchSuperAdminInfo();

        //setting button actions
        doctorsButton.setOnClickListener(v -> startActivity(new Intent(this, DoctorsManagementActivity.class)));
        patientsButton.setOnClickListener(v -> startActivity(new Intent(this, PatientsManagementActivity.class)));
        staffButton.setOnClickListener(v -> startActivity(new Intent(this, StaffManagementActivity.class)));
        inventoryButton.setOnClickListener(v -> startActivity(new Intent(this, InventoryManagementActivity.class)));
        physicalTestButton.setOnClickListener(v -> startActivity(new Intent(this, PhysicalTestManagementActivity.class)));
        logoutButton.setOnClickListener(v -> showLogoutDialog());
        resetPasswordButton.setOnClickListener(v -> showResetPasswordDialog());
    }

    private void fetchSuperAdminInfo(){
        superAdminRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot){
                if(snapshot.exists()){
                    String name = snapshot.child("name").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    String position = snapshot.child("position").getValue(String.class);

                    if(name != null)
                        textSuperAdminName.setText("Name: " + name);
                    if(email != null)
                        textSuperAdminEmail.setText("Email: " + email);
                    if(position != null)
                        textSuperAdminPosition.setText("Position: " + position);
                }
                else{
                    Toast.makeText(SuperAdminDashboardActivity.this, "Super admin data not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error){
                Toast.makeText(SuperAdminDashboardActivity.this, "Failed to load super admin info", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void showLogoutDialog(){
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void showResetPasswordDialog(){
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
