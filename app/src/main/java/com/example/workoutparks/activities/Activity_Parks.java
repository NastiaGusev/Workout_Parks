package com.example.workoutparks.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.workoutparks.callbacks.CallBack_ParkPopup;
import com.example.workoutparks.callbacks.CallBack_ShowParks;
import com.example.workoutparks.fragments.Fragment_Map;
import com.example.workoutparks.R;
import com.example.workoutparks.callbacks.CallBack_Location;
import com.example.workoutparks.objects.Park;
import com.example.workoutparks.objects.User;
import com.example.workoutparks.utils.MyLocation;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Activity_Parks extends Activity_Base {

    public static final String USERS = "Users";
    private Fragment_Map fragment_map;
    private MaterialCardView parks_LAY_map;
    private MyLocation myLocation;
    private boolean locationAccepted = true;
    private ArrayList<Park> parks = new ArrayList<>();
    private User currentUser;
    private PopupWindow popupWindow;

    private TextView popup_TXT_title;
    private TextView popup_TXT_distance;
    private MaterialButton popup_BTN_show;
    private RatingBar popup_RTB_rating;
    private TextView popup_TXT_people;

    private ImageButton parks_BTN_home;
    private ImageButton parks_BTN_parks;
    private ImageButton parks_BTN_chats;
    private ImageButton parks_BTN_favorites;

    private CallBack_Location callBack_location = new CallBack_Location() {
        @Override
        public void startLocation(double lat, double lon) {
            if(fragment_map.showUser(lat, lon)){
                currentUser.setCurrentLat(lat);
                currentUser.setCurrentLon(lon);
                updateUserInDataBase();
            }
            if(callBack_showParks!= null){
                callBack_showParks.showPark();
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
        public void showPopupWindow(String pid) {
            onButtonShowPopupWindow(pid);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        fragment_map.SetCallBack_ShowParks(callBack_showParks);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parks);
        getCurrentUser();
        findView();
        initView();
        initLocation();
        getParksFromServer();
        //addParksToServer();
    }

    private void findView() {
        parks_LAY_map = findViewById(R.id.parks_LAY_map);
        parks_BTN_home = findViewById(R.id.favorites_BTN_home);
        parks_BTN_parks = findViewById(R.id.favorites_BTN_parks);
        parks_BTN_chats = findViewById(R.id.favorites_BTN_chats);
        parks_BTN_favorites = findViewById(R.id.favorites_BTN_favorites);
    }

    private void initView() {
        fragment_map = new Fragment_Map();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.parks_LAY_map, fragment_map)
                .commit();
        fragment_map.SetCallBack_ShowParks(callBack_showParks);
        fragment_map.SetCallBack_ParkPopup(callBack_parkPopup);

        parks_BTN_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parks_BTN_home.setImageResource(R.drawable.img_home2);
                parks_BTN_parks.setImageResource(R.drawable.img_location);
                Intent myIntent = new Intent(Activity_Parks.this, Activity_Home.class);
                startActivity(myIntent);
                finish();
            }
        });

        parks_BTN_favorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parks_BTN_favorites.setImageResource(R.drawable.img_favorite_2);
                parks_BTN_parks.setImageResource(R.drawable.img_location);
                Intent myIntent = new Intent(Activity_Parks.this, Activity_Favorites.class);
                startActivity(myIntent);
                finish();
            }
        });
        //TODO: 2. make chats with friends activity
    }

    private void initLocation() {
        myLocation = new MyLocation(this);
        myLocation.setCallBack_location(callBack_location);
    }


    private void addParksToMap() {
        for (Park park : parks) {
            fragment_map.showPark(park);
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
                for (DataSnapshot key : snapshot.getChildren()) {
                    Park park = key.getValue(Park.class);
                    parks.add(park);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void onButtonShowPopupWindow(String pid) {
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_window, null);
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        popupWindow = new PopupWindow(popupView, width, height, false);
        popupWindow.setOutsideTouchable(true);

        findViewsPopup(popupWindow);
        initViewsPopup(popupWindow, pid);

        popupWindow.showAtLocation(findViewById(android.R.id.content), Gravity.BOTTOM | Gravity.CENTER, 0, 0);
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });
    }

    public void findViewsPopup(PopupWindow popupWindow){
        popup_TXT_title = popupWindow.getContentView().findViewById(R.id.parkfav_TXT_name);
        popup_TXT_distance = popupWindow.getContentView().findViewById(R.id.parkfav_TXT_distance);
        popup_BTN_show = popupWindow.getContentView().findViewById(R.id.parkfav_BTN_showpark);
        popup_RTB_rating = popupWindow.getContentView().findViewById(R.id.park_RTB_rating);
        popup_TXT_people = popupWindow.getContentView().findViewById(R.id.park_TXT_people);
    }

    public void initViewsPopup( PopupWindow popupWindow , String pid){
        Park park = findParkByPid(pid);
        popup_RTB_rating.setRating(3);
        popup_TXT_title.setText(park.getName());
        popup_TXT_people.setText(""+ park.getUsers().size());
        popup_TXT_distance.setText(fragment_map.getDistance(park.getLat(), park.getLon()) + " m");
        popup_BTN_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                Intent myIntent = new Intent(Activity_Parks.this, Activity_ParkInfo.class);
                myIntent.putExtra(Activity_ParkInfo.PARK, park);
                startActivity(myIntent);
                finish();
            }
        });
    }

    public Park findParkByPid(String pid) {
        for (Park park : parks) {
            if (park.getPid().equals(pid)) {
                return park;
            }
        }
        return null;
    }

    private void getCurrentUser() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(USERS);
        myRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentUser = dataSnapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }

    public void updateUserInDataBase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(USERS);
        myRef.child(currentUser.getUid()).setValue(currentUser);
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