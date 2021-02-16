package com.example.workoutparks.activities;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.workoutparks.utils.MyScreenUtils;

public class Activity_Base extends AppCompatActivity {

    private  MediaPlayer mp;
    protected boolean isDoublePressToClose = false;
    private static final int TIME_INTERVAL = 2000;
    private long mBackPressed;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyScreenUtils.hideSystemUI(this);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            MyScreenUtils.hideSystemUI(this);
        }
    }

    @Override
    public void onBackPressed() {
        if (isDoublePressToClose) {
            if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
                super.onBackPressed();
            } else {
                Toast.makeText(this, "Tap back button again to exit", Toast.LENGTH_SHORT).show();
            }
            mBackPressed = System.currentTimeMillis();
        } else {
            super.onBackPressed();
        }
    }


}