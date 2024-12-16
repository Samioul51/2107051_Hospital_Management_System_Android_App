package com.lenovo.hospital_management_system;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class DoctorAppointmentsAdapter extends RecyclerView.Adapter<DoctorAppointmentsAdapter.ViewHolder> {
    private List<AppointmentDoctorView> appointmentList;

    public DoctorAppointmentsAdapter(List<AppointmentDoctorView> appointmentList){
        this.appointmentList = appointmentList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_doctorview_appointment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position){
        AppointmentDoctorView appointment = appointmentList.get(position);

        //binding the data to the view with labels
        holder.patientName.setText("Patient's Name: " + appointment.getPatientName());
        holder.appointmentDate.setText("Appointment Date: " + appointment.getAppointmentDate());
        holder.serialNumber.setText("Serial Number: " + appointment.getSerialNumber());
        holder.patientPhone.setText("Patient Phone Number: " + appointment.getPatientPhone());
        holder.session.setText("Session: " + appointment.getSession());
        holder.status.setText("Status: " + appointment.getStatus());
    }

    @Override
    public int getItemCount(){
        return appointmentList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView patientName, appointmentDate, serialNumber, patientPhone, session, status;

        public ViewHolder(View itemView){
            super(itemView);
            //initializing the TextViews
            patientName = itemView.findViewById(R.id.patientName);
            appointmentDate = itemView.findViewById(R.id.appointmentDate);
            serialNumber = itemView.findViewById(R.id.serialNumber);
            patientPhone = itemView.findViewById(R.id.patientPhone);
            session = itemView.findViewById(R.id.session);
            status = itemView.findViewById(R.id.status);
        }
    }
}
