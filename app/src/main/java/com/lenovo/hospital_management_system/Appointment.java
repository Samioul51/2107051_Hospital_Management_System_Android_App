package com.lenovo.hospital_management_system;

public class Appointment{
    private String appointmentId; //unique id for each appointment
    private String userId; //user id of the person booking the appointment
    private String doctorId; //doctor id of the doctor
    private String appointmentDate;
    private long timestamp;
    private String status;
    private String session;
    private String patientName;
    private String patientPhone;
    private int serialNumber;

    //default constructor for Firebase
    public Appointment() {
    }

    //constructor with all necessary fields
    public Appointment(String appointmentId, String userId, String doctorId, String appointmentDate, long timestamp, String status, String session, String patientName, String patientPhone, int serialNumber){
        this.appointmentId = appointmentId;
        this.userId = userId;
        this.doctorId = doctorId;
        this.appointmentDate = appointmentDate;
        this.timestamp = timestamp;
        this.status = status;
        this.session = session;
        this.patientName = patientName;
        this.patientPhone = patientPhone;
        this.serialNumber = serialNumber;
    }

    //getters and setters for all fields
    public String getAppointmentId(){
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId){
        this.appointmentId = appointmentId;
    }

    public String getUserId(){
        return userId;
    }

    public void setUserId(String userId){
        this.userId = userId;
    }

    public String getDoctorId(){
        return doctorId;
    }

    public void setDoctorId(String doctorId){
        this.doctorId = doctorId;
    }

    public String getAppointmentDate(){
        return appointmentDate;
    }

    public void setAppointmentDate(String appointmentDate){
        this.appointmentDate = appointmentDate;
    }

    public long getTimestamp(){
        return timestamp;
    }

    public void setTimestamp(long timestamp){
        this.timestamp = timestamp;
    }

    public String getStatus(){
        return status;
    }

    public void setStatus(String status){
        this.status = status;
    }

    public String getSession(){
        return session;
    }

    public void setSession(String session){
        this.session = session;
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
}
