package com.example.workoutparks.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.workoutparks.R;
import com.example.workoutparks.activities.Activity_Home;
import com.example.workoutparks.activities.Activity_ParkInfo;
import com.example.workoutparks.objects.User;
import com.google.android.material.button.MaterialButton;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;

public class Adapter_User extends RecyclerView.Adapter<Adapter_User.ViewHolder> {
    private ArrayList<User> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context context;

    public Adapter_User(Context context, ArrayList<User> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.context = context;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.list_user, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        User user = mData.get(position);
        holder.user_LBL_username.setText(user.getName());
        if(!user.getImgURL().equals("")){
            Picasso.with(context).load(user.getImgURL()).into(holder.user_IMG_userimage);
        }
        holder.user_LBL_distance.setText(user.getDescription());
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    // convenience method for getting data at click position
    public User getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView user_IMG_userimage;
        TextView user_LBL_username;
        TextView user_LBL_distance;

        public ViewHolder(View itemView) {
            super(itemView);
            user_IMG_userimage = itemView.findViewById(R.id.user_IMG_userimage);
            user_LBL_username = itemView.findViewById(R.id.user_LBL_username);
            user_LBL_distance = itemView.findViewById(R.id.user_LBL_distance);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mClickListener != null) {
                        mClickListener.onItemClick(v, getAdapterPosition());
                    }
                }
            });
        }


    }

}
