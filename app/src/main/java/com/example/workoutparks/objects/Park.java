package com.example.workoutparks.objects;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Park implements Serializable {
    private String pid = "";
    private String name = "";
    private double lat = 0.0;
    private double lon = 0.0;
    private String address = "";
    private int rating = 0;
    private List<String> users;

    public Park() {
        users = new ArrayList<>();
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }

    public void addUser(String uid){
        if(!checkIfUserIsInPark(uid)){
            users.add(uid);
        }
    }

    public void removeUser(String newUid){
        for (String uid: users) {
            if(uid.equals(newUid)){
                users.remove(uid);
                Log.d("TAG", "removeUser: ");
            }
        }
    }

    public boolean checkIfUserIsInPark(String newUid){
        if(users.isEmpty()){
            return false;
        }
        for (String uid: users) {
            if(uid.equals(newUid)){
                return true;
            }
        }
        return false;
    }

    public Park setPid(String pid) {
        this.pid = pid;
        return this;
    }

    public Park setName(String name) {
        this.name = name;
        return this;
    }

    public Park setLat(double lat) {
        this.lat = lat;
        return this;
    }

    public Park setLon(double lon) {
        this.lon = lon;
        return this;
    }

    public String getPid() {
        return pid;
    }

    public String getName() {
        return name;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public List<String> getUsers() {
        return users;
    }
}
