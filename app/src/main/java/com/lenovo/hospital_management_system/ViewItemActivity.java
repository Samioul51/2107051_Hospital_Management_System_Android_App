package com.lenovo.hospital_management_system;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ViewItemActivity extends AppCompatActivity{

    private Spinner inventoryTypeSpinner;
    private ListView itemsListView;
    private DatabaseReference inventoryRef;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_item);

        inventoryRef = FirebaseDatabase.getInstance().getReference("inventory");

        inventoryTypeSpinner = findViewById(R.id.inventoryTypeSpinner);
        itemsListView = findViewById(R.id.itemsListView);

        //populating inventory type spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.inventory_types, android.R.layout.simple_spinner_item);  //inventory_types is an array resource
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        inventoryTypeSpinner.setAdapter(adapter);

        inventoryTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id){
                loadItemsForSelectedType();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });
    }

    private void loadItemsForSelectedType(){
        String selectedType = inventoryTypeSpinner.getSelectedItem().toString();
        if(selectedType.equals("Select Item Type"))
            return;

        DatabaseReference itemsReference = inventoryRef.child(selectedType.toLowerCase());

        ArrayList<String> itemDetails = new ArrayList<>();
        itemsReference.get().addOnCompleteListener(task -> {
            if(task.isSuccessful() && task.getResult().exists()){
                for(DataSnapshot itemSnapshot : task.getResult().getChildren()){
                    String name = itemSnapshot.getKey();
                    int quantity = itemSnapshot.child("quantity").getValue(Integer.class);
                    String description = itemSnapshot.child("description").getValue(String.class);
                    String itemDetail = "Name: " + name + "\nStock: " + quantity + "\nDescription: " + description;
                    itemDetails.add(itemDetail);
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, itemDetails);
                itemsListView.setAdapter(adapter);
            }
            else{
                Toast.makeText(this, "No items found.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
