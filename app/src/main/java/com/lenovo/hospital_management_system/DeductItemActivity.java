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

public class DeductItemActivity extends AppCompatActivity{

    private Spinner inventoryTypeSpinner, itemSpinner;
    private EditText quantityToDeductInput;
    private Button deductItemButton;

    private DatabaseReference inventoryRef;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deduct_item);

        //initializing Firebase reference
        inventoryRef = FirebaseDatabase.getInstance().getReference("inventory");

        //initializing views
        inventoryTypeSpinner = findViewById(R.id.inventoryTypeSpinner);
        itemSpinner = findViewById(R.id.itemSpinner);
        quantityToDeductInput = findViewById(R.id.quantityToDeductInput);
        deductItemButton = findViewById(R.id.deductItemButton);

        //populating inventory type spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.inventory_types, android.R.layout.simple_spinner_item);  //inventory_types is an array resource
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        inventoryTypeSpinner.setAdapter(adapter);

        //setting onItemSelectedListener for the inventory type spinner
        inventoryTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                loadItemsForSelectedType();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });

        deductItemButton.setOnClickListener(v -> deductItem());
    }

    private void loadItemsForSelectedType(){
        String selectedType = inventoryTypeSpinner.getSelectedItem().toString();
        if (selectedType.equals("Select Item Type")) return;

        itemSpinner.setAdapter(null);
        DatabaseReference itemsReference = inventoryRef.child(selectedType.toLowerCase());

        ArrayList<String> itemNames = new ArrayList<>();
        itemsReference.get().addOnCompleteListener(task -> {
            if(task.isSuccessful() && task.getResult().exists()){
                for (DataSnapshot itemSnapshot : task.getResult().getChildren()) {
                    String itemName = itemSnapshot.getKey();
                    int stock = itemSnapshot.child("quantity").getValue(Integer.class);
                    if (stock > 0) { // Only show items with stock > 0
                        itemNames.add(itemName);
                    }
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, itemNames);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                itemSpinner.setAdapter(adapter);
            }
            else{
                Toast.makeText(this, "No items found.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deductItem(){
        String itemType = inventoryTypeSpinner.getSelectedItem().toString();
        String itemName = itemSpinner.getSelectedItem().toString();
        String quantityToDeductStr = quantityToDeductInput.getText().toString();

        if(quantityToDeductStr.isEmpty()){
            Toast.makeText(this, "Please enter quantity to deduct.", Toast.LENGTH_SHORT).show();
            return;
        }

        int quantityToDeduct = Integer.parseInt(quantityToDeductStr);

        DatabaseReference itemRef = inventoryRef.child(itemType.toLowerCase()).child(itemName);
        itemRef.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                int currentQuantity = task.getResult().child("quantity").getValue(Integer.class);
                if(currentQuantity >= quantityToDeduct){
                    itemRef.child("quantity").setValue(currentQuantity - quantityToDeduct);
                    Toast.makeText(this, "Item quantity deducted successfully", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(this, "Insufficient stock to deduct", Toast.LENGTH_SHORT).show();
                }
            }
            else{
                Toast.makeText(this, "Failed to deduct item quantity", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
