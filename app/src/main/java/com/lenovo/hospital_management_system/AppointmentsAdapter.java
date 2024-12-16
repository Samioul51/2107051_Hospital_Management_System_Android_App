package com.lenovo.hospital_management_system;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class AppointmentsAdapter extends RecyclerView.Adapter<AppointmentsAdapter.ViewHolder> {

    private final ArrayList<AppointmentHistory> appointments;
    private final Context context;
    private final DatabaseReference appointmentsRef;

    public AppointmentsAdapter(Context context, ArrayList<AppointmentHistory> appointments){
        this.context = context;
        this.appointments = appointments;
        this.appointmentsRef = FirebaseDatabase.getInstance().getReference("appointments");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_appointment, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position){
        AppointmentHistory appointment = appointments.get(position);

        if(appointment != null){
            if(appointment.getDoctorName() != null){
                holder.details.setText("Doctor: " + appointment.getDoctorName() + "\nRoom: " + appointment.getDoctorRoom() + "\nPatient: " + appointment.getPatientName() + "\nDate: " + appointment.getAppointmentDate() + "\nSession: " + appointment.getSession() + "\nSerial: " + appointment.getSerialNumber());
            }
            else if(appointment.getTestName() != null){
                holder.details.setText("Patient: " + appointment.getPatientName() + "\nTest: " + appointment.getTestName() + "\nRoom: " + appointment.getTestRoom() + "\nDate: " + appointment.getAppointmentDate());
            }

            holder.cancelButton.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Cancel Appointment").setMessage("Are you sure you want to cancel this appointment?").setPositiveButton("Yes", (dialog, which) -> {
                            if(appointment.getDoctorName() != null){
                                if(canCancelDoctorAppointment(appointment.getAppointmentDate())){
                                    cancelDoctorAppointment(appointment, position);
                                }
                                else{
                                    Toast.makeText(context, "You can only cancel doctor appointments at least 2 days before the appointment.", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else if(appointment.getTestName() != null){
                                cancelPhysicalTestAppointment(appointment, position);
                            }
                        }).setNegativeButton("No", (dialog, which) -> dialog.dismiss()).show();
            });
        }
    }

    private boolean canCancelDoctorAppointment(String appointmentDate){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        try{
            Date appointment = sdf.parse(appointmentDate);
            Date today = new Date();
            long diff = appointment.getTime() - today.getTime();
            return diff >= 2 * 24 * 60 * 60 * 1000;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    private void cancelDoctorAppointment(AppointmentHistory appointment, int position){
        String doctorId = appointment.getDoctorId();
        String appointmentDate = appointment.getAppointmentDate();
        String session = appointment.getSession();
        DatabaseReference specificAppointmentRef = appointmentsRef.child(doctorId).child(appointmentDate).child(appointment.getAppointmentId());

        specificAppointmentRef.removeValue().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                updateSerialNumbers(doctorId, appointmentDate, session, appointment.getSerialNumber(), position);
            }
            else{
                Toast.makeText(context, "Failed to cancel appointment", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateSerialNumbers(String doctorId, String date, String session, int canceledSerial, int position){
        DatabaseReference sessionAppointmentsRef = appointmentsRef.child(doctorId).child(date);

        sessionAppointmentsRef.orderByChild("session").equalTo(session).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot){
                        for (DataSnapshot child : snapshot.getChildren()){
                            AppointmentHistory appointment = child.getValue(AppointmentHistory.class);
                            if(appointment != null && appointment.getSerialNumber() > canceledSerial){
                                appointment.setSerialNumber(appointment.getSerialNumber() - 1);
                                sessionAppointmentsRef.child(child.getKey()).setValue(appointment);
                            }
                        }
                        appointments.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, appointments.size());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error){
                        Toast.makeText(context, "Failed to update serials", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void cancelPhysicalTestAppointment(AppointmentHistory appointment, int position){
        DatabaseReference specificTestAppointmentRef = FirebaseDatabase.getInstance().getReference("physicaltestappointments").child(appointment.getAppointmentId());
        specificTestAppointmentRef.removeValue().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                appointments.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, appointments.size());
            }
            else{
                Toast.makeText(context, "Failed to cancel test appointment", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount(){
        return appointments.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView details;
        Button cancelButton;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            details = itemView.findViewById(R.id.text_appointment_details);
            cancelButton = itemView.findViewById(R.id.button_cancel_appointment);
        }
    }
}
