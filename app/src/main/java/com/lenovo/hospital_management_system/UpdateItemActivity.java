package com.lenovo.hospital_management_system;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class UpdateItemActivity extends AppCompatActivity{

    private Spinner inventoryTypeSpinner, itemSpinner;
    private EditText descriptionInput, amountToAddInput;
    private Button updateItemButton;

    private DatabaseReference inventoryRef;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_item);

        //initializing Firebase reference
        inventoryRef = FirebaseDatabase.getInstance().getReference("inventory");

        //initializing views
        inventoryTypeSpinner = findViewById(R.id.inventoryTypeSpinner);
        itemSpinner = findViewById(R.id.itemSpinner);
        descriptionInput = findViewById(R.id.itemDescriptionEditText);
        amountToAddInput = findViewById(R.id.amountToAddEditText); // Input for amount to add
        updateItemButton = findViewById(R.id.updateItemButton);

        //setting onItemSelectedListener for the item type spinner
        inventoryTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                loadItemsForSelectedType();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });

        //updating item on button click
        updateItemButton.setOnClickListener(v -> updateItem());

        //loading inventory types on activity creation
        loadInventoryTypes();
    }

    //loading inventory types(Hospital, Pharmacy)
    private void loadInventoryTypes(){
        ArrayList<String> inventoryTypes = new ArrayList<>();
        inventoryTypes.add("Select Item Type");
        inventoryTypes.add("Hospital");
        inventoryTypes.add("Pharmacy");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, inventoryTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        inventoryTypeSpinner.setAdapter(adapter);
    }

    //loading items based on selected inventory type(Hospital, Pharmacy)
    private void loadItemsForSelectedType(){
        String selectedType = inventoryTypeSpinner.getSelectedItem().toString();
        if(selectedType.equals("Select Item Type")){
            return;
        }

        itemSpinner.setAdapter(null);
        DatabaseReference itemsReference = inventoryRef.child(selectedType.toLowerCase());

        ArrayList<String> itemNames = new ArrayList<>();
        itemNames.add("Select Item");

        itemsReference.get().addOnCompleteListener(task -> {
            if(task.isSuccessful() && task.getResult().exists()){
                for(DataSnapshot itemSnapshot : task.getResult().getChildren()){
                    String itemName = itemSnapshot.getKey();
                    if(itemName != null){
                        itemNames.add(itemName);
                    }
                }

                //populating item spinner with item names
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, itemNames);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                itemSpinner.setAdapter(adapter);
            }
            else{
                Toast.makeText(this, "No items found for selected type", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateItem(){
        String itemType = inventoryTypeSpinner.getSelectedItem().toString();
        String itemName = itemSpinner.getSelectedItem().toString();
        String description = descriptionInput.getText().toString();
        String amountToAddStr = amountToAddInput.getText().toString();

        //checking if the user has selected an item and entered an amount to add
        if(itemName.equals("Select Item")){
            Toast.makeText(this, "Please select an item", Toast.LENGTH_SHORT).show();
            return;
        }

        if(amountToAddStr.isEmpty()){
            Toast.makeText(this, "Please enter an amount to add", Toast.LENGTH_SHORT).show();
            return;
        }

        int amountToAdd = Integer.parseInt(amountToAddStr); //getting the amount to add from the input field

        if(amountToAdd <= 0){
            Toast.makeText(this, "Amount to add must be greater than zero", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference itemRef = inventoryRef.child(itemType.toLowerCase()).child(itemName);

        //getting current quantity before updating
        itemRef.child("quantity").get().addOnCompleteListener(task -> {
            if(task.isSuccessful() && task.getResult().exists()){
                int currentQuantity = task.getResult().getValue(Integer.class);

                //adding the specified amount to the current quantity
                int newQuantity = currentQuantity + amountToAdd;

                //updating the quantity and description
                itemRef.child("quantity").setValue(newQuantity);

                //updating description if provided
                if(!description.isEmpty()){
                    itemRef.child("description").setValue(description);
                }

                Toast.makeText(this, "Item updated successfully", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(this, "Error: Item not found or quantity could not be retrieved", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
