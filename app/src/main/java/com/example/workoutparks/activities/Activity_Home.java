package com.example.workoutparks.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.workoutparks.R;
import com.example.workoutparks.callbacks.CallBack_GetUserName;
import com.example.workoutparks.objects.User;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Activity_Home extends Activity_Base {

    private final String users = "Users";
    private MaterialButton home_BTN_parks;
    private MaterialButton home_BTN_signout;
    private TextView post_LBL_user;
    private TextView home_LBL_welcome;
    private User currentUser;

    private CallBack_GetUserName callBack_getUserName = new CallBack_GetUserName() {
        @Override
        public void printName(String name) {
            post_LBL_user.setText(currentUser.getName());
            home_LBL_welcome.setText("Welcome Back!");
        }
    };

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
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser == null) {
            Intent myIntent = new Intent(Activity_Home.this, Activity_Login.class);
            startActivity(myIntent);
            finish();
        }
    }

    private void checkIfUserExistsInServer(){
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(users);
        myRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    addUserToServer();
                } else {
                    currentUser = dataSnapshot.getValue(User.class);
                    callBack_getUserName.printName(currentUser.getName());
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
        DatabaseReference myRef = database.getReference(users);
        myRef.child(user.getUid()).setValue(user);
    }

    private void findView() {
        home_BTN_parks = findViewById(R.id.home_BTN_parks);
        post_LBL_user = findViewById(R.id.post_LBL_user);
        home_LBL_welcome = findViewById(R.id.home_LBL_welcome);
        home_BTN_signout =  findViewById(R.id.home_BTN_signout);
    }

    private void initViews() {
        home_BTN_parks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(Activity_Home.this, Activity_Parks.class);
                startActivity(myIntent);
            }
        });

        home_BTN_signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.signOut();
                Intent myIntent = new Intent(Activity_Home.this, Activity_Login.class);
                startActivity(myIntent);
                finish();
            }
        });
    }

}