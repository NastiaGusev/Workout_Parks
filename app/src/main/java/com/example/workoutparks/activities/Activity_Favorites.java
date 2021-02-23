package com.example.workoutparks.activities;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.workoutparks.R;
import com.example.workoutparks.adapters.Adapter_ParkFav;
import com.example.workoutparks.adapters.Adapter_User;
import com.example.workoutparks.objects.Park;
import com.example.workoutparks.objects.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Activity_Favorites extends Activity_Base {

    public static final String PARKS = "Parks";
    public static final String USERS = "Users";
    private User currentUser;
    private ArrayList<String> pidFavParks = new ArrayList<>();
    private ArrayList<Park> favParks = new ArrayList<>();
    private Adapter_ParkFav adapter_parkFav;

    private RecyclerView favorites_LST_userList;
    private ImageButton favorites_BTN_home;
    private ImageButton favorites_BTN_parks;
    private ImageButton favorites_BTN_chats;
    private ImageButton favorites_BTN_favorites;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__favorites);
        findViews();
        getCurrentUser();
        initView();
    }

    private void findViews() {
        favorites_LST_userList = findViewById(R.id.favorites_LST_userList);
        favorites_BTN_home = findViewById(R.id.favorites_BTN_home);
        favorites_BTN_parks = findViewById(R.id.favorites_BTN_parks);
        favorites_BTN_chats = findViewById(R.id.favorites_BTN_chats);
        favorites_BTN_favorites = findViewById(R.id.favorites_BTN_favorites);
    }

    private void initView() {
        favorites_BTN_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favorites_BTN_home.setImageResource(R.drawable.img_home2);
                favorites_BTN_favorites.setImageResource(R.drawable.img_favorite_24);
                Intent myIntent = new Intent(Activity_Favorites.this, Activity_Home.class);
                startActivity(myIntent);
                finish();
            }
        });

        favorites_BTN_parks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favorites_BTN_parks.setImageResource(R.drawable.img_location2);
                favorites_BTN_favorites.setImageResource(R.drawable.img_favorite_24);
                Intent myIntent = new Intent(Activity_Favorites.this, Activity_Parks.class);
                startActivity(myIntent);
                finish();
            }
        });
    }

    public void getParksFromServer() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(PARKS);

        if (favorites_LST_userList.getAdapter() == null) {
            initListAdapter();
        }
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot key : snapshot.getChildren()) {
                    Park park = key.getValue(Park.class);
                    if (checkIfParkIsFav(park.getPid())) {
                        updateListParkFav(park);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public boolean checkIfParkIsFav(String pid) {
        for (String park : pidFavParks) {
            if (park.equals(pid)) {
                return true;
            }
        }
        return false;
    }

    private void updateListParkFav(Park park) {
        favParks.add(park);
        adapter_parkFav.notifyItemChanged(favParks.size());
    }

    private void removeParkFromList(int position) {
        favParks.remove(position);
        favorites_LST_userList.removeViewAt(position);
        adapter_parkFav.notifyItemRemoved(position);
        adapter_parkFav.notifyItemRangeChanged(position, favParks.size());
    }

    private void initListAdapter() {
        favorites_LST_userList.setLayoutManager(new LinearLayoutManager(this));
        adapter_parkFav = new Adapter_ParkFav(this, favParks);
        adapter_parkFav.setClickListener(new Adapter_ParkFav.ItemClickListener() {

            @Override
            public void onMoreClick(int position) {
                Park park = favParks.get(position);
                Intent myIntent = new Intent(Activity_Favorites.this, Activity_ParkInfo.class);
                myIntent.putExtra(Activity_ParkInfo.PARK, park);
                startActivity(myIntent);
                finish();
            }

            @Override
            public void onFavClick(int position) {
                currentUser.removePark(favParks.get(position).getPid());
                updateUserInDataBase();
                removeParkFromList(position);
            }
        });
        favorites_LST_userList.setAdapter(adapter_parkFav);
    }

    private void getCurrentUser() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(USERS);
        myRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentUser = dataSnapshot.getValue(User.class);
                pidFavParks = currentUser.getFavParks();
                getParksFromServer();
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
}