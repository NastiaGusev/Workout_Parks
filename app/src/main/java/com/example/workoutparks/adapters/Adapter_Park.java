package com.example.workoutparks.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.workoutparks.R;
import com.example.workoutparks.objects.Distance;
import com.example.workoutparks.objects.Park;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class Adapter_Park extends RecyclerView.Adapter<Adapter_Park.ViewHolder>{

    private ArrayList<Park> mData;
    private LayoutInflater mInflater;
    private Adapter_Park.ItemClickListener mClickListener;
    private Distance distance = new Distance();

    public Adapter_Park(Context context, ArrayList<Park> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public Adapter_Park.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.list_park, parent, false);
        return new Adapter_Park.ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(Adapter_Park.ViewHolder holder, int position) {
        Park park = mData.get(position);
        holder.park_TXT_name.setText(park.getName());
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
    public void setClickListener(Adapter_Park.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onMoreClick(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView park_TXT_name;
        MaterialButton park_BTN_showpark;

        public ViewHolder(View itemView) {
            super(itemView);
            park_TXT_name = itemView.findViewById(R.id.park_TXT_name);
            park_BTN_showpark = itemView.findViewById(R.id.park_BTN_showpark);

            park_BTN_showpark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mClickListener != null) {
                        mClickListener.onMoreClick(getAdapterPosition());
                    }
                }
            });

        }


    }

}
