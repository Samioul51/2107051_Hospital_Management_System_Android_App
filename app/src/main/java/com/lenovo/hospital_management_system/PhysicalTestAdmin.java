package com.lenovo.hospital_management_system;

public class PhysicalTestAdmin{
    private String name;
    private String description;
    private String availability;
    private String roomNumber;

    public PhysicalTestAdmin() {}

    public PhysicalTestAdmin(String name, String description, String availability, String roomNumber){
        this.name = name;
        this.description = description;
        this.availability = availability;
        this.roomNumber = roomNumber;
    }

    //getters and setters
    public String getName() {
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

    public String getRoomNumber(){
        return roomNumber;
    }
    public void setRoomNumber(String roomNumber){
        this.roomNumber = roomNumber;
    }
}