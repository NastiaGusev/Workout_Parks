package com.example.workoutparks.objects;

public class User {
    private String uid = "";
    private String name = "";
    private String phone = "";

    public User() { }

    public User setUid(String uid) {
        this.uid = uid;
        return this;
    }

    public User setName(String name) {
        this.name = name;
        return this;
    }

    public User setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public String getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

}
