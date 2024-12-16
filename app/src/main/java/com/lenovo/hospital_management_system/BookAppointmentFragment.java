package com.lenovo.hospital_management_system;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import android.widget.Button;


public class BookAppointmentFragment extends Fragment{

    private Button doctorBookingButton, physicalTestBookingButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.fragment_book_appointment, container, false);

        doctorBookingButton = rootView.findViewById(R.id.doctorBookingButton);
        physicalTestBookingButton = rootView.findViewById(R.id.physicalTestBookingButton);

        doctorBookingButton.setOnClickListener(v -> getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameContainer, new DoctorBookingFragment()).addToBackStack(null).commit());

        physicalTestBookingButton.setOnClickListener(v -> getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameContainer, new PhysicalTestBookingFragment()).addToBackStack(null).commit());

        return rootView;
    }
}
