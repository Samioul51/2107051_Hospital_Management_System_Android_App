package com.lenovo.hospital_management_system;

public class AppointmentDoctorView {
    private String appointmentId;
    private String appointmentDate;
    private String patientName;
    private String patientPhone;
    private int serialNumber;
    private String session;
    private String status;

    //default constructor for Firebase
    public AppointmentDoctorView() {}

    //constructor with all necessary fields
    public AppointmentDoctorView(String appointmentId, String appointmentDate, String patientName, String patientPhone, int serialNumber, String session, String status){
        this.appointmentId = appointmentId;
        this.appointmentDate = appointmentDate;
        this.patientName = patientName;
        this.patientPhone = patientPhone;
        this.serialNumber = serialNumber;
        this.session = session;
        this.status = status;
    }

    //getters and setters
    public String getAppointmentId(){
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId){
        this.appointmentId = appointmentId;
    }

    public String getAppointmentDate(){
        return appointmentDate;
    }

    public void setAppointmentDate(String appointmentDate){
        this.appointmentDate = appointmentDate;
    }

    public String getPatientName(){
        return patientName;
    }

    public void setPatientName(String patientName){
        this.patientName = patientName;
    }

    public String getPatientPhone(){
        return patientPhone;
    }

    public void setPatientPhone(String patientPhone){
        this.patientPhone = patientPhone;
    }

    public int getSerialNumber(){
        return serialNumber;
    }

    public void setSerialNumber(int serialNumber){
        this.serialNumber = serialNumber;
    }

    public String getSession(){
        return session;
    }

    public void setSession(String session){
        this.session = session;
    }

    public String getStatus(){
        return status;
    }

    public void setStatus(String status){
        this.status = status;
    }
}
