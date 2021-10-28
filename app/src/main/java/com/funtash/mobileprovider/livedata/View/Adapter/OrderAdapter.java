package com.funtash.mobileprovider.livedata.View.Adapter;


import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.funtash.mobileprovider.R;
import com.funtash.mobileprovider.livedata.View.Activity.ActivityHome;
import com.funtash.mobileprovider.livedata.View.Activity.ActivityOnMap;
import com.funtash.mobileprovider.livedata.View.Frag.HomeFragment;
import com.funtash.mobileprovider.livedata.View.Frag.RecievedOrderFrag;
import com.funtash.mobileprovider.livedata.View.Frag.RescheduleFragment;
import com.funtash.mobileprovider.response.OrderClass;

import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EventListener;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.MyViewHolder> {

    private Fragment fragment;
    private Context context;
    private OrderClass list;
    private String status;

    public OrderAdapter(Fragment fragment,Context context, OrderClass list, String status) {
        this.fragment=fragment;
        this.context = context;
        this.list = list;
        this.status =status;
    }

    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.order_layout,parent,false);

        return new MyViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {
        try {
            holder.tvname.setText(list.getData().get(position).getService().getName().getEn().toString());
            String des=list.getData().get(position).getService().getDescription().getEn().toString();
            des = Jsoup.parse(des).text();
            holder.tvsdes.setText(des);
            holder.tvadd.setText(list.getData().get(position).getAddress().toString());

            if(list.getData().get(position).getPayment()==null){
                holder.tvpay.setText(String.valueOf("Pending"));
            }
            else
                holder.tvpay.setText(String.valueOf("$"+list.getData().get(position).getPayment().getAmount()));
            String sp=parseDateToddMMyyyy(list.getData().get(position).getTimes().toString());
            holder.tvdate.setText(sp.split("-")[1]);
            holder.tvtime.setText(sp.split("-")[0]);

          /*  if(list.getData().get(position).getService().getHas_media()==true)
                 Glide.with(context)
                    .load(list.getData().get(position).getService().getMedia().get(0).getUrl())
                    //.placeholder(R.drawable.banner)
                    .into(holder.iv_service);*/

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(status.equals("current")){
                        ((RecievedOrderFrag)fragment).orderDialog(context,list.getData().get(position));
                    }
                    else if(!status.equals("completed")){
                        try {
                            Intent intent = new Intent(context, ActivityOnMap.class);
                            intent.putExtra("s_name", list.getData().get(position).getService().getName().getEn().toString());
                            intent.putExtra("p_name", list.getData().get(position).getUser().getName().toString());
                            intent.putExtra("p_Add", list.getData().get(position).getAddress().toString());
                            //intent.putExtra("payment_status",list.getData().get(position).getPayment().toString());
                            intent.putExtra("date", list.getData().get(position).getBooking_at().toString());
                            intent.putExtra("p_no", list.getData().get(position).getUser().getPhone_number().toString());
                            intent.putExtra("o_id", String.valueOf(list.getData().get(position).getId()));
                            intent.putExtra("status", list.getData().get(position).getBooking_status().toString());
                            intent.putExtra("lat", list.getData().get(position).getLat().toString());
                            intent.putExtra("lng", list.getData().get(position).getLan().toString());
                            context.startActivity(intent);

                        }catch (Exception e){
                            Log.e("exp",e.getMessage());
                        }
                    }
                }
            });

            holder.cv_reschedule.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment fragment = new RescheduleFragment();
                    ((ActivityHome)context).getSupportFragmentManager()
                            .beginTransaction().replace(R.id.fragmentContainer,
                            fragment,"OptionsFragment").addToBackStack(null).commit();
                }

            });

        }catch (Exception e){
            Log.e("exp",position+" : "+e.getMessage());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public String parseDateToddMMyyyy(String time) {
        try {
            //String inputPattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX";
            String inputPattern = "yyyy-MM-dd HH:mm";
            String outputPattern = "HH:mm-dd MMM";
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

        CardView cv_reschedule;
        ImageView iv_service;
        TextView tvname,tvsdes,tvadd,tvpay,tvtime,tvdate;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            iv_service = itemView.findViewById(R.id.iv_service);
            tvname = itemView.findViewById(R.id.tvsname);
            tvsdes = itemView.findViewById(R.id.tvsdes);
            tvpay = itemView.findViewById(R.id.tvpay);
            tvtime = itemView.findViewById(R.id.tvtime);
            tvdate=itemView.findViewById(R.id.tvdate);
            tvadd=itemView.findViewById(R.id.tvaddress);
            cv_reschedule=itemView.findViewById(R.id.cv_reschedule);
            if(status.equals("current")){
                cv_reschedule.setVisibility(View.VISIBLE);
            }
        }
    }

}
