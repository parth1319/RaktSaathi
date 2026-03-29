package com.parth.raktsaathi;

public class RequestModel {

    String name, blood, city, mobile;

    public RequestModel(String name, String blood, String city, String mobile) {
        this.name = name;
        this.blood = blood;
        this.city = city;
        this.mobile = mobile;
    }

    public String getName() {
        return name;
    }

    public String getBlood() {
        return blood;
    }

    public String getCity() {
        return city;
    }

    public String getMobile() {
        return mobile;
    }
}