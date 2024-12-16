package com.lenovo.hospital_management_system;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;
import java.util.regex.Pattern;

public class ProfileFragment extends Fragment{

    private TextView fullNameTextView, emailTextView, phoneTextView, sexTextView, dobTextView, bloodGroupTextView, addressTextView;
    private Button logoutButton, editProfileButton, resetPasswordButton;
    private ProgressBar loadingProgressBar;

    //password pattern to enforce complexity rules
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{6,}$");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        //inflating the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        fullNameTextView = rootView.findViewById(R.id.fullNameTextView);
        emailTextView = rootView.findViewById(R.id.emailTextView);
        phoneTextView = rootView.findViewById(R.id.phoneTextView);
        sexTextView = rootView.findViewById(R.id.sexTextView);
        dobTextView = rootView.findViewById(R.id.dobTextView);
        bloodGroupTextView = rootView.findViewById(R.id.bloodGroupTextView);
        addressTextView = rootView.findViewById(R.id.addressTextView);
        logoutButton = rootView.findViewById(R.id.logoutButton);
        editProfileButton = rootView.findViewById(R.id.editProfileButton);
        resetPasswordButton = rootView.findViewById(R.id.resetPasswordButton);
        loadingProgressBar = rootView.findViewById(R.id.loadingProgressBar);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());

        //showing ProgressBar before loading data
        loadingProgressBar.setVisibility(View.VISIBLE);

        //fetching and displaying user data
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot){
                if(dataSnapshot.exists()){
                    String fullName = dataSnapshot.child("fullName").getValue(String.class);
                    String email = dataSnapshot.child("email").getValue(String.class);
                    String phone = dataSnapshot.child("phone").getValue(String.class);
                    String sex = dataSnapshot.child("sex").getValue(String.class);
                    String dob = dataSnapshot.child("dob").getValue(String.class);
                    String bloodGroup = dataSnapshot.child("bloodGroup").getValue(String.class);
                    String address = dataSnapshot.child("address").getValue(String.class);

                    fullNameTextView.setText(fullName);
                    emailTextView.setText(email);
                    phoneTextView.setText(phone);
                    sexTextView.setText(sex);
                    dobTextView.setText(dob);
                    bloodGroupTextView.setText(bloodGroup);
                    addressTextView.setText(address);
                }

                //hiding ProgressBar after loading data
                loadingProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError){
                //hiding ProgressBar
                loadingProgressBar.setVisibility(View.GONE);

                //notifying the user about the error
                Toast.makeText(getContext(), "Failed to load profile data. Please try again later.", Toast.LENGTH_SHORT).show();
            }

        });

        //log out button and its action
        logoutButton.setOnClickListener(v -> showLogoutDialog());

        //edit Profile button and its action
        editProfileButton.setOnClickListener(v -> openEditProfileActivity());

        //reset Password button and its action
        resetPasswordButton.setOnClickListener(v -> sendPasswordResetEmail());

        return rootView;
    }

    //method to show logout confirmation dialog
    private void showLogoutDialog(){
        new AlertDialog.Builder(getContext())
                .setMessage("Are you sure you want to log out?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> logoutUser())
                .setNegativeButton("No", null)
                .show();
    }

    //method to log out the user and navigate back to LoginActivity
    private void logoutUser(){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        requireActivity().finish(); // Close the current activity
    }

    //method to open Edit Profile screen
    private void openEditProfileActivity(){
        Intent intent = new Intent(getActivity(), EditProfileActivity.class);
        startActivity(intent);
    }

    //method to send password reset email
    private void sendPasswordResetEmail(){
        //showing confirmation dialog before sending the reset email
        new AlertDialog.Builder(getContext())
                .setMessage("Are you sure you want to change your password?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, which) -> {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if(user != null){
                        String email = user.getEmail();
                        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                                .addOnCompleteListener(task -> {
                                    if(task.isSuccessful()){
                                        Toast.makeText(getContext(), "Password reset email sent", Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        Toast.makeText(getContext(), "Failed to send reset email", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .setNegativeButton("No", null).show();
    }

    //Password Validation: checking if password matches the required pattern
    private boolean isPasswordValid(String password) {
        return PASSWORD_PATTERN.matcher(password).matches();
    }
}
