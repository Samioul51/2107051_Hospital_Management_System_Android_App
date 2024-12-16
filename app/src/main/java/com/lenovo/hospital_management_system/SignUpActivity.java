package com.lenovo.hospital_management_system;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import java.util.Calendar;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.HashMap;
import java.util.Map;
public class SignUpActivity extends AppCompatActivity{
    private TextInputLayout tilFullName, tilEmail, tilPassword, tilConfirmPassword, tilPhone, tilSex, tilDob, tilBloodGroup, tilAddress;
    private TextInputEditText edtFullName, edtEmail, edtPassword, edtConfirmPassword, edtPhone, edtDob, edtAddress;
    private AutoCompleteTextView dropdownSex, dropdownBloodGroup;
    private MaterialButton btnSignup;
    private ProgressBar progressBarSignUp;
    private ScrollView scrollViewSignUp;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private static final String TAG = "SignUpActivity";
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{6,}$");
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        initViews();
        setupDropdowns();
        setupDatePicker();
        setupValidation();
    }
    private void initViews(){
        tilFullName = findViewById(R.id.tilFullName);
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword);
        tilPhone = findViewById(R.id.tilPhone);
        tilSex = findViewById(R.id.tilSex);
        tilDob = findViewById(R.id.tilDob);
        tilBloodGroup = findViewById(R.id.tilBloodGroup);
        tilAddress = findViewById(R.id.tilAddress);
        edtFullName = findViewById(R.id.edtFullName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        edtPhone = findViewById(R.id.edtPhone);
        edtDob = findViewById(R.id.edtDob);
        edtAddress = findViewById(R.id.edtAddress);
        dropdownSex = findViewById(R.id.dropdownSex);
        dropdownBloodGroup = findViewById(R.id.dropdownBloodGroup);
        btnSignup = findViewById(R.id.btnSignup);
        progressBarSignUp = findViewById(R.id.progressBarSignUp);
        scrollViewSignUp = findViewById(R.id.scrollViewSignUp);
    }
    private void setupDropdowns(){
        String[] sexOptions = new String[]{"Male", "Female"};
        ArrayAdapter<String> sexAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, sexOptions);
        dropdownSex.setAdapter(sexAdapter);
        String[] bloodGroups = new String[]{"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
        ArrayAdapter<String> bloodGroupAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, bloodGroups);
        dropdownBloodGroup.setAdapter(bloodGroupAdapter);
    }
    private void setupDatePicker(){
        edtDob.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(SignUpActivity.this, (view, selectedYear, selectedMonth, selectedDay) -> {
                String date = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                edtDob.setText(date);
            }, year, month, day);
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePickerDialog.show();
        });
    }
    private void setupValidation(){
        btnSignup.setOnClickListener(v -> {
            if (validateInputs()) {
                performSignup();
            }
        });
    }
    private boolean validateInputs(){
        boolean isValid = true;
        if(TextUtils.isEmpty(edtFullName.getText())){
            tilFullName.setError("Full name is required");
            isValid = false;
        }
        else{
            tilFullName.setError(null);
        }
        String email = Objects.requireNonNull(edtEmail.getText()).toString().trim();
        if(TextUtils.isEmpty(email)){
            tilEmail.setError("Email is required");
            isValid = false;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            tilEmail.setError("Invalid email address");
            isValid = false;
        }
        else{
            tilEmail.setError(null);
        }
        String password = Objects.requireNonNull(edtPassword.getText()).toString();
        if(TextUtils.isEmpty(password)){
            tilPassword.setError("Password is required");
            isValid = false;
        }
        else if(!PASSWORD_PATTERN.matcher(password).matches()){
            //tilPassword.setError("Use 6+ characters with uppercase, lowercase, a number and a special character.");
            showToast("Password must be 6+ characters with uppercase, lowercase, a number and a special character.");
            isValid = false;
        }
        else{
            tilPassword.setError(null);
        }
        String confirmPassword = Objects.requireNonNull(edtConfirmPassword.getText()).toString();
        if(TextUtils.isEmpty(confirmPassword)){
            tilConfirmPassword.setError("Please confirm your password");
            isValid = false;
        }
        else if(!confirmPassword.equals(password)){
            tilConfirmPassword.setError("Passwords do not match");
            isValid = false;
        }
        else{
            tilConfirmPassword.setError(null);
        }
        String phone = Objects.requireNonNull(edtPhone.getText()).toString().trim();
        if(TextUtils.isEmpty(phone)){
            tilPhone.setError("Phone number is required");
            isValid = false;
        }
        else if(phone.length() < 10){
            tilPhone.setError("Invalid phone number");
            isValid = false;
        }
        else{
            tilPhone.setError(null);
        }
        if(TextUtils.isEmpty(dropdownSex.getText())){
            tilSex.setError("Please select your sex");
            isValid = false;
        }
        else{
            tilSex.setError(null);
        }
        if(TextUtils.isEmpty(edtDob.getText())){
            tilDob.setError("Date of birth is required");
            isValid = false;
        }
        else{
            tilDob.setError(null);
        }
        if(TextUtils.isEmpty(dropdownBloodGroup.getText())){
            tilBloodGroup.setError("Please select your blood group");
            isValid = false;
        }
        else{
            tilBloodGroup.setError(null);
        }
        if(TextUtils.isEmpty(edtAddress.getText())){
            tilAddress.setError("Address is required");
            isValid = false;
        }
        else{
            tilAddress.setError(null);
        }
        return isValid;
    }
    private void performSignup(){
        setSignupInProgress(true);
        String email = Objects.requireNonNull(edtEmail.getText()).toString().trim();
        String password = Objects.requireNonNull(edtPassword.getText()).toString();
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if(task.isSuccessful()){
                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                if(firebaseUser != null){
                    String userId = firebaseUser.getUid();
                    createUserProfile(userId);
                }
                else{
                    showToast("Error creating user profile");
                    setSignupInProgress(false);
                }
            }
            else{
                handleAuthError(task.getException());
                setSignupInProgress(false);
            }
        });
    }
    private void createUserProfile(String userId){
        String fullName = Objects.requireNonNull(edtFullName.getText()).toString().trim();
        String email = Objects.requireNonNull(edtEmail.getText()).toString().trim();
        String phone = Objects.requireNonNull(edtPhone.getText()).toString().trim();
        String sex = Objects.requireNonNull(dropdownSex.getText()).toString();
        String dob = Objects.requireNonNull(edtDob.getText()).toString();
        String bloodGroup = Objects.requireNonNull(dropdownBloodGroup.getText()).toString();
        String address = Objects.requireNonNull(edtAddress.getText()).toString().trim();
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("fullName", fullName);
        userMap.put("email", email);
        userMap.put("phone", phone);
        userMap.put("sex", sex);
        userMap.put("dob", dob);
        userMap.put("bloodGroup", bloodGroup);
        userMap.put("address", address);
        userMap.put("timestamp", ServerValue.TIMESTAMP);
        mDatabase.child("users").child(userId).setValue(userMap).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                showToast("User registration successful");
                redirectToLogin();
            }
            else{
                showToast("Error saving user profile");
                setSignupInProgress(false);
            }
        });
    }
    private void handleAuthError(Exception exception){
        if(exception instanceof FirebaseAuthUserCollisionException){
            showToast("User already exists with this email.");
        }
        else if(exception instanceof FirebaseAuthWeakPasswordException){
            showToast("Weak password. Please enter a stronger password.");
        }
        else if(exception instanceof FirebaseAuthInvalidCredentialsException){
            showToast("Invalid email or password.");
        }
        else{
            showToast("Authentication failed: " + exception.getMessage());
        }
    }
    private void setSignupInProgress(boolean inProgress){
        if(inProgress){
            progressBarSignUp.setVisibility(View.VISIBLE);
            scrollViewSignUp.setAlpha(0.3f);
            btnSignup.setEnabled(false);
        }
        else{
            progressBarSignUp.setVisibility(View.GONE);
            scrollViewSignUp.setAlpha(1.0f);
            btnSignup.setEnabled(true);
        }
    }
    private void showToast(String message){
        Toast.makeText(SignUpActivity.this, message, Toast.LENGTH_SHORT).show();
    }
    private void redirectToLogin(){
        Intent intent = new Intent(SignUpActivity.this, SignupSuccessActivity.class);
        startActivity(intent);
        finish();
    }
}