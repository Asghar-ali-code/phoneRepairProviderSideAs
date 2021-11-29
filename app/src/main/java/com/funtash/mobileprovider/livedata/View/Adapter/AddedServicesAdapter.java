package com.funtash.mobileprovider.livedata.View.Adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.funtash.mobileprovider.R;
import com.funtash.mobileprovider.response.Service;

import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;

import java.util.ArrayList;

public class AddedServicesAdapter extends RecyclerView.Adapter<AddedServicesAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<Service> list;

    public AddedServicesAdapter(Context context, ArrayList<Service> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.addedservice_layout, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {

        try {

            holder.tvname.setText(list.get(position).getName().getEn().toString());
            if (String.valueOf(list.get(position).getDiscount_price()).equals(list.get(position).getPrice())) {
                holder.tvprice.setText("$ " + list.get(position).getDiscount_price());
            } else {
                holder.tvpricest.setVisibility(View.VISIBLE);
                holder.tvpricest.setText("$ " + list.get(position).getPrice());
                holder.tvpricest.setPaintFlags(holder.tvpricest.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                holder.tvprice.setText("$ " + list.get(position).getDiscount_price());
            }
            String des = Jsoup.parse(list.get(position).getDescription().getEn()).text();
            holder.tvdescription.setText(des.trim());

            if (list.get(position).getHas_media())
                Glide.with(context)
                        .load(list.get(position).getMedia().get(0).getUrl())
                        //.placeholder(R.drawable.banner)
                        .into(holder.img);

            holder.iv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    list.remove(position);
                    notifyDataSetChanged();
                }
            });

        } catch (Exception e) {

        }
    }


    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView img, iv_delete;
        TextView tvname, tvpricest, tvprice, tvdescription;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            img = itemView.findViewById(R.id.img);
            iv_delete = itemView.findViewById(R.id.iv_delete);
            tvname = itemView.findViewById(R.id.tv_name);
            tvpricest = itemView.findViewById(R.id.tvpricest);
            tvprice = itemView.findViewById(R.id.tvprice);
            tvdescription = itemView.findViewById(R.id.tvDescription);


        }
    }
}
