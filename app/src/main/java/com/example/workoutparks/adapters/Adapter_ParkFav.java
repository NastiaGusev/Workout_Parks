package com.example.workoutparks.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.workoutparks.R;
import com.example.workoutparks.objects.Park;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class Adapter_ParkFav extends RecyclerView.Adapter<Adapter_ParkFav.ViewHolder>{
    private ArrayList<Park> mData;
    private LayoutInflater mInflater;
    private Adapter_ParkFav.ItemClickListener mClickListener;

    public Adapter_ParkFav(Context context, ArrayList<Park> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public Adapter_ParkFav.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.list_parkfav, parent, false);
        return new Adapter_ParkFav.ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(Adapter_ParkFav.ViewHolder holder, int position) {
        Park park = mData.get(position);
        holder.parkfav_TXT_name.setText(park.getName());
        //holder.parkfav_TXT_distance.setText();
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    // convenience method for getting data at click position
    public Park getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(Adapter_ParkFav.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onMoreClick(int position);
        void onFavClick(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView parkfav_TXT_name;
        TextView parkfav_TXT_distance;
        ImageButton parkfav_BTN_addToFav;
        MaterialButton parkfav_BTN_showpark;

        public ViewHolder(View itemView) {
            super(itemView);
            parkfav_BTN_addToFav = itemView.findViewById(R.id.parkfav_BTN_addToFav);
            parkfav_TXT_name = itemView.findViewById(R.id.parkfav_TXT_name);
            parkfav_TXT_distance = itemView.findViewById(R.id.parkfav_TXT_distance);
            parkfav_BTN_showpark = itemView.findViewById(R.id.parkfav_BTN_showpark);

            parkfav_BTN_showpark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mClickListener != null) {
                        mClickListener.onMoreClick(getAdapterPosition());
                    }
                }
            });

            parkfav_BTN_addToFav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mClickListener != null) {
                        mClickListener.onFavClick(getAdapterPosition());
                    }
                }
            });

        }


    }

}

