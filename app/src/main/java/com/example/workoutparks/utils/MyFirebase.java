package com.example.workoutparks.utils;

import androidx.annotation.NonNull;

import com.example.workoutparks.callbacks.CallBack_FirebaseParks;
import com.example.workoutparks.callbacks.CallBack_FirebaseUser;
import com.example.workoutparks.callbacks.CallBack_Location;
import com.example.workoutparks.objects.Park;
import com.example.workoutparks.objects.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;

public class MyFirebase implements Serializable {

    public static final String USERS = "Users";
    public static final String PARKS = "Parks";

    public User currentUser;
    public ArrayList<Park> parks = new ArrayList<>();
    private CallBack_FirebaseUser callBack_firebaseUser;
    private CallBack_FirebaseParks callBack_firebaseParks;

    public void setCallBack_firebaseUser(CallBack_FirebaseUser callBack_firebaseUser) {
        this.callBack_firebaseUser = callBack_firebaseUser;
    }

    public void setCallBack_FirebaseParks(CallBack_FirebaseParks callBack_firebaseParks) {
        this.callBack_firebaseParks = callBack_firebaseParks;
    }

    public MyFirebase() { }

    public void initParksFromServer() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(PARKS);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot key : snapshot.getChildren()) {
                    Park park = key.getValue(Park.class);
                    parks.add(park);
                }
                callBack_firebaseParks.updateParks(parks);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void initCurrentUser() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(USERS);
        myRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentUser = dataSnapshot.getValue(User.class);
                callBack_firebaseUser.updateUser(currentUser);
            }
            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }

    public void updateParkInDataBase(Park park){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(PARKS);
        myRef.child(park.getPid()).setValue(park);
    }

    public void updateUserInDataBase(User user){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(USERS);
        myRef.child(user.getUid()).setValue(user);
        currentUser = user;
    }
}
