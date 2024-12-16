package com.lenovo.hospital_management_system;

public class Staff{
    private String name;
    private String email;
    private String nid;
    private String phone;
    private String address;

    //default constructor required for Firebase
    public Staff(){
    }

    public Staff(String name, String email, String nid, String phone, String address){
        this.name = name;
        this.email = email;
        this.nid = nid;
        this.phone = phone;
        this.address = address;
    }

    //getters
    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getNid() {
        return nid;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }
}
