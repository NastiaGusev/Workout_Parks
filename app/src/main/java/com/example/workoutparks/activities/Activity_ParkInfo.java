package com.example.workoutparks.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.workoutparks.R;
import com.example.workoutparks.adapters.Adapter_User;
import com.example.workoutparks.callbacks.CallBack_Favorites;
import com.example.workoutparks.callbacks.CallBack_Home;
import com.example.workoutparks.callbacks.CallBack_MyProfile;
import com.example.workoutparks.callbacks.CallBack_Parks;
import com.example.workoutparks.fragments.Fragment_bottomButtons;
import com.example.workoutparks.objects.Distance;
import com.example.workoutparks.objects.Park;
import com.example.workoutparks.objects.User;
import com.example.workoutparks.utils.MyDataBase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.internal.$Gson$Preconditions;

import java.util.ArrayList;

public class Activity_ParkInfo extends Activity_Base {

    public static final String PARK = "PARK";
    public static final String USERS = "Users";
    public static final String PARKS = "Parks";
    private User currentUser;
    private Park thisPark;
    private ArrayList<Park> parks = new ArrayList<>();
    private ArrayList<User> usersInPark = new ArrayList<>();
    private Distance distance  = new Distance();
    private MyDataBase myDatabase = new MyDataBase();

    private RecyclerView parkInfo_LST_userList;
    private Adapter_User adapter_user;

    private ImageButton parkInfo_IBTN_chat;
    private ImageButton parkInfo_BTN_addToFav;
    private TextView parkInfo_TXT_parkTitle;
    private TextView parkInfo_TXT_parkAddress;
    private CardView parkInfo_BTN_checkIn;
    private TextView parkInfo_TXT_checkIn;
    private ImageView parkInfo_IMG_placeholder;
    private ImageButton parkInfo_BTN_likes;
    private ImageButton parkInfo_BTN_navigate;
    private TextView parkInfo_TXT_likes;
    private TextView parkInfo_TXT_people;

    private FrameLayout parkInfo_LAY_bottomButtons;
    private Fragment_bottomButtons fragment_buttons;

    private CallBack_Home callBack_home = new CallBack_Home() {
        @Override
        public void gotoHome() {
            Intent myIntent = new Intent(Activity_ParkInfo.this, Activity_Home.class);
            startActivity(myIntent);
            finish();
        }
    };

    private CallBack_MyProfile callBack_myProfile = new CallBack_MyProfile() {
        @Override
        public void gotoMyProfile() {
            Intent myIntent = new Intent(Activity_ParkInfo.this, Activity_UserProfile.class);
            myIntent.putExtra(Activity_UserProfile.USER, currentUser);
            startActivity(myIntent);
            finish();
        }
    };

    private CallBack_Parks callBack_parks = new CallBack_Parks() {
        @Override
        public void gotoParks() {
            Intent myIntent = new Intent(Activity_ParkInfo.this, Activity_Parks.class);
            startActivity(myIntent);
            finish();
        }
    };

    private CallBack_Favorites callBack_favorites = new CallBack_Favorites() {
        @Override
        public void gotoFavorites() {
            Intent myIntent = new Intent(Activity_ParkInfo.this, Activity_Favorites.class);
            startActivity(myIntent);
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_park_info);
        thisPark = (Park) getIntent().getSerializableExtra(PARK);
        initBottomFragment();
        findViews();
        initViews();
        getParksFromDatabase();
        getCurrentUser();
    }

    private void initBottomFragment() {
        parkInfo_LAY_bottomButtons = findViewById(R.id.parkInfo_LAY_bottomButtons);
        fragment_buttons = new Fragment_bottomButtons();
        fragment_buttons.setCallBack_Home(callBack_home);
        fragment_buttons.setCallBack_Parks(callBack_parks);
        fragment_buttons.setCallBack_myProfile(callBack_myProfile);
        fragment_buttons.setCallBack_favorites(callBack_favorites);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.parkInfo_LAY_bottomButtons, fragment_buttons)
                .commit();
    }

    private void findViews() {
        parkInfo_LST_userList = findViewById(R.id.parkInfo_LST_userList);
        parkInfo_IBTN_chat = findViewById(R.id.parkInfo_IBTN_chat);
        parkInfo_BTN_addToFav = findViewById(R.id.parkInfo_BTN_addToFav);
        parkInfo_TXT_parkTitle = findViewById(R.id.parkInfo_TXT_parkTitle);
        parkInfo_TXT_parkAddress = findViewById(R.id.parkInfo_TXT_parkAddress);
        parkInfo_BTN_checkIn = findViewById(R.id.parkInfo_BTN_checkIn);
        parkInfo_TXT_checkIn = findViewById(R.id.parkInfo_TXT_checkIn);
        parkInfo_IMG_placeholder = findViewById(R.id.parkInfo_IMG_placeholder);
        parkInfo_BTN_likes =findViewById(R.id.parkInfo_BTN_likes);
        parkInfo_TXT_likes = findViewById(R.id.parkInfo_TXT_likes);
        parkInfo_TXT_people = findViewById(R.id.parkInfo_TXT_people);
        parkInfo_BTN_navigate = findViewById(R.id.parkInfo_BTN_navigate);
    }

    private void initViews() {
        parkInfo_TXT_parkTitle.setText(thisPark.getName());
        parkInfo_TXT_parkAddress.setText(thisPark.getAddress());
        parkInfo_TXT_likes.setText("" + thisPark.getUserLikes().size());
        parkInfo_TXT_people.setText("" );

        parkInfo_BTN_navigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToPark();
            }
        });

        parkInfo_IBTN_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(Activity_ParkInfo.this, Activity_GroupChat.class);
                myIntent.putExtra(Activity_ParkInfo.PARK, thisPark);
                startActivity(myIntent);
            }
        });
    }

    private void navigateToPark() {
        Uri gmIntentUri = Uri.parse("google.navigation:q=" + thisPark.getLat() + "," + thisPark.getLon() + "&mode=w");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }

    //Get all the parks from database
    public void getParksFromDatabase() {
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
                initLikes();
                initFavPark();
                getUsersFromPark();
                initCheckIn();
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }

    private void getUsersFromPark() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(USERS);
        if (parkInfo_LST_userList.getAdapter() == null) {
            initListAdapter();
        }
        //Listener for changes in users - if users entered/exited the park
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot key : snapshot.getChildren()) {
                    User user = key.getValue(User.class);
                    if (user.getCurrentPark().equals(thisPark.getPid()) && checkIfUserInPark(user) < 0) {
                        addUserToList(user);
                    } else if (!user.getCurrentPark().equals(thisPark.getPid()) && checkIfUserInPark(user) >= 0) {
                        removeUserFromList(user);
                    }
                }
                parkInfo_TXT_people.setText("" + usersInPark.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public int checkIfUserInPark(User checkUser) {
        for (User user : usersInPark) {
            if (user.getUid().equals(checkUser.getUid())) {
                return usersInPark.indexOf(user);
            }
        }
        return -1;
    }

    private void addUserToList(User user) {
        if (user.getUid().equals(currentUser.getUid())) {
            usersInPark.add(0, user);
            adapter_user.notifyItemChanged(0);
        } else {
            usersInPark.add(user);
            adapter_user.notifyItemChanged(usersInPark.size());
        }
        adapter_user.notifyItemRangeChanged(0, usersInPark.size());
        parkInfo_TXT_people.setText("" + usersInPark.size());
    }

    private void removeUserFromList(User user) {
        int position = checkIfUserInPark(user);
        usersInPark.remove(position);
        adapter_user.notifyItemRemoved(position);
        adapter_user.notifyItemRangeChanged(position, usersInPark.size());
        parkInfo_TXT_people.setText("" + usersInPark.size());
    }

    private void initListAdapter() {
        parkInfo_LST_userList.setLayoutManager(new LinearLayoutManager(this));
        adapter_user = new Adapter_User(this, usersInPark);
        adapter_user.setClickListener(new Adapter_User.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //Go to profile of the user (can't go to current user's profile)
                if(!usersInPark.get(position).getUid().equals(currentUser.getUid())){
                    Intent myIntent = new Intent(Activity_ParkInfo.this, Activity_UserProfile.class);
                    myIntent.putExtra(Activity_UserProfile.USER, usersInPark.get(position));
                    myIntent.putExtra(Activity_UserProfile.CURRENT_USER, currentUser);
                    myIntent.putExtra(Activity_UserProfile.CURRENT_PARK, thisPark);
                    startActivity(myIntent);
                    finish();
                }
            }
        });
        parkInfo_LST_userList.setAdapter(adapter_user);
    }



    private void initLikes() {
        if(thisPark.checkIfUserLikedPark(currentUser.getUid())){
            parkInfo_BTN_likes.setImageResource(R.drawable.img_like);
        }
        parkInfo_BTN_likes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(thisPark.checkIfUserLikedPark(currentUser.getUid())){
                    parkInfo_BTN_likes.setImageResource(R.drawable.img_emptylike);
                    thisPark.removeLike(currentUser.getUid());
                }else {
                    parkInfo_BTN_likes.setImageResource(R.drawable.img_like);
                    thisPark.addLike(currentUser.getUid());
                }
                myDatabase.updateParkInDataBase(thisPark);
                parkInfo_TXT_likes.setText(""+ thisPark.getUserLikes().size());
            }
        });
    }

    public void initCheckIn() {
        if (currentUser.getCurrentPark().equals(thisPark.getPid())) {
            changeCheckOut();
        }
        parkInfo_BTN_checkIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (distance.getDistance(currentUser.getCurrentLat(), currentUser.getCurrentLon(), thisPark.getLat(), thisPark.getLon()) > 500) {
                    //User is too far away to check in park
                    Toast.makeText(Activity_ParkInfo.this, "You are too far away!", Toast.LENGTH_SHORT).show();
                } else {
                    if (!currentUser.checkIfInPark()) {
                        //The user is not in any park
                        thisPark.addUser(currentUser.getUid());
                        currentUser.setCurrentPark(thisPark.getPid());
                        changeCheckOut();
                    } else if (currentUser.getCurrentPark().equals(thisPark.getPid())) {
                        //If user is already in park- check him out
                        currentUser.checkOutOfPark();
                        thisPark.removeUser(currentUser.getUid());
                        changeCheckIn();
                    } else {
                        //The user checked in other park
                        String pid = currentUser.getCurrentPark();
                        for (Park park : parks) {
                            if (pid.equals(park.getPid())) {
                                Toast.makeText(Activity_ParkInfo.this, "You checked in " + park.getName(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    myDatabase.updateParkInDataBase(thisPark);
                    myDatabase.updateUserInDataBase(currentUser);
                }
            }
        });
    }

    public void initFavPark() {
        if (currentUser.checkIfParkInFav(thisPark.getPid())) {
            parkInfo_BTN_addToFav.setImageResource(R.drawable.img_star);
        }
        parkInfo_BTN_addToFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUser.checkIfParkInFav(thisPark.getPid())) {
                    parkInfo_BTN_addToFav.setImageResource(R.drawable.img_starempty);
                    currentUser.removeFavPark(thisPark.getPid());
                    myDatabase.updateUserInDataBase(currentUser);
                    Toast.makeText(Activity_ParkInfo.this, "Park removed from favorites", Toast.LENGTH_SHORT).show();
                } else {
                    parkInfo_BTN_addToFav.setImageResource(R.drawable.img_star);
                    currentUser.addFavPark(thisPark.getPid());
                    myDatabase.updateUserInDataBase(currentUser);
                    Toast.makeText(Activity_ParkInfo.this, "Park added to favorites!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void changeCheckIn() {
        parkInfo_BTN_checkIn.getBackground().setTint(ContextCompat.getColor(Activity_ParkInfo.this, R.color.lightred));
        parkInfo_TXT_checkIn.setTextColor(ContextCompat.getColor(Activity_ParkInfo.this, R.color.red));
        parkInfo_TXT_checkIn.setText("Check in");
        parkInfo_IMG_placeholder.setImageResource(R.drawable.img_placeholder);
    }

    public void changeCheckOut() {
        parkInfo_BTN_checkIn.getBackground().setTint(ContextCompat.getColor(Activity_ParkInfo.this, R.color.lighgreen));
        parkInfo_TXT_checkIn.setTextColor(ContextCompat.getColor(Activity_ParkInfo.this, R.color.green));
        parkInfo_TXT_checkIn.setText("Check out");
        parkInfo_IMG_placeholder.setImageResource(R.drawable.img_placeholdergreen);
    }
}