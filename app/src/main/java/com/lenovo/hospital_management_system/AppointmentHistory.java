package com.lenovo.hospital_management_system;

public class AppointmentHistory {
    private String appointmentId;
    private String appointmentDate;
    private String patientName;
    private String patientPhone;
    private String status;
    private String doctorId;
    private String testId;
    private String session;
    private String doctorRoom;
    private String doctorName;
    private String testName;
    private String testRoom;
    private int serialNumber;
    private String userId;

    //getter and setter methods

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

    public String getStatus(){
        return status;
    }

    public void setStatus(String status){
        this.status = status;
    }

    public String getDoctorId(){
        return doctorId;
    }

    public void setDoctorId(String doctorId){
        this.doctorId = doctorId;
    }

    public String getTestId(){
        return testId;
    }

    public void setTestId(String testId){
        this.testId = testId;
    }

    public String getSession(){
        return session;
    }

    public void setSession(String session){
        this.session = session;
    }

    public String getDoctorRoom(){
        return doctorRoom;
    }

    public void setDoctorRoom(String doctorRoom){
        this.doctorRoom = doctorRoom;
    }

    public String getDoctorName(){
        return doctorName;
    }

    public void setDoctorName(String doctorName){
        this.doctorName = doctorName;
    }

    public String getTestName(){
        return testName;
    }

    public void setTestName(String testName){
        this.testName = testName;
    }

    public String getTestRoom(){
        return testRoom;
    }

    public void setTestRoom(String testRoom){
        this.testRoom = testRoom;
    }

    public int getSerialNumber(){
        return serialNumber;
    }

    public void setSerialNumber(int serialNumber){
        this.serialNumber = serialNumber;
    }

    public String getUserId(){
        return userId;
    }

    public void setUserId(String userId){
        this.userId = userId;
    }
}