package com.parth.raktsaathi;

public class DonorModel {

    String name, mobile, blood_group, district, city, address;

    public DonorModel(String name, String mobile, String blood_group, String district, String city, String address) {
        this.name = name;
        this.mobile = mobile;
        this.blood_group = blood_group;
        this.district = district;
        this.city = city;
        this.address = address;
    }

    public String getName() { return name; }
    public String getMobile() { return mobile; }
    public String getBlood_group() { return blood_group; }
    public String getDistrict() { return district; }
    public String getCity() { return city; }
    public String getAddress() { return address; }
}