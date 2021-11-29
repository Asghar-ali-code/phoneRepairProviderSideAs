package com.funtash.mobileprovider.livedata.View.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.funtash.mobileprovider.R;
import com.funtash.mobileprovider.response.Datas;
import com.funtash.mobileprovider.response.ScheduleList;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class RescheduleAddedAdapter extends RecyclerView.Adapter<RescheduleAddedAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<Datas> list;

    public RescheduleAddedAdapter(Context context,  ArrayList<Datas> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.timeslotrc,parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {
        try {
            holder.tvday.setBackgroundColor(ContextCompat.getColor(context, R.color.blue));
            holder.tvtime.setBackgroundColor(ContextCompat.getColor(context, R.color.blue));
            holder.tvdate.setBackgroundColor(ContextCompat.getColor(context, R.color.blue));

            holder.tvdate.setText(list.get(position).getStart_day().toString());
            holder.tvtime.setText(list.get(position).getAvailable_time().toString());
            holder.tvday.setText(list.get(position).getAvailable_day().toString());
        }catch (Exception e){
            Log.e("exp",position+" : "+e.getMessage());
        }
    }

    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout ll_main;
        TextView tvday,tvtime,tvdate;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            tvdate = itemView.findViewById(R.id.tvdate);
            tvday = itemView.findViewById(R.id.tvday);
            tvtime = itemView.findViewById(R.id.tvtime);
            ll_main=itemView.findViewById(R.id.ll_main);
        }
    }

}
