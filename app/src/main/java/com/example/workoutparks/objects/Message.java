package com.example.workoutparks.objects;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Message {
    String message;
    String uid;
    String name;
    String key;
    String time;
    String date;

    public Message() { }

    public Message(String message, String uid, String name) {
        this.message = message;
        this.uid = uid;
        this.name = name;
        setTime();
        setDate();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private void setDate() {
        date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
    }

    private void setTime() {
        time = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
    }

    public String getMessage() {
        return message;
    }

    public String getUid() {
        return uid;
    }

    public String getKey() {
        return key;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }
}
