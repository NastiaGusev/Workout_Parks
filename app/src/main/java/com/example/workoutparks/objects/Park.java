package com.example.workoutparks.objects;

public class Park {
    private String pid = "";
    private String name = "";
    private double lat = 0.0;
    private double lon = 0.0;

    public Park() { }

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

}
