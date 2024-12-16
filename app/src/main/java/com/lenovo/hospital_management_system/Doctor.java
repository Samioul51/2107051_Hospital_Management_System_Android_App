package com.lenovo.hospital_management_system;

public class Doctor{
    private String id;
    private String name;
    private String specialty;

    //default constructor for firebase
    public Doctor(){
    }

    public Doctor(String id, String name, String specialty){
        this.id = id;
        this.name = name;
        this.specialty = specialty;
    }

    public String getId(){
        return id;
    }

    public void setId(String id){
        this.id = id;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getSpecialty(){
        return specialty;
    }

}
