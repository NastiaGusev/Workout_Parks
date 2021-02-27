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
    private ArrayList<String> userLikes = new ArrayList<>();
    private ArrayList<String> users= new ArrayList<>();

    public Park() { }

    public void setUsers(ArrayList<String> users) {
        this.users = users;
    }

    public void addUser(String uid){
        if(!checkIfUserIsInPark(uid)){
            users.add(uid);
        }
    }

    public void removeUser(String newUid){
        if (checkIfUserIsInPark(newUid)) {
            users.remove(newUid);
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

    public boolean checkIfUserLikedPark(String newUid){
        if(userLikes.isEmpty()){
            return false;
        }
        for (String uid: userLikes) {
            if(uid.equals(newUid)){
                return true;
            }
        }
        return false;
    }

    public void addLike(String uid){
        if (!checkIfUserLikedPark(uid)) {
            userLikes.add(uid);
        }
    }

    public void removeLike(String uid){
        if (checkIfUserLikedPark(uid)) {
            userLikes.remove(uid);
        }
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

    public Park setAddress(String address) {
        this.address = address;
        return this;
    }

    public Park setUserLikes(ArrayList<String> userLikes) {
        this.userLikes = userLikes;
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

    public ArrayList<String> getUserLikes() {
        return userLikes;
    }

    public String getAddress() {
        return address;
    }
}
