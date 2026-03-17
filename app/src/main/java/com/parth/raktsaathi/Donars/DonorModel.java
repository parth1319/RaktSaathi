package com.parth.raktsaathi.Donars;

public class DonorModel {

    String username;
    String mobileno;
    String emailid;
    String blood_group;
    String address;
    String city;

    public DonorModel(String username, String mobileno, String emailid, String blood_group, String address, String city) {
        this.username = username;
        this.mobileno = mobileno;
        this.emailid = emailid;
        this.blood_group = blood_group;
        this.address = address;
        this.city = city;
    }

    public String getUsername() {
        return username;
    }

    public String getMobileno() {
        return mobileno;
    }

    public String getEmailid() {
        return emailid;
    }

    public String getBlood_group() {
        return blood_group;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }
}
