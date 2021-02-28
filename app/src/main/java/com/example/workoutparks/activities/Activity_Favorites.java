package com.example.workoutparks.activities;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.workoutparks.R;
import com.example.workoutparks.adapters.Adapter_ParkFav;
import com.example.workoutparks.callbacks.CallBack_Home;
import com.example.workoutparks.callbacks.CallBack_MyProfile;
import com.example.workoutparks.callbacks.CallBack_Parks;
import com.example.workoutparks.fragments.Fragment_bottomButtons;
import com.example.workoutparks.objects.Park;
import com.example.workoutparks.objects.User;
import com.example.workoutparks.utils.MyDataBase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Activity_Favorites extends Activity_Base {

    public static final String PARKS = "Parks";
    public static final String USERS = "Users";
    private User currentUser;
    private ArrayList<Park> favParks = new ArrayList<>();
    private MyDataBase myDatabase = new MyDataBase();

    private Adapter_ParkFav adapter_parkFav;
    private RecyclerView favorites_LST_userList;
    private FrameLayout favorites_LAY_bottomButtons;
    private Fragment_bottomButtons fragment_buttons;

    private CallBack_Home callBack_home = new CallBack_Home() {
        @Override
        public void gotoHome() {
            Intent myIntent = new Intent(Activity_Favorites.this, Activity_Home.class);
            startActivity(myIntent);
            finish();
        }
    };

    private CallBack_MyProfile callBack_myProfile = new CallBack_MyProfile() {
        @Override
        public void gotoMyProfile() {
            Intent myIntent = new Intent(Activity_Favorites.this, Activity_UserProfile.class);
            myIntent.putExtra(Activity_UserProfile.USER, currentUser);
            startActivity(myIntent);
            finish();
        }
    };

    private CallBack_Parks callBack_parks = new CallBack_Parks() {
        @Override
        public void gotoParks() {
            Intent myIntent = new Intent(Activity_Favorites.this, Activity_Parks.class);
            startActivity(myIntent);
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__favorites);
        findViews();
        initBottomFragment();
        getCurrentUser();
    }

    private void initBottomFragment() {
        fragment_buttons = new Fragment_bottomButtons();
        fragment_buttons.setCallBack_Home(callBack_home);
        fragment_buttons.setCallBack_Parks(callBack_parks);
        fragment_buttons.setCallBack_myProfile(callBack_myProfile);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.favorites_LAY_bottomButtons, fragment_buttons)
                .commit();
    }

    private void findViews() {
        favorites_LAY_bottomButtons = findViewById(R.id.favorites_LAY_bottomButtons);
        favorites_LST_userList = findViewById(R.id.favorites_LST_userList);
    }

    //Get the parks (from user's favorites list ) from database
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
                    if (currentUser.checkIfParkInFav(park.getPid())) {
                        favParks.add(park);
                        adapter_parkFav.notifyItemChanged(favParks.size());
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    //Remove the park from the recycle-view
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
                Park park = favParks.get(position);
                Toast.makeText(Activity_Favorites.this, park.getName() + " removed from favorites!", Toast.LENGTH_LONG).show();
                currentUser.removeFavPark(park.getPid());
                myDatabase.updateUserInDataBase(currentUser);
                removeParkFromList(position);
            }
        });
        favorites_LST_userList.setAdapter(adapter_parkFav);
    }

    //get current user from database
    private void getCurrentUser() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(USERS);
        myRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //For current user - get favorite park's IDs
                currentUser = dataSnapshot.getValue(User.class);
                getParksFromServer();
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }

}