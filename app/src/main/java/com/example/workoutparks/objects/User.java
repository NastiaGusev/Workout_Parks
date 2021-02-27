package com.example.workoutparks.objects;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {
    private String uid = "";
    private String phone = "";
    private String name = "";
    private String description = "";
    private int age= 0;
    private String gender = "";
    private String imgURL = "";
    private String currentPark = "";
    private double currentLat = 0.0;
    private double currentLon = 0.0;
    private ArrayList<String> favParks = new ArrayList<>();
    private ArrayList<String> friends = new ArrayList<>();

    public User() { }

    public void checkOutOfPark() {
        setCurrentPark("no park");
    }

    public void setCurrentPark(String currentPark) {
        this.currentPark = currentPark;
    }

    public boolean checkIfInPark() {
        if (currentPark.equals("") || currentPark.equals("no park")) {
            return false;
        }
        return true;
    }

    public void addFavPark(String pid) {
        if (!checkIfParkInFav(pid)) {
            favParks.add(pid);
        }
    }

    public void removeFavPark(String pid) {
        if(checkIfParkInFav(pid)){
            favParks.remove(pid);
        }
    }

    public boolean checkIfParkInFav(String newPid) {
        Log.d("TAG", "checkIfParkInFav: " + favParks.size());
        if (favParks.isEmpty()) {
            return false;
        }
        for (String pid : favParks) {
            if (pid.equals(newPid)) {
                return true;
            }
        }
        return false;
    }

    public void addFriend(String userid) {
        if (!checkIfUserInFriends(userid)) {
            friends.add(userid);
        }
    }

    public void removeFriend(String userid) {
        if(checkIfUserInFriends(userid)){
            friends.remove(userid);
        }
    }

    public boolean checkIfUserInFriends(String newUid) {
        if (friends.isEmpty()) {
            return false;
        }
        for (String uid : friends) {
            if (uid.equals(newUid)) {
                return true;
            }
        }
        return false;
    }

    public void setCurrentLat(double currentLat) {
        this.currentLat = currentLat;
    }

    public void setCurrentLon(double currentLon) {
        this.currentLon = currentLon;
    }

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

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setGender(String gender) {
        this.gender = gender;
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

    public ArrayList<String> getFavParks() {
        return favParks;
    }

    public ArrayList<String> getFriends() {
        return friends;
    }

    public void setFriends(ArrayList<String> friends) {
        this.friends = friends;
    }

    public String getCurrentPark() {
        return currentPark;
    }

    public double getCurrentLat() {
        return currentLat;
    }

    public double getCurrentLon() {
        return currentLon;
    }

    public String getImgURL() {
        return imgURL;
    }

    public String getDescription() {
        return description;
    }

    public int getAge() {
        return age;
    }

    public String getGender() {
        return gender;
    }
}
