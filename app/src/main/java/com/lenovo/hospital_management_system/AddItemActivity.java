package com.lenovo.hospital_management_system;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddItemActivity extends AppCompatActivity {

    private Spinner inventoryTypeSpinner;
    private EditText itemNameInput, itemDescriptionInput, itemQuantityInput;
    private Button addItemButton;

    private DatabaseReference inventoryRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        //initializing firebase reference
        inventoryRef = FirebaseDatabase.getInstance().getReference("inventory");

        //initializing views
        inventoryTypeSpinner = findViewById(R.id.inventoryTypeSpinner);
        itemNameInput = findViewById(R.id.itemNameInput);
        itemDescriptionInput = findViewById(R.id.itemDescriptionInput);
        itemQuantityInput = findViewById(R.id.itemQuantityInput);
        addItemButton = findViewById(R.id.addItemButton);

        //setting onClickListener for Add Item button
        addItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                addItem();
            }
        });
    }

    private void addItem() {
        //getting the input data
        String itemType = inventoryTypeSpinner.getSelectedItem().toString();
        String itemName = itemNameInput.getText().toString();
        String itemDescription = itemDescriptionInput.getText().toString();
        String itemQuantityStr = itemQuantityInput.getText().toString();

        //validating inputs
        if(itemType.equals("Select Item Type")){
            Toast.makeText(this, "Please select item type", Toast.LENGTH_SHORT).show();
            return;
        }

        if(itemName.isEmpty()){
            Toast.makeText(this, "Please enter item name", Toast.LENGTH_SHORT).show();
            return;
        }

        if(itemDescription.isEmpty()){
            Toast.makeText(this, "Please enter item description", Toast.LENGTH_SHORT).show();
            return;
        }

        if(itemQuantityStr.isEmpty()){
            Toast.makeText(this, "Please enter item quantity", Toast.LENGTH_SHORT).show();
            return;
        }

        int itemQuantity = Integer.parseInt(itemQuantityStr);

        //creating a new item object
        InventoryItem newItem = new InventoryItem(itemName, itemDescription, itemQuantity);

        //determining the database reference based on item type
        DatabaseReference itemRef = inventoryRef.child(itemType.toLowerCase()).child(itemName);

        //adding the new item to firebase database
        itemRef.setValue(newItem).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Toast.makeText(AddItemActivity.this, "Item added successfully", Toast.LENGTH_SHORT).show();
                //clearInputs();
                finish();
            }
            else{
                Toast.makeText(AddItemActivity.this, "Failed to add item", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*private void clearInputs(){
        itemNameInput.setText("");
        itemDescriptionInput.setText("");
        itemQuantityInput.setText("");
        inventoryTypeSpinner.setSelection(0);  //resetting spinner to default
    }*/

    //InventoryItem class to represent an item in firebase
    public static class InventoryItem{
        public String name;
        public String description;
        public int quantity;

        public InventoryItem(){
            //default constructor required for firebase
        }

        public InventoryItem(String name, String description, int quantity){
            this.name = name;
            this.description = description;
            this.quantity = quantity;
        }
    }
}
