package com.example.workoutparks.objects;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String uid = "";
    private String name = "";
    private String phone = "";
    private String currentPark = "";
    private List<String> favParks;

    public User() {
        favParks = new ArrayList<>();
    }

    public void setFavParks(List<String> favParks) {
        this.favParks = favParks;
    }

    public void checkOutOfPark(){
        setCurrentPark("no park");
    }

    public void setCurrentPark(String currentPark) {
        this.currentPark = currentPark;
    }

    public boolean checkIfInPark(){
        if (currentPark.equals("") || currentPark.equals("no park")){
            return false;
        }
        return true;
    }

    public void addPark(String pid){
        if(!checkIfParkInFav(pid)){
            favParks.add(pid);
        }
    }

    public boolean checkIfParkInFav(String newPid){
        if(favParks.isEmpty()){
            return false;
        }
        for (String pid: favParks) {
            if(pid.equals(newPid)){
                return true;
            }
        }
        return false;
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

    public String getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public List<String> getFavParks() {
        return favParks;
    }

    public String getCurrentPark() {
        return currentPark;
    }
}
