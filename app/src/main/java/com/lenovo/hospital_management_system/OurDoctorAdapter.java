package com.lenovo.hospital_management_system;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class OurDoctorAdapter extends RecyclerView.Adapter<OurDoctorAdapter.DoctorViewHolder> {

    private List<OurDoctor> doctorList;

    public OurDoctorAdapter(List<OurDoctor> doctorList){
        this.doctorList = doctorList;
    }

    @NonNull
    @Override
    public DoctorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.doctor_card_item, parent, false);
        return new DoctorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorViewHolder holder, int position){
        OurDoctor doctor = doctorList.get(position);
        holder.tvDoctorName.setText(doctor.getName());
        holder.tvSpecialty.setText(doctor.getSpecialty());
        holder.tvRoomNumber.setText(doctor.getRoomNumber());
    }

    @Override
    public int getItemCount(){
        return doctorList.size();
    }

    static class DoctorViewHolder extends RecyclerView.ViewHolder {
        TextView tvDoctorName, tvSpecialty, tvRoomNumber;

        public DoctorViewHolder(@NonNull View itemView){
            super(itemView);
            tvDoctorName = itemView.findViewById(R.id.tvDoctorName);
            tvSpecialty = itemView.findViewById(R.id.tvSpecialty);
            tvRoomNumber = itemView.findViewById(R.id.tvRoomNumber);
        }
    }
}
