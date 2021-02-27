package com.example.workoutparks.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.workoutparks.R;
import com.example.workoutparks.callbacks.CallBack_GetUserName;
import com.example.workoutparks.objects.User;
import com.example.workoutparks.utils.MyDataBase;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class Activity_Home extends Activity_Base {

    private final String users = "Users";
    private User currentUser;

    private MaterialButton home_BTN_parks;
    private MaterialButton home_BTN_favorites;
    private MaterialButton home_BTN_settings;
    private ImageButton home_BTN_signout;
    private TextView post_LBL_user;
    private TextView home_LBL_welcome;
    private ShapeableImageView home_IMG_user;
    private MyDataBase myDatabase = new MyDataBase();

    private CallBack_GetUserName callBack_getUserName = new CallBack_GetUserName() {
        @Override
        public void printName(String name) {
            post_LBL_user.setText(currentUser.getName());
            if(!currentUser.getName().equals("new user")){
                home_LBL_welcome.setText("Welcome Back!");
            }
            if(!currentUser.getImgURL().equals("")){
                Picasso.with(Activity_Home.this).load(currentUser.getImgURL()).into(home_IMG_user);
            }
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
        myDatabase.updateUserInDataBase(user);
    }

    private void findView() {
        home_BTN_parks = findViewById(R.id.home_BTN_parks);
        post_LBL_user = findViewById(R.id.park_LBL_parkname);
        home_LBL_welcome = findViewById(R.id.home_LBL_welcome);
        home_BTN_signout =  findViewById(R.id.home_BTN_signout);
        home_BTN_favorites = findViewById(R.id.home_BTN_favorites);
        home_BTN_settings = findViewById(R.id.home_BTN_settings);
        home_IMG_user = findViewById(R.id.home_IMG_user);
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

        home_BTN_favorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(Activity_Home.this, Activity_Favorites.class);
                startActivity(myIntent);
                finish();
            }
        });

        home_BTN_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(Activity_Home.this, Activity_UpdateProfile.class);
                startActivity(myIntent);
                finish();
            }
        });

    }

}