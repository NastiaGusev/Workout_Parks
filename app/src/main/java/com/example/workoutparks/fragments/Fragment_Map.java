package com.example.workoutparks.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.workoutparks.R;
import com.example.workoutparks.callbacks.CallBack_ParkPopup;
import com.example.workoutparks.callbacks.CallBack_ShowParks;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class Fragment_Map extends Fragment {

    private GoogleMap gMap;
    private Marker currentMarker;
    private CallBack_ShowParks callBack_showParks;
    private CallBack_ParkPopup callBack_parkPopup;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            gMap = googleMap;
            callBack_showParks.showPark();
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        initView();
        return view;
    }

    public void SetCallBack_ShowParks(CallBack_ShowParks callBack_showParks) {
        this.callBack_showParks = callBack_showParks;
    }

    public void SetCallBack_ParkPopup(CallBack_ParkPopup callBack_parkPopup) {
        this.callBack_parkPopup = callBack_parkPopup;
    }

    private void initView() {
        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (supportMapFragment != null) {
            supportMapFragment.getMapAsync(callback);
        }
    }

    public boolean showUser(double lat, double lon) {
        MarkerOptions newPosition = new MarkerOptions().position(new LatLng(lat, lon));
        if (getDistance(newPosition.getPosition().latitude, newPosition.getPosition().longitude) > 15 || currentMarker == null) {
            if (currentMarker != null) {
                currentMarker.remove();
                currentMarker = gMap.addMarker(newPosition);
            } else {
                currentMarker = gMap.addMarker(newPosition);
                gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentMarker.getPosition(), 15f));
            }
            return true;
        }
        return false;
    }

    public void showPark(double lat, double lon, String name) {
        Marker parkMarker = gMap.addMarker(new MarkerOptions().position(new LatLng(lat, lon)).title(name));
        parkMarker.setTag(name);
        gMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker m) {
                if (m.getTag() != null){
                    m.showInfoWindow();
                    gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(m.getPosition(), 15f));
                    callBack_parkPopup.showPopupWindow(""+ m.getTag());
                    return true;
                }
                return false;
            }
        });
    }

    public double getDistance(double lat, double lon) {
        if (currentMarker == null) {
            return 0;
        }
        Location startPoint = new Location("locationA");
        startPoint.setLatitude(currentMarker.getPosition().latitude);
        startPoint.setLongitude(currentMarker.getPosition().longitude);

        Location endPoint = new Location("locationA");
        endPoint.setLatitude(lat);
        endPoint.setLongitude(lon);
        return startPoint.distanceTo(endPoint);
    }

}