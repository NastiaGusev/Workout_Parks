package com.example.workoutparks.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.workoutparks.R;
import com.example.workoutparks.objects.Message;
import com.example.workoutparks.objects.User;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class Adapter_Message extends RecyclerView.Adapter {

    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    private Context context;
    private List<Message> messages;
    private DatabaseReference messageDB;
    private String uid;
    private ArrayList<User> users = new ArrayList<>();

    public Adapter_Message(Context context, List<Message> messages, DatabaseReference messageDB, String uid, ArrayList<User> users) {
        this.context = context;
        this.messages = messages;
        this.messageDB = messageDB;
        this.uid = uid;
        this.users = users;
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        if (message.getUid().equals(uid)) {
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_chat_me, parent, false);
            return new SentMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_chat_other, parent, false);
            return new ReceivedMessageHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message);
        }
    }

    private User findUser(String uid) {
        for (User user : users) {
            if (user.getUid().equals(uid)) {
                return user;
            }
        }
        return null;
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView listOther_TXT_username, listOther_TXT_time, listOther_TXT_msg;
        ShapeableImageView listOther_IMG_user;

        ReceivedMessageHolder(@NonNull View itemView) {
            super(itemView);
            listOther_TXT_msg = itemView.findViewById(R.id.listOther_TXT_msg);
            listOther_TXT_time = itemView.findViewById(R.id.listOther_TXT_time);
            listOther_TXT_username = itemView.findViewById(R.id.listOther_TXT_username);
            listOther_IMG_user = itemView.findViewById(R.id.listOther_IMG_user);
        }

        void bind(Message message) {
            User user = findUser(message.getUid());
            listOther_TXT_msg.setText(message.getMessage());
            listOther_TXT_time.setText(message.getTime());
            if (user == null) {
                listOther_TXT_username.setText(message.getName());
            } else {
                listOther_TXT_username.setText(user.getName());
                if (!user.getImgURL().equals("")) {
                    Picasso.with(context).load(user.getImgURL()).into(listOther_IMG_user);
                }
            }
        }
    }

    private class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView listOther_TXT_msg, listOther_TXT_time;

        SentMessageHolder(@NonNull View itemView) {
            super(itemView);
            listOther_TXT_msg = itemView.findViewById(R.id.listOther_TXT_msg);
            listOther_TXT_time = itemView.findViewById(R.id.listOther_TXT_time);
        }

        void bind(Message message) {
            listOther_TXT_msg.setText(message.getMessage());
            listOther_TXT_time.setText(message.getTime());
        }
    }

}
