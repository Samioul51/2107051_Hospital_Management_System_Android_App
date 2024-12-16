package com.lenovo.hospital_management_system;

public class PhysicalTestAppointment {
    private String appointmentId; //unique appointment ID
    private String userId;        //firebase user ID of the patient
    private String testId;        //test ID from physical test data
    private String appointmentDate;
    private long timestamp;
    private String status;
    private String patientName;
    private String patientPhone;

    //default constructor required for Firebase
    public PhysicalTestAppointment() {
    }

    //constructor to initialize all fields
    public PhysicalTestAppointment(String appointmentId, String userId, String testId, String appointmentDate, long timestamp, String status, String patientName, String patientPhone){
        this.appointmentId = appointmentId;
        this.userId = userId;
        this.testId = testId;
        this.appointmentDate = appointmentDate;
        this.timestamp = timestamp;
        this.status = status;
        this.patientName = patientName;
        this.patientPhone = patientPhone;
    }

    //getters and setters
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

    public String getTestId(){
        return testId;
    }

    public void setTestId(String testId){
        this.testId = testId;
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
}
