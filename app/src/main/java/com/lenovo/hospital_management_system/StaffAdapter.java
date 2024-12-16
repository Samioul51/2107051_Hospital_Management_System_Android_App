package com.lenovo.hospital_management_system;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import java.util.List;

public class StaffAdapter extends ArrayAdapter<Staff>{

    public StaffAdapter(Context context, List<Staff> staffList){
        super(context, 0, staffList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.staff_list_item, parent, false);
        }

        //getting the current staff
        Staff staff = getItem(position);

        //binding staff data to the UI
        TextView nameTextView = convertView.findViewById(R.id.nameTextView);
        TextView emailTextView = convertView.findViewById(R.id.emailTextView);
        TextView nidTextView = convertView.findViewById(R.id.nidTextView);
        TextView phoneTextView = convertView.findViewById(R.id.phoneTextView);
        TextView addressTextView = convertView.findViewById(R.id.addressTextView);

        nameTextView.setText("Name: " + staff.getName());
        emailTextView.setText("Email: " + staff.getEmail());
        nidTextView.setText("NID: " + staff.getNid());
        phoneTextView.setText("Phone: " + staff.getPhone());
        addressTextView.setText("Address: " + staff.getAddress());

        return convertView;
    }
}
