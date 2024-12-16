package com.lenovo.hospital_management_system;

import android.app.AlertDialog;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class UserDashboardActivity extends AppCompatActivity{

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        loadFragment(new ProfileFragment());
        bottomNavigationView.setSelectedItemId(R.id.home);

        //using setOnItemSelectedListener instead of the deprecated setOnNavigationItemSelectedListener
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if(itemId == R.id.home){
                selectedFragment = new ProfileFragment();
            }
            else if(itemId == R.id.appointments){
                selectedFragment = new BookAppointmentFragment();
            }
            else if(itemId == R.id.schedule){
                selectedFragment = new ScheduleFragment();
            }
            if(selectedFragment != null){
                loadFragment(selectedFragment);
                return true;
            }
            return false;
        });
    }

    private void loadFragment(Fragment fragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameContainer, fragment);
        transaction.commit();
    }

    @Override
    public void onBackPressed(){
        //checking if the current fragment is ProfileFragment
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frameContainer);
        if(currentFragment instanceof ProfileFragment){
            showQuitDialog(); //showing the quit confirmation dialog
        }
        else{
            //navigating back to the ProfileFragment instead of quitting the app
            loadFragment(new ProfileFragment());
            bottomNavigationView.setSelectedItemId(R.id.home);
        }
    }


    private void showQuitDialog(){
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to quit?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> {
                    finishAffinity(); //closing all activities and exit the app
                })
                .setNegativeButton("No", null)
                .show();
    }
}
