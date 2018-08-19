package com.trilokynath.iscon;


public class DATA {

    Integer ID;
    String name, address,mobile, person;

    DATA(){
        name="";
        address="";
        mobile="";
        person="";
    }
    public DATA(String name, String mobile,String address, String person) {
        this.name = name;
        this.mobile = mobile;
        this.address = address;
        this.person = person;
    }
    public DATA set(String name, String mobile,String address) {
        this.name = name;
        this.mobile = mobile;
        this.address = address;
        this.person = person;
        return this;
    }

    public Integer getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getMobile() {
        return mobile;
    }
    public String getPerson() {
        return person;
    }
    
}