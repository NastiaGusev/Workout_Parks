package com.example.workoutparks.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.example.workoutparks.R;
import com.example.workoutparks.objects.MyFirebase;
import com.example.workoutparks.objects.Park;
import com.example.workoutparks.objects.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Activity_ParkInfo extends Activity_Base {

    public static final String PARK = "PARK";
    public static final String USERS = "Users";
    public static final String PARKS = "Parks";
    private Park thisPark;
    private ArrayList<Park> parks = new ArrayList<>();
    private User currentUser;
    private ImageButton parkInfo_IBTN_chat;
    private ImageButton parkInfo_BTN_addToFav;
    private TextView parkInfo_TXT_parkTitle;
    private TextView parkInfo_TXT_parkAddress;
    private RatingBar parkInfo_RTB_rating;
    private CardView parkInfo_BTN_checkIn;
    private TextView parkInfo_TXT_checkIn;
    private ImageView parkInfo_IMG_placeholder;
    //private MyFirebase thisFirebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_park_info);
        thisPark = (Park) getIntent().getSerializableExtra(PARK);

        initViews();
        getParksFromServer();
        getCurrentUser();
        findViews();

    }

    public void getParksFromServer() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(PARKS);
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

    private void getCurrentUser() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(USERS);
        myRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentUser = dataSnapshot.getValue(User.class);
                initFavPark();
                initCheckIn();
            }
            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }

    private void initViews() {
        parkInfo_IBTN_chat = findViewById(R.id.parkInfo_IBTN_chat);
        parkInfo_BTN_addToFav = findViewById(R.id.parkInfo_BTN_addToFav);
        parkInfo_TXT_parkTitle = findViewById(R.id.parkInfo_TXT_parkTitle);
        parkInfo_TXT_parkAddress = findViewById(R.id.parkInfo_TXT_parkAddress);
        parkInfo_BTN_checkIn = findViewById(R.id.parkInfo_BTN_checkIn);
        parkInfo_TXT_checkIn = findViewById(R.id.parkInfo_TXT_checkIn);
        parkInfo_IMG_placeholder = findViewById(R.id.parkInfo_IMG_placeholder);
        parkInfo_RTB_rating = findViewById(R.id.parkInfo_RTB_rating);
    }

    private void findViews() {
        parkInfo_TXT_parkTitle.setText(thisPark.getName());
        parkInfo_IBTN_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(Activity_ParkInfo.this, Activity_GroupChat.class);
                myIntent.putExtra(Activity_ParkInfo.PARK, thisPark);
                startActivity(myIntent);
            }
        });
    }

    public void initCheckIn(){
        if(currentUser.getCurrentPark().equals(thisPark.getPid())){
            changeCheckOut();
        }
        parkInfo_BTN_checkIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!thisPark.checkIfUserIsInPark(currentUser.getUid()) && !currentUser.checkIfInPark()){
                    thisPark.addUser(currentUser.getUid());
                    currentUser.setCurrentPark(thisPark.getPid());
                    changeCheckOut();
                } else if(currentUser.getCurrentPark().equals(thisPark.getPid()) ){
                    currentUser.checkOutOfPark();
                    thisPark.removeUser(currentUser.getUid());
                    changeCheckIn();
                } else {
                    String pid = currentUser.getCurrentPark();
                    for (Park park : parks) {
                        if (pid.equals(park.getPid())) {
                            Toast.makeText(Activity_ParkInfo.this, "You checked in " + park.getName(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                updateParkInDataBase();
                updateUserInDataBase();
            }
        });
    }

    public void initFavPark(){
        if (currentUser.checkIfParkInFav(thisPark.getPid())) {
            parkInfo_BTN_addToFav.setImageResource(R.drawable.img_like);
        }else {
            parkInfo_BTN_addToFav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    parkInfo_BTN_addToFav.setImageResource(R.drawable.img_like);
                    currentUser.addPark(thisPark.getPid());
                    updateUserInDataBase();
                    Toast.makeText(Activity_ParkInfo.this, "Park added to favorites!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void updateParkInDataBase(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(PARKS);
        myRef.child(thisPark.getPid()).setValue(thisPark);
    }

    public void updateUserInDataBase(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(USERS);
        myRef.child(currentUser.getUid()).setValue(currentUser);
    }

    public void changeCheckIn() {
        parkInfo_BTN_checkIn.getBackground().setTint(ContextCompat.getColor(Activity_ParkInfo.this, R.color.lightred));
        parkInfo_TXT_checkIn.setTextColor(ContextCompat.getColor(Activity_ParkInfo.this, R.color.red));
        parkInfo_TXT_checkIn.setText("Check in");
        parkInfo_IMG_placeholder.setImageResource(R.drawable.img_placeholder);
        Toast.makeText(Activity_ParkInfo.this, "Rating is: " + parkInfo_RTB_rating.getRating(), Toast.LENGTH_SHORT).show();
    }

    public void changeCheckOut() {
        parkInfo_BTN_checkIn.getBackground().setTint(ContextCompat.getColor(Activity_ParkInfo.this, R.color.lighgreen));
        parkInfo_TXT_checkIn.setTextColor(ContextCompat.getColor(Activity_ParkInfo.this, R.color.green));
        parkInfo_TXT_checkIn.setText("Check out");
        parkInfo_IMG_placeholder.setImageResource(R.drawable.img_placeholdergreen);
    }
}