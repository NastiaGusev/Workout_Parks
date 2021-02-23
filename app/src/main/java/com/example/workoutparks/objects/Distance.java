package com.example.workoutparks.objects;

import android.location.Location;

public class Distance {

    public Distance() { }

    public int getDistance(double first_lat, double first_lon, double second_lat, double second_lon) {
        Location startPoint = new Location("locationA");
        startPoint.setLatitude(first_lat);
        startPoint.setLongitude(first_lon);
        Location endPoint = new Location("locationA");
        endPoint.setLatitude(second_lat);
        endPoint.setLongitude(second_lon);
        return ((((int)startPoint.distanceTo(endPoint) + 99) / 100 ) * 100);
    }
}
