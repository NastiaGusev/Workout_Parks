package com.example.workoutparks.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.workoutparks.R;
import com.example.workoutparks.objects.User;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Activity_Home extends Activity_Base {

    private MaterialButton home_BTN_parks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        validateUser();
        checkIfUserExistsInServer();
        findView();
        initViews();
    }

    private void validateUser() {
        //Check if a user is logged in:
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        //firebaseAuth.signOut();

        if (firebaseUser == null) {
            Intent myIntent = new Intent(Activity_Home.this, Activity_Login.class);
            startActivity(myIntent);
        }
    }

    private void checkIfUserExistsInServer(){
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users");
        myRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    addUserToServer();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }

    private void addUserToServer() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        User user = new User().setUid(firebaseUser.getUid()).setName("New User").setPhone(firebaseUser.getPhoneNumber());
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users");
        myRef.child(user.getUid()).setValue(user);
    }


    private void findView() {
        home_BTN_parks = findViewById(R.id.home_BTN_parks);
    }

    private void initViews() {
        home_BTN_parks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(Activity_Home.this, Activity_Parks.class);
                startActivity(myIntent);
                finish();
            }
        });
    }

}