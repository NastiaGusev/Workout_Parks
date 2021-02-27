package com.example.workoutparks.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.workoutparks.R;
import com.example.workoutparks.adapters.Adapter_Park;
import com.example.workoutparks.adapters.Adapter_ParkFav;
import com.example.workoutparks.callbacks.CallBack_Favorites;
import com.example.workoutparks.callbacks.CallBack_Home;
import com.example.workoutparks.callbacks.CallBack_MyProfile;
import com.example.workoutparks.callbacks.CallBack_Parks;
import com.example.workoutparks.fragments.Fragment_bottomButtons;
import com.example.workoutparks.objects.Park;
import com.example.workoutparks.objects.User;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Activity_UserProfile extends Activity_Base {

    public static final String USER = "USER";
    public static final String CURRENT_USER = "CURRENT_USER";
    public static final String CURRENT_PARK = "CURRENT_PARK";
    public static final String PARKS = "Parks";

    private ShapeableImageView profile_IMG_user;
    private TextView profile_TXT_title;
    private TextView profile_TXT_ageGender;
    private TextView profile_TXT_description;
    private TextView profile_TXT_parkTitle;
    private ImageButton profile_BTN_back;
    private RecyclerView profile_LST_parksList;
    private Adapter_Park adapter_park;
    private FrameLayout profile_LAY_bottomButtons;
    private Fragment_bottomButtons fragment_buttons;

    private User user;
    private User currentUser;
    private ArrayList<Park> favParks = new ArrayList<>();
    private Park thisPark;

    private CallBack_Home callBack_home = new CallBack_Home() {
        @Override
        public void gotoHome() {
            Intent myIntent = new Intent(Activity_UserProfile.this, Activity_Home.class);
            startActivity(myIntent);
            finish();
        }
    };

    private CallBack_Parks callBack_parks = new CallBack_Parks() {
        @Override
        public void gotoParks() {
            Intent myIntent = new Intent(Activity_UserProfile.this, Activity_Parks.class);
            startActivity(myIntent);
            finish();
        }
    };

    private CallBack_Favorites callBack_favorites = new CallBack_Favorites() {
        @Override
        public void gotoFavorites() {
            Intent myIntent = new Intent(Activity_UserProfile.this, Activity_Favorites.class);
            startActivity(myIntent);
            finish();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__user_profile);
        user = (User) getIntent().getSerializableExtra(USER);
        currentUser = (User) getIntent().getSerializableExtra(CURRENT_USER);
        thisPark = (Park) getIntent().getSerializableExtra(CURRENT_PARK);
        findViews();
        initViews();
        initBottomFragment();
        getParksFromServer();
    }

    private void findViews() {
        profile_LAY_bottomButtons = findViewById(R.id.groupChat_LAY_bottomButtons);
        profile_IMG_user = findViewById(R.id.profile_IMG_user);
        profile_TXT_title = findViewById(R.id.profile_TXT_title);
        profile_TXT_ageGender = findViewById(R.id.profile_TXT_ageGender);
        profile_TXT_description = findViewById(R.id.profile_TXT_description);
        profile_TXT_parkTitle = findViewById(R.id.profile_TXT_parkTitle);

        profile_BTN_back = findViewById(R.id.profile_BTN_back);
        profile_LST_parksList = findViewById(R.id.profile_LST_parksList);
    }

    private void initViews() {
        if (!user.getImgURL().equals("")) {
            Picasso.with(Activity_UserProfile.this).load(user.getImgURL()).into(profile_IMG_user);
        }
        if (user.getFavParks().isEmpty()) {
            profile_TXT_parkTitle.setText("");
        }
        profile_TXT_title.setText(user.getName());
        profile_TXT_ageGender.setText(user.getGender() + " " + user.getAge());
        profile_TXT_description.setText(user.getDescription());

        if (thisPark != null) {
            profile_BTN_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent myIntent = new Intent(Activity_UserProfile.this, Activity_ParkInfo.class);
                    myIntent.putExtra(Activity_ParkInfo.PARK, thisPark);
                    startActivity(myIntent);
                    finish();
                }
            });
        } else {
            profile_BTN_back.setImageResource(R.drawable.img_settings);
            profile_BTN_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent myIntent = new Intent(Activity_UserProfile.this, Activity_UpdateProfile.class);
                    startActivity(myIntent);
                    finish();
                }
            });
        }
    }

    private void initBottomFragment() {
        fragment_buttons = new Fragment_bottomButtons();
        fragment_buttons.setCallBack_Home(callBack_home);
        fragment_buttons.setCallBack_Parks(callBack_parks);
        fragment_buttons.setCallBack_favorites(callBack_favorites);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.profile_LAY_bottomButtons, fragment_buttons)
                .commit();
    }

    public void getParksFromServer() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(PARKS);
        if (adapter_park == null) {
            initListAdapter();
        }
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot key : snapshot.getChildren()) {
                    Park park = key.getValue(Park.class);
                    if (user.checkIfParkInFav(park.getPid())) {
                        favParks.add(park);
                        adapter_park.notifyItemChanged(favParks.size());
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void initListAdapter() {
        profile_LST_parksList.setLayoutManager(new LinearLayoutManager(this));
        adapter_park = new Adapter_Park(this, favParks);
        adapter_park.setClickListener(new Adapter_Park.ItemClickListener() {
            @Override
            public void onMoreClick(int position) {
                Park park = favParks.get(position);
                Intent myIntent = new Intent(Activity_UserProfile.this, Activity_ParkInfo.class);
                myIntent.putExtra(Activity_ParkInfo.PARK, park);
                startActivity(myIntent);
                finish();
            }
        });
        profile_LST_parksList.setAdapter(adapter_park);
    }

}