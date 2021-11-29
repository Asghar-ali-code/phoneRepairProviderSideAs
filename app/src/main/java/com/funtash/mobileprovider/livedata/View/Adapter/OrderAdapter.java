package com.funtash.mobileprovider.livedata.View.Adapter;


import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.funtash.mobileprovider.R;
import com.funtash.mobileprovider.livedata.View.Activity.ActivityHome;
import com.funtash.mobileprovider.livedata.View.Activity.ActivityOnMap;
import com.funtash.mobileprovider.livedata.View.Activity.ServiceActivity;
import com.funtash.mobileprovider.livedata.View.Frag.HomeFragment;
import com.funtash.mobileprovider.livedata.View.Frag.RecievedOrderFrag;
import com.funtash.mobileprovider.livedata.View.Frag.RescheduleFragment;
import com.funtash.mobileprovider.response.OrderClass;
import com.funtash.mobileprovider.response.Service;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.pixplicity.easyprefs.library.Prefs;

import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.EventListener;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.MyViewHolder> {

    private Fragment fragment;
    private Context context;
    private OrderClass list;
    private String status,sname="";
    private ArrayList<Service> list2=new ArrayList<>();

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
            int price=0;
            if(list.getData().get(position).getService()!=null &&
                list.getData().get(position).getService().size()!=0) {
                list2.clear();
                for (Service i : list.getData().get(position).getService()) {
                    if (sname.equals(""))
                        sname = i.getName().getEn().toString();
                    else
                        sname = sname + "," + i.getName().getEn().toString();
                    price=price+i.getDiscount_price();
                }
                holder.tvname.setText(sname);
                String des=list.getData().get(position).getService().get(0).getDescription().getEn().toString();
                des = Jsoup.parse(des).text();
                holder.tvsdes.setText(des);
                if(list.getData().get(position).getService().get(0).getCategory().getHas_media())
                    Glide.with(context)
                            .load(list.getData().get(position).getService().get(0).getCategory().getMedia().get(0).getUrl())
                            //.placeholder(R.drawable.banner)
                            .into(holder.iv_service);
            }
            else {
                sname="Don't Know the Issue";
                holder.tvname.setText("Don't Know the Issue");
                holder.tvsdes.setText("Find the issue in device and fix it.");
                Glide.with(context)
                        .load(R.drawable.issue_image)
                        //.placeholder(R.drawable.banner)
                        .into(holder.iv_service);
            }


            holder.tvadd.setText(list.getData().get(position).getAddress().toString());

            if(list.getData().get(position).getBooking_status().equals("re_schedule")){
                holder.cv_reschedule.setVisibility(View.GONE);
            }

            if(list.getData().get(position).getPayment()==null){
                holder.tvpay.setText(String.valueOf("$"+price+" Pending"));
            }
            else if(list.getData().get(position).getPayment().getRemaining_amount()!=0 &&
                    !list.getData().get(position).getBooking_status().equals("completed"))
                holder.tvpay.setText(String.valueOf("$"+list.getData().get(position).getPayment().getRemaining_amount()+"Advance Pay"));
            else
                holder.tvpay.setText(String.valueOf("$"+
                        list.getData().get(position).getPayment().getAmount()+" Paid"));

            String sp=parseDateToddMMyyyy(list.getData().get(position).getTimes().toString());
            holder.tvdate.setText(sp.split("-")[1]);
            holder.tvtime.setText(sp.split("-")[0]);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(status.equals("current")){
                        ((RecievedOrderFrag)fragment).orderDialog(context,list.getData().get(position));
                    }
                    else if(!status.equals("completed")){
                        try {
                            Intent intent = new Intent(context, ActivityOnMap.class);
                            intent.putExtra("o_id", String.valueOf(list.getData().get(position).getId()));
                          /*  intent.putExtra("s_name", holder.tvname.getText().toString());
                            intent.putExtra("p_name", list.getData().get(position).getUser().getName().toString());
                            intent.putExtra("p_Add", list.getData().get(position).getAddress().toString());
                            //intent.putExtra("payment_status",list.getData().get(position).getPayment().toString());
                            intent.putExtra("date", list.getData().get(position).getBooking_at().toString());
                            intent.putExtra("p_no", list.getData().get(position).getUser().getPhone_number().toString());
                            intent.putExtra("u_id", String.valueOf(list.getData().get(position).getUser_id()));
                            intent.putExtra("u_img", String.valueOf(list.getData().get(position).getUser().getProfile_pic()));
                            intent.putExtra("status", list.getData().get(position).getBooking_status().toString());
                            intent.putExtra("p_status", holder.tvpay.getText().toString());
                            intent.putExtra("lat", list.getData().get(position).getLat().toString());
                            intent.putExtra("lng", list.getData().get(position).getLan().toString());
            */              context.startActivity(intent);

                        }catch (Exception e){
                            Log.e("exp",e.getMessage());
                        }
                    }
                }
            });

            holder.cv_reschedule.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Prefs.putString("o_id", String.valueOf(list.getData().get(position).getId()));
                    Fragment fragment = new RescheduleFragment();
                    ((ActivityHome)context).getSupportFragmentManager()
                            .beginTransaction().replace(R.id.fragmentContainer,
                            fragment,"OptionsFragment").addToBackStack(null).commit();
                }

            });

            holder.cv_detail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    list2.clear();
                    for (Service i : list.getData().get(position).getService()) {
                        list2.add(i);
                    }
                    if(list2.size()!=0)
                        bottomDlg(position);
                }
            });

        }catch (Exception e){
            Log.e("exp",position+" : "+e.getMessage());
        }
    }

    private void bottomDlg(int pos) {
        BottomSheetDialog dialog = new BottomSheetDialog(context);
        dialog.setContentView(R.layout.timebottom_layout);

        RecyclerView rvservices=dialog.findViewById(R.id.rv_services);

        rvservices.setHasFixedSize(true);
        rvservices.setLayoutManager(new LinearLayoutManager(context));

        AddedServicesAdapter adapter=new AddedServicesAdapter(context,list2);
        rvservices.setAdapter(adapter);

        Button btncontinue=dialog.findViewById(R.id.btncontinue);
        TextView txt=dialog.findViewById(R.id.text);
        txt.setText("All Services");
        btncontinue.setText("Add More Services");

        btncontinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               dialog.dismiss();
                Prefs.putString("o_id", String.valueOf(list.getData().get(pos).getId()));
               Intent intent=new Intent(context, ServiceActivity.class);
               intent.putExtra("c_id",
                       String.valueOf(list.getData().get(pos).getService().get(0).getCategory_id()));
                intent.putExtra("name",
                        list.getData().get(pos).getService().get(0).getCategory().getName().getEn().toString() );
                context.startActivity(intent);
            }
        });

        dialog.show();

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

        CardView cv_reschedule,cv_detail;
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
            cv_detail=itemView.findViewById(R.id.cv_detail);
            if(status.equals("current")){
                cv_reschedule.setVisibility(View.VISIBLE);
                cv_detail.setVisibility(View.GONE);
            }
            else if(status.equals("accepted")){
                cv_detail.setVisibility(View.GONE);
            }
        }
    }

}
