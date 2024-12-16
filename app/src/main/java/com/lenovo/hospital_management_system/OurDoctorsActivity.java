package com.lenovo.hospital_management_system;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OurDoctorsActivity extends AppCompatActivity{
    private static final String TAG = "OurDoctorsActivity";
    private static final String API_URL = "https://api.myjson.online/v1/records/a1e637d2-51da-4f94-b95f-521440530f21";

    private RecyclerView recyclerView;
    private OurDoctorAdapter doctorAdapter;
    private List<OurDoctor> doctorList;
    private DatabaseReference doctorRef;
    private RequestQueue requestQueue;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_our_doctors);

        recyclerView = findViewById(R.id.recyclerViewDoctors);
        progressBar = findViewById(R.id.progressBar);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        doctorList = new ArrayList<>();
        doctorAdapter = new OurDoctorAdapter(doctorList);
        recyclerView.setAdapter(doctorAdapter);

        requestQueue = Volley.newRequestQueue(this);
        doctorRef = FirebaseDatabase.getInstance().getReference("doctors");

        // Show progress bar before fetching data
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        fetchDoctorsFromFirebase();
    }

    private void fetchDoctorsFromFirebase(){
        doctorRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot){
                doctorList.clear();
                Map<String, Object> doctorsMap = new HashMap<>();

                for(DataSnapshot doctorSnapshot : snapshot.getChildren()){
                    String doctorId = doctorSnapshot.getKey();
                    Map<String, Object> doctorData = new HashMap<>();

                    doctorData.put("name", doctorSnapshot.child("name").getValue(String.class));
                    doctorData.put("specialty", doctorSnapshot.child("specialty").getValue(String.class));
                    doctorData.put("roomNumber", doctorSnapshot.child("roomNumber").getValue(String.class));
                    doctorData.put("schedulerId", doctorSnapshot.child("schedulerId").getValue(String.class));

                    doctorsMap.put(doctorId, doctorData);

                    //adding to local list for RecyclerView
                    doctorList.add(new OurDoctor(
                            doctorData.get("name").toString(),
                            doctorData.get("specialty").toString(),
                            doctorData.get("roomNumber").toString()
                    ));
                }

                //updating RecyclerView
                doctorAdapter.notifyDataSetChanged();

                //hiding progress bar and showing RecyclerView
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);

                //updating JSON API
                updateJsonAPI(doctorsMap);
            }

            @Override
            public void onCancelled(DatabaseError error){
                Log.e(TAG, "Database error: " + error.getMessage());
                Toast.makeText(OurDoctorsActivity.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();

                //hiding progress bar in case of error
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        });
    }

    private void updateJsonAPI(Map<String, Object> doctorsMap){
        try{
            JSONObject jsonData = new JSONObject();
            jsonData.put("doctors", new JSONObject(doctorsMap));

            JSONObject payload = new JSONObject();
            payload.put("jsonData", jsonData.toString());

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.PUT,
                    API_URL,
                    payload,
                    response -> {
                       // Toast.makeText(OurDoctorsActivity.this, "Doctors Updated", Toast.LENGTH_SHORT).show();
                    },
                    error -> {
                        if (error.networkResponse != null) {
                          //  Log.e(TAG, "Error Response: " + new String(error.networkResponse.data));
                        }
                       // Toast.makeText(OurDoctorsActivity.this, "Update Failed", Toast.LENGTH_SHORT).show();
                    }
            );

            requestQueue.add(request);
        } catch(JSONException e){
            //Log.e(TAG, "JSON Conversion Error", e);
        }
    }
}