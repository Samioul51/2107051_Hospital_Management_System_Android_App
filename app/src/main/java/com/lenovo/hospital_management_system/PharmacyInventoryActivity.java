package com.lenovo.hospital_management_system;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PharmacyInventoryActivity extends AppCompatActivity {

    private EditText itemNameInput, itemQuantityInput, itemDescriptionInput;
    private Button addItemButton, deductItemButton;
    private ListView inventoryListView;
    private ArrayAdapter<String> inventoryAdapter;
    private List<String> inventoryList;
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pharmacy_inventory);

        database = FirebaseDatabase.getInstance().getReference("pharmacy_inventory");

        // Initialize UI elements
        itemNameInput = findViewById(R.id.itemNameInput);
        itemQuantityInput = findViewById(R.id.itemQuantityInput);
        itemDescriptionInput = findViewById(R.id.itemDescriptionInput);
        addItemButton = findViewById(R.id.addItemButton);
        deductItemButton = findViewById(R.id.deductItemButton);
        inventoryListView = findViewById(R.id.inventoryListView);

        inventoryList = new ArrayList<>();
        inventoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, inventoryList);
        inventoryListView.setAdapter(inventoryAdapter);

        loadInventory();

        // Add item to inventory
        addItemButton.setOnClickListener(v -> addItemToInventory());

        // Deduct item from inventory
        deductItemButton.setOnClickListener(v -> deductItemFromInventory());
    }

    private void loadInventory() {
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                inventoryList.clear();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    String itemName = itemSnapshot.child("name").getValue(String.class);
                    if (itemName != null) {
                        inventoryList.add(itemName);
                    }
                }
                inventoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PharmacyInventoryActivity.this, "Failed to load inventory", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addItemToInventory() {
        String itemName = itemNameInput.getText().toString().trim();
        String quantityString = itemQuantityInput.getText().toString().trim();
        String description = itemDescriptionInput.getText().toString().trim();

        if (TextUtils.isEmpty(itemName) || TextUtils.isEmpty(quantityString) || TextUtils.isEmpty(description)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int quantity = Integer.parseInt(quantityString);
        String itemId = database.push().getKey(); // Generate unique ID for the item

        Map<String, Object> itemData = new HashMap<>();
        itemData.put("name", itemName);
        itemData.put("quantity", quantity);
        itemData.put("description", description);

        if (itemId != null) {
            database.child(itemId).setValue(itemData).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Item added to pharmacy inventory", Toast.LENGTH_SHORT).show();
                    loadInventory();
                } else {
                    Toast.makeText(this, "Failed to add item", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void deductItemFromInventory() {
        String itemName = itemNameInput.getText().toString().trim();
        String quantityString = itemQuantityInput.getText().toString().trim();

        if (TextUtils.isEmpty(itemName) || TextUtils.isEmpty(quantityString)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int quantity = Integer.parseInt(quantityString);

        database.orderByChild("name").equalTo(itemName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    int currentQuantity = itemSnapshot.child("quantity").getValue(Integer.class);
                    if (currentQuantity != 0 && currentQuantity >= quantity) {
                        int updatedQuantity = currentQuantity - quantity;
                        itemSnapshot.getRef().child("quantity").setValue(updatedQuantity);
                        Toast.makeText(PharmacyInventoryActivity.this, "Item deducted from inventory", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(PharmacyInventoryActivity.this, "Not enough stock to deduct", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PharmacyInventoryActivity.this, "Failed to deduct item", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
