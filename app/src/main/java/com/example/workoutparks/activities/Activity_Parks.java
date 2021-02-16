package com.example.workoutparks.activities;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.workoutparks.callbacks.CallBack_ParkPopup;
import com.example.workoutparks.callbacks.CallBack_ShowParks;
import com.example.workoutparks.fragments.Fragment_Map;
import com.example.workoutparks.R;
import com.example.workoutparks.callbacks.CallBack_Location;
import com.example.workoutparks.objects.Park;
import com.example.workoutparks.utils.MyLocation;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Activity_Parks extends Activity_Base {

    private Fragment_Map fragment_map;
    private MaterialCardView parks_LAY_map;
    private MyLocation myLocation;
    private boolean locationAccepted = true;
    private ArrayList<Park> parks= new ArrayList<>();

    private CallBack_Location callBack_location = new CallBack_Location() {
        @Override
        public void startLocation(double lat, double lon) {
            if (fragment_map.showUser(lat, lon)){
                getDistanceFromParks();
            }
        }
    };

    private CallBack_ShowParks callBack_showParks = new CallBack_ShowParks() {
        @Override
        public void showPark() {
            addParksToMap();
        }
    };

    private CallBack_ParkPopup callBack_parkPopup = new CallBack_ParkPopup() {
        @Override
        public void showPopupWindow(String name) {
            onButtonShowPopupWindow(name);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parks);

        findView();
        initView();
        initLocation();
        getParksFromServer();
        getDistanceFromParks();
        //addParksToServer();
    }

    private void findView() {
        parks_LAY_map = findViewById(R.id.parks_LAY_map);
    }

    private void initView() {
        fragment_map = new Fragment_Map();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.parks_LAY_map, fragment_map)
                .commit();
        fragment_map.SetCallBack_ShowParks(callBack_showParks);
        fragment_map.SetCallBack_ParkPopup(callBack_parkPopup);
    }

    private void initLocation() {
        myLocation = new MyLocation(this);
        myLocation.setCallBack_location(callBack_location);
    }

    private void getDistanceFromParks() {
        for (Park park : parks){
            Double distance = fragment_map.getDistance(park.getLat(), park.getLon());
            Log.d("TAG", "getDistanceFromParks: " + park.getName() + "Distance: " + distance);
        }
    }

    private void addParksToMap() {
        for (Park park : parks){
            fragment_map.showPark(park.getLat(), park.getLon(), park.getName());
        }
    }

    private void addParksToServer() {
        Park park1 = new Park().setLat(32.1072849620732).setLon(34.7897869419462).setName("Zilber").setPid("park-1");
        Park park2 = new Park().setLat(32.1295073636982).setLon(34.7919566281888).setName("Hameyasdim").setPid("park-2");
        Park park3 = new Park().setLat(32.1220204064956).setLon(34.8414674832841).setName("Adirim").setPid("park-3");
        Park park4 = new Park().setLat(32.11805684137114).setLon(34.84088085673903).setName("Neve Sharett").setPid("park-4");
        Park park5 = new Park().setLat(32.1206711205366).setLon(34.8376050971302).setName("Mudai - Tzahala").setPid("park-5");

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Parks");
        myRef.child(park1.getPid()).setValue(park1);
        myRef.child(park2.getPid()).setValue(park2);
        myRef.child(park3.getPid()).setValue(park3);
        myRef.child(park4.getPid()).setValue(park4);
        myRef.child(park5.getPid()).setValue(park5);
    }

    public void getParksFromServer() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Parks");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot key : snapshot.getChildren()){
                    Park park = key.getValue(Park.class);
                    parks.add(park);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void onButtonShowPopupWindow(String name) {
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_window, null);

        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, false);
        popupWindow.setOutsideTouchable(true);

        TextView popup_TXT_title = (TextView) popupWindow.getContentView().findViewById(R.id.popup_TXT_title);
        popup_TXT_title.setText(name);
        popupWindow.showAtLocation(findViewById(android.R.id.content), Gravity.BOTTOM | Gravity.CENTER, 0, 0);

        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == myLocation.MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                myLocation.getLocation();
            }
        } else {
            locationAccepted = false;
        }
    }


}