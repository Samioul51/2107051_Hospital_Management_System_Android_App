package com.lenovo.hospital_management_system;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DoctorBookingAdapter extends RecyclerView.Adapter<DoctorBookingAdapter.DoctorViewHolder> {

    private ArrayList<Doctor> doctorList;
    private OnDoctorClickListener onDoctorClickListener;

    public DoctorBookingAdapter(ArrayList<Doctor> doctorList, OnDoctorClickListener onDoctorClickListener){
        this.doctorList = doctorList;
        this.onDoctorClickListener = onDoctorClickListener;
    }

    @Override
    public DoctorViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_doctor, parent, false);
        return new DoctorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DoctorViewHolder holder, int position){
        Doctor doctor = doctorList.get(position);
        holder.doctorName.setText(doctor.getName());
        holder.specialty.setText(doctor.getSpecialty());

        //setting on click listener to open booking page
        holder.cardView.setOnClickListener(v -> onDoctorClickListener.onDoctorClick(doctor));
    }

    @Override
    public int getItemCount(){
        return doctorList.size();
    }

    public interface OnDoctorClickListener{
        void onDoctorClick(Doctor doctor);
    }

    public static class DoctorViewHolder extends RecyclerView.ViewHolder {

        TextView doctorName, specialty;
        CardView cardView;

        public DoctorViewHolder(View itemView){
            super(itemView);
            doctorName = itemView.findViewById(R.id.doctorName);
            specialty = itemView.findViewById(R.id.doctorSpecialty);
            cardView = itemView.findViewById(R.id.cardViewDoctor);
        }
    }
}
