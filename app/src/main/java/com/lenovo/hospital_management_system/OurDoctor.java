package com.lenovo.hospital_management_system;

public class OurDoctor{
    private String name;
    private String specialty;
    private String roomNumber;

    OurDoctor(){

    }

    public OurDoctor(String name, String specialty, String roomNumber){
        this.name = name;
        this.specialty = specialty;
        this.roomNumber = roomNumber;
    }

    public String getName(){
        return name;
    }

    public String getSpecialty(){
        return specialty;
    }

    public String getRoomNumber(){
        return roomNumber;
    }
}
