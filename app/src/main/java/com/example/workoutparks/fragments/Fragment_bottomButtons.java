package com.example.workoutparks.fragments;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.workoutparks.R;
import com.example.workoutparks.callbacks.CallBack_Favorites;
import com.example.workoutparks.callbacks.CallBack_Home;
import com.example.workoutparks.callbacks.CallBack_MyProfile;
import com.example.workoutparks.callbacks.CallBack_Parks;


public class Fragment_bottomButtons extends Fragment {

    private ImageButton bottom_BTN_home;
    private ImageButton bottom_BTN_parks;
    private ImageButton bottom_BTN_profile;
    private ImageButton bottom_BTN_favorites;

    private CallBack_Home callBack_home;
    private CallBack_Parks callBack_parks;
    private CallBack_MyProfile callBack_myProfile;
    private CallBack_Favorites callBack_favorites;

    public void setCallBack_Home(CallBack_Home callBack_home){
        this.callBack_home = callBack_home;
    }

    public void setCallBack_Parks(CallBack_Parks callBack_parks){
        this.callBack_parks = callBack_parks;
    }

    public void setCallBack_myProfile(CallBack_MyProfile callBack_myProfile){
        this.callBack_myProfile = callBack_myProfile;
    }

    public void setCallBack_favorites(CallBack_Favorites callBack_favorites){
        this.callBack_favorites = callBack_favorites;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bottom_buttons, container, false);
        findViews(view);
        return view;
    }

    private void initViews() {
        bottom_BTN_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(callBack_home!=null){
                    bottom_BTN_favorites.setImageResource(R.drawable.img_favorite);
                    bottom_BTN_profile.setImageResource(R.drawable.img_person);
                    bottom_BTN_parks.setImageResource(R.drawable.img_location);
                    bottom_BTN_home.setImageResource(R.drawable.img_home2);
                    callBack_home.gotoHome();
                }
            }
        });

        bottom_BTN_parks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(callBack_parks!=null){
                    bottom_BTN_favorites.setImageResource(R.drawable.img_favorite);
                    bottom_BTN_profile.setImageResource(R.drawable.img_person);
                    bottom_BTN_parks.setImageResource(R.drawable.img_location2);
                    bottom_BTN_home.setImageResource(R.drawable.img_home);
                    callBack_parks.gotoParks();
                }
            }
        });

        bottom_BTN_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(callBack_myProfile!= null){
                    bottom_BTN_favorites.setImageResource(R.drawable.img_favorite);
                    bottom_BTN_profile.setImageResource(R.drawable.img_person2);
                    bottom_BTN_parks.setImageResource(R.drawable.img_location);
                    bottom_BTN_home.setImageResource(R.drawable.img_home);
                    callBack_myProfile.gotoMyProfile();
                }
            }
        });

        bottom_BTN_favorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(callBack_favorites!=null){
                    bottom_BTN_favorites.setImageResource(R.drawable.img_favorite2);
                    bottom_BTN_profile.setImageResource(R.drawable.img_person);
                    bottom_BTN_parks.setImageResource(R.drawable.img_location);
                    bottom_BTN_home.setImageResource(R.drawable.img_home);
                    callBack_favorites.gotoFavorites();
                }
            }
        });
    }

    private void findViews(View view) {
        bottom_BTN_home = view.findViewById(R.id.bottom_BTN_home);
        bottom_BTN_parks = view.findViewById(R.id.bottom_BTN_parks);
        bottom_BTN_profile = view.findViewById(R.id.bottom_BTN_profile);
        bottom_BTN_favorites = view.findViewById(R.id.bottom_BTN_favorites);
        checkWhatIsPressed();
        initViews();
    }

    private void checkWhatIsPressed() {
        if(callBack_favorites==null){
            bottom_BTN_favorites.setImageResource(R.drawable.img_favorite2);
        }
        if(callBack_myProfile==null){
            bottom_BTN_profile.setImageResource(R.drawable.img_person2);
        }
        if(callBack_parks==null){
            bottom_BTN_parks.setImageResource(R.drawable.img_location2);
        }
        if(callBack_home==null){
            bottom_BTN_home.setImageResource(R.drawable.img_home2);
        }

    }

}