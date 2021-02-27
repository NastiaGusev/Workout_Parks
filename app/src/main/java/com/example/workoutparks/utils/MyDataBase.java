package com.example.workoutparks.utils;
import com.example.workoutparks.objects.Park;
import com.example.workoutparks.objects.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.io.Serializable;

public class MyDataBase implements Serializable {

    public static final String USERS = "Users";
    public static final String PARKS = "Parks";

    public MyDataBase() { }

    public void updateParkInDataBase(Park park){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(PARKS);
        myRef.child(park.getPid()).setValue(park);
    }

    public void updateUserInDataBase(User user){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(USERS);
        myRef.child(user.getUid()).setValue(user);
    }
}
