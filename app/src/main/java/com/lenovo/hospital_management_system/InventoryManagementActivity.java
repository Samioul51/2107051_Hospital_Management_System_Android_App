package com.lenovo.hospital_management_system;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class InventoryManagementActivity extends AppCompatActivity{

    private Button addItemButton, updateItemButton, deductItemButton, viewItemButton;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_management);

        //initializing buttons
        addItemButton = findViewById(R.id.addItemButton);
        updateItemButton = findViewById(R.id.updateItemButton);
        deductItemButton = findViewById(R.id.deductItemButton);
        viewItemButton = findViewById(R.id.viewItemButton);

        //setting onClickListeners to navigate to respective activities
        addItemButton.setOnClickListener(v -> openAddItemActivity());
        updateItemButton.setOnClickListener(v -> openUpdateItemActivity());
        deductItemButton.setOnClickListener(v -> openDeductItemActivity());
        viewItemButton.setOnClickListener(v -> openViewItemActivity());
    }

    private void openAddItemActivity(){
        Intent intent = new Intent(InventoryManagementActivity.this, AddItemActivity.class);
        startActivity(intent);
    }

    private void openUpdateItemActivity(){
        Intent intent = new Intent(InventoryManagementActivity.this, UpdateItemActivity.class);
        startActivity(intent);
    }

    private void openDeductItemActivity(){
        Intent intent = new Intent(InventoryManagementActivity.this, DeductItemActivity.class);
        startActivity(intent);
    }

    private void openViewItemActivity(){
        Intent intent = new Intent(InventoryManagementActivity.this, ViewItemActivity.class);
        startActivity(intent);
    }
}
