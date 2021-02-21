package com.example.workoutparks.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.workoutparks.R;
import com.example.workoutparks.adapters.Adapter_Message;
import com.example.workoutparks.objects.Message;
import com.example.workoutparks.objects.Park;
import com.example.workoutparks.objects.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Activity_GroupChat extends Activity_Base {

    public static final String PARK = "PARK";
    public static final String MESSAGES = "Messages";
    FirebaseAuth auth;
    FirebaseDatabase dataBase;
    DatabaseReference messageDB;

    Park thisPark;
    String uid;
    User user;
    List<Message> messages;
    Adapter_Message adapter_message;
    RecyclerView groupChat_RCV_recycleView;
    EditText groupChat_TXT_message;
    ImageButton groupChat_IBTN_send;
    TextView groupChat_TXT_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);
        thisPark = (Park) getIntent().getSerializableExtra(PARK);

        initUser();
        initMessages();
    }

    private void initUser() {
        auth = FirebaseAuth.getInstance();
        uid = auth.getCurrentUser().getUid();
        dataBase = FirebaseDatabase.getInstance();
        dataBase.getReference("Users").child(auth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);
                findViews();
                readMessage();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        messages = new ArrayList<>();
        messageDB = dataBase.getReference(MESSAGES).child(thisPark.getPid());
    }

    private void findViews() {
        groupChat_RCV_recycleView = findViewById(R.id.groupChat_RCV_recycleView);
        groupChat_TXT_message = findViewById(R.id.groupChat_TXT_message);
        groupChat_IBTN_send = findViewById(R.id.groupChat_IBTN_send);
        groupChat_TXT_title = findViewById(R.id.groupChat_TXT_title);
        groupChat_TXT_title.setText(thisPark.getName());
    }

    private void readMessage() {
        groupChat_IBTN_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(groupChat_TXT_message.getText().toString())){
                    Message message = new Message(groupChat_TXT_message.getText().toString(), uid, user.getName());
                    groupChat_TXT_message.setText("");
                    messageDB.push().setValue(message);
                }else {
                    Toast.makeText(Activity_GroupChat.this, "You cannot send empty message!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initMessages() {
        messageDB.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Message message = snapshot.getValue(Message.class);
                message.setKey(snapshot.getKey());
                messages.add(message);
                displayMessages(messages);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Message message = snapshot.getValue(Message.class);
                message.setKey(snapshot.getKey());
                List<Message> newMessages = new ArrayList<Message>();

                for(Message m :messages){
                    if(m.getKey().equals(message.getKey())){
                        newMessages.add(message);
                    }else {
                        newMessages.add(m);
                    }
                }

                messages = newMessages;
                displayMessages(messages);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Message message = snapshot.getValue(Message.class);
                message.setKey(snapshot.getKey());
                List<Message> newMessages = new ArrayList<Message>();

                for(Message m :messages){
                    if(!m.getKey().equals(message.getKey())){
                        newMessages.add(m);
                    }
                }
                messages = newMessages;
                displayMessages(messages);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void displayMessages(List<Message> messages) {
        groupChat_RCV_recycleView.setLayoutManager(new LinearLayoutManager(Activity_GroupChat.this));
        adapter_message = new Adapter_Message(Activity_GroupChat.this, messages, messageDB, uid);
        groupChat_RCV_recycleView.setAdapter(adapter_message);
        groupChat_RCV_recycleView.scrollToPosition(adapter_message.getItemCount() -1);
    }

}