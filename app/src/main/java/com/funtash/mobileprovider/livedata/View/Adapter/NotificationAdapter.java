package com.funtash.mobileprovider.livedata.View.Adapter;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.funtash.mobileprovider.R;
import com.funtash.mobileprovider.livedata.View.Frag.HomeFragment;
import com.funtash.mobileprovider.response.NotificationClass;
import com.funtash.mobileprovider.response.OrderClass;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder> {

    private Context context;
    private NotificationClass list;

    public NotificationAdapter(Context context, NotificationClass list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(context).inflate(R.layout.notificationsrc,parent,false);

        return new MyViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {
        try {
            holder.tvname.setText(list.getData().get(position).getDescription().toString());
            holder.tvdate.setText(parseDateToddMMyyyy(list.getData().get(position).getCreated_at().toString()));
        }catch (Exception e){
            Log.e("exp",position+" : "+e.getMessage());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public String parseDateToddMMyyyy(String time) {
        try {
            String inputPattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX";
            String outputPattern = "HH:mm-EEE dd MMM";
            SimpleDateFormat inputFormat = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                inputFormat = new SimpleDateFormat(inputPattern);
            }
            SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

            Date date = null;
            String str = null;

            try {
                assert inputFormat != null;
                date = inputFormat.parse(time);
                str = outputFormat.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return str;
        }catch (Exception e){
            Log.e("exception",e.getMessage());
            return "";
        }
    }

    public int getItemCount() {
        return list.getData().size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvname,tvdate;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            tvname = itemView.findViewById(R.id.tvtitle);

            tvdate = itemView.findViewById(R.id.tvdate);
        }
    }

}
