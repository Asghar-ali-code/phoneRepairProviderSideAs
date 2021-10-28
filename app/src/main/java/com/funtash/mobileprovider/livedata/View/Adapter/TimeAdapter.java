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
import com.funtash.mobileprovider.response.ScheduleList;

import org.jetbrains.annotations.NotNull;

public class TimeAdapter extends RecyclerView.Adapter<TimeAdapter.MyViewHolder> {

    private Context context;
    private ScheduleList list;

    public TimeAdapter(Context context, ScheduleList list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.timeslot_layout,parent,false);

        return new MyViewHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {
        try {
            holder.tvdate.setText(list.getData().get(position).getStart_day().toString());
            holder.tvtime.setText(list.getData().get(position).getAvailable_time().toString());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }catch (Exception e){
            Log.e("exp",position+" : "+e.getMessage());
        }
    }

    public int getItemCount() {
        return list.getData().size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvtime,tvdate;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            tvdate = itemView.findViewById(R.id.tvdate);
            tvtime = itemView.findViewById(R.id.tvtime);
        }
    }

}
