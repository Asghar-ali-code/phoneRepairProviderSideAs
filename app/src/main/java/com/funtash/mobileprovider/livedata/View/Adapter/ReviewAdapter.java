package com.funtash.mobileprovider.livedata.View.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.funtash.mobileprovider.R;
import com.funtash.mobileprovider.livedata.View.Frag.HomeFragment;
import com.funtash.mobileprovider.response.OrderClass;
import com.funtash.mobileprovider.response.Revie;
import com.funtash.mobileprovider.response.ReviewClass;

import org.jetbrains.annotations.NotNull;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.MyViewHolder> {

    private Context context;
    private ReviewClass list;

    public ReviewAdapter(Context context, ReviewClass list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(context).inflate(R.layout.review_rc,parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {
        try {
            holder.tvname.setText(list.getData().getRevie_list().get(position).getUser().getName().toString());
            holder.tvservice.setText(list.getData().getRevie_list().get(position).getService().getName().getEn().toString());
            holder.tvcomment.setText(list.getData().getRevie_list().get(position).getComment().toString());
            holder.tvrating.setText(String.valueOf(list.getData().getRevie_list().get(position).getRate()));

            if(list.getData().getRevie_list().get(position).getUser().getProfile_pic()!=null)
                 Glide.with(context)
                    .load(list.getData().getRevie_list().get(position).getUser().getProfile_pic().toString())
                    //.placeholder(R.drawable.banner)
                    .into(holder.iv_user);

        }catch (Exception e){
            Log.e("exp",position+" : "+e.getMessage());
        }
    }

    public int getItemCount() {
        return list.getData().getRevie_list().size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_user;
        TextView tvname,tvservice,tvcomment,tvrating;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            iv_user = itemView.findViewById(R.id.img_human);
            tvname = itemView.findViewById(R.id.tv_name);
            tvservice = itemView.findViewById(R.id.tv_problem);
            tvcomment = itemView.findViewById(R.id.tvcomment);
            tvrating = itemView.findViewById(R.id.rating);
        }
    }

}
