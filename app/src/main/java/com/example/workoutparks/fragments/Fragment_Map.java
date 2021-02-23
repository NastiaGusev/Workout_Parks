package com.example.workoutparks.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.workoutparks.R;
import com.example.workoutparks.callbacks.CallBack_ParkPopup;
import com.example.workoutparks.callbacks.CallBack_ShowParks;
import com.example.workoutparks.objects.Park;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class Fragment_Map extends Fragment {

    private GoogleMap gMap;
    private Marker userMarker;
    private CallBack_ShowParks callBack_showParks;
    private CallBack_ParkPopup callBack_parkPopup;
    private ArrayList<Park> parks= new ArrayList<>();
    private Marker currentMarker;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            gMap = googleMap;
            if(callBack_showParks!= null){
                callBack_showParks.showPark();
            }
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
        if (getDistance(newPosition.getPosition().latitude, newPosition.getPosition().longitude) > 400| userMarker == null) {
            Log.d("TAG", "users distance: " + getDistance(newPosition.getPosition().latitude, newPosition.getPosition().longitude) );
            if (userMarker != null) {
                userMarker.remove();
                userMarker = gMap.addMarker(newPosition);
            } else {
                userMarker = gMap.addMarker(newPosition);
                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userMarker.getPosition(), 15f));
            }
            return true;
        }
        return false;
    }

    public void showPark(Park park) {
        Marker parkMarker = gMap.addMarker(new MarkerOptions().
                position(new LatLng(park.getLat(), park.getLon())).
                title(park.getName()).
                icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        parkMarker.setTag(park.getPid());
        gMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker m) {
                if (m.getTag() != null){
                    changeColorMarker(m);
                    m.showInfoWindow();

                    gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(m.getPosition(), 15f));
                    callBack_parkPopup.showPopupWindow(""+m.getTag());
                    return true;
                }
                return false;
            }
        });
    }

    public void changeColorMarker(Marker marker){
        if (currentMarker == null || currentMarker.getTag() != marker.getTag()) {
            marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
            if (currentMarker != null) {
                currentMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            }
        }
        currentMarker = marker;
    }

    public int getDistance(double lat, double lon) {
        if(userMarker == null) {
            return 0;
        }
        Location startPoint = new Location("locationA");
        startPoint.setLatitude(userMarker.getPosition().latitude);
        startPoint.setLongitude(userMarker.getPosition().longitude);

        Location endPoint = new Location("locationA");
        endPoint.setLatitude(lat);
        endPoint.setLongitude(lon);
        return ((((int)startPoint.distanceTo(endPoint) + 99) / 100 ) * 100);
    }

}