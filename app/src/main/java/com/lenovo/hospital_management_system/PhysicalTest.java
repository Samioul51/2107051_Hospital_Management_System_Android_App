package com.lenovo.hospital_management_system;

public class PhysicalTest{
    private String testId;
    private String name;
    private String description;
    private String availability;

    //default constructor for Firebase
    public PhysicalTest(){
    }

    //constructor to initialize PhysicalTest details
    public PhysicalTest(String name, String description, String availability){
        this.name = name;
        this.description = description;
        this.availability = availability;
    }

    //getters and setters
    public String getTestId(){
        return testId;
    }

    public void setTestId(String testId){
        this.testId = testId;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getDescription(){
        return description;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public String getAvailability(){
        return availability;
    }

    public void setAvailability(String availability){
        this.availability = availability;
    }
}
