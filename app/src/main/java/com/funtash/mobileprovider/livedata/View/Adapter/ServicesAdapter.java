package com.funtash.mobileprovider.livedata.View.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.funtash.mobileprovider.Api.RetrofitClient;
import com.funtash.mobileprovider.R;
import com.funtash.mobileprovider.livedata.View.Activity.ActivityHome;
import com.funtash.mobileprovider.response.MessageClass;
import com.funtash.mobileprovider.response.Service;
import com.funtash.mobileprovider.response.ServiceClass;
import com.funtash.mobilerepairinguserapp.Api.ApiInterface;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.labters.lottiealertdialoglibrary.ClickListener;
import com.labters.lottiealertdialoglibrary.DialogTypes;
import com.labters.lottiealertdialoglibrary.LottieAlertDialog;
import com.pixplicity.easyprefs.library.Prefs;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServicesAdapter extends RecyclerView.Adapter<ServicesAdapter.MyViewHolder> {

    private Context context;
    private ServiceClass list;
    private ArrayList<Service> list2=new ArrayList<>();
    private LottieAlertDialog alertDialog;

    public ServicesAdapter(Context context, ServiceClass list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.service_layout,parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {

        try {

            holder.tvname.setText(list.getData().get(position).getName().getEn().toString());

            if(list.getData().get(position).getHas_media())
                Glide.with(context)
                    .load(list.getData().get(position).getMedia().get(0).getUrl())
                    //.placeholder(R.drawable.banner)
                    .into(holder.img);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!list2.contains(list.getData().get(position))) {
                        list2.add(list.getData().get(position));
                    }
                    bottomDlg(position);
                }
            });

        }catch (Exception e){

        }
    }

    private void bottomDlg(int position) {
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
        btncontinue.setText("Continue");


        alertDialog = new LottieAlertDialog.Builder(context, DialogTypes.TYPE_LOADING)
                .setTitle("Adding More Services")
                .setDescription("Please wait a moment!")
                .build();
        alertDialog.setCancelable(false);

        btncontinue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String s="",name="";
                    for(int i=0;i<list2.size();i++){
                        if(s.equals("")){
                            s= String.valueOf(list2.get(i).getId());
                            name=list2.get(i).getName().getEn().toString();
                        }
                        else {
                            s = s + "," + list2.get(i).getId();
                            name=name+","+list2.get(i).getName().getEn().toString();
                        }
                    }
                    updateBookApi(s,name);
                }
            });

            dialog.show();

    }

    private void updateBookApi(String s, String name) {
        try {

            alertDialog.show();
            String o_id = Prefs.getString("o_id", "");
            ApiInterface apiService = RetrofitClient.getClientRetro(context).create(ApiInterface.class);
            Call<MessageClass> call = apiService.provider_updateBooking(s, o_id);
            call.enqueue(new Callback<MessageClass>() {
                @Override
                public void onResponse(Call<MessageClass> call, Response<MessageClass> response) {
                    try {
                        Log.e("response", "" + response.body());
                        if (response.body().getSuccess() == true) {
                            try {
                                alertDialog.dismiss();
                                LottieAlertDialog successDlg = new LottieAlertDialog.Builder(context, DialogTypes.TYPE_SUCCESS)
                                        .setTitle("Booking Updation")
                                        .setDescription(response.body().getMessage().toString())
                                        .setPositiveText("OK")
                                        .setPositiveButtonColor(ContextCompat.getColor(context, R.color.blue))
                                        .setPositiveTextColor(ContextCompat.getColor(context, R.color.white))
                                        .setPositiveListener(new ClickListener() {
                                            @Override
                                            public void onClick(@NotNull LottieAlertDialog lottieAlertDialog) {
                                                Prefs.remove("o_id");
                                                lottieAlertDialog.dismiss();
                                                context.startActivity(new Intent(context, ActivityHome.class));
                                            }
                                        })
                                        .build();
                                successDlg.show();
                            } catch (Exception e) {
                                alertDialog.dismiss();
                            }
                        } else {
                            alertDialog.dismiss();
                            try {
                                LottieAlertDialog successDlg = new LottieAlertDialog.Builder(context, DialogTypes.TYPE_WARNING)
                                        .setTitle("Booking")
                                        .setDescription(response.body().getMessage().toString())
                                        .setPositiveText("OK")
                                        .setPositiveListener(new ClickListener() {
                                            @Override
                                            public void onClick(@NotNull LottieAlertDialog lottieAlertDialog) {
                                                lottieAlertDialog.dismiss();
                                            }
                                        })
                                        .build();
                                successDlg.show();

                            } catch (Exception e) {

                            }
                        }
                    } catch (Exception e) {
                        alertDialog.dismiss();
                        try {
                            Toast.makeText(context, e.getMessage().toString(), Toast.LENGTH_LONG)
                                    .show();
                        } catch (Exception exp) {
                            Toast.makeText(context, exp.toString(), Toast.LENGTH_LONG)
                                    .show();
                        }

                    }
                }

                @Override
                public void onFailure(Call<MessageClass> call, Throwable t) {
                    alertDialog.dismiss();
                    try {
                        String message = t.getMessage().toString();
                        LottieAlertDialog successDlg = new LottieAlertDialog.Builder(context, DialogTypes.TYPE_ERROR)
                                .setTitle("Booking Failed")
                                .setDescription(message)
                                .setPositiveText("OK")
                                .setPositiveListener(new ClickListener() {
                                    @Override
                                    public void onClick(@NotNull LottieAlertDialog lottieAlertDialog) {
                                        lottieAlertDialog.dismiss();
                                    }
                                })
                                .build();
                        successDlg.show();
                    } catch (Exception e) {

                    }
                }
            });
        }catch (Exception e){
            Log.e("exp",e.getMessage().toString());
            alertDialog.dismiss();
        }
    }

    public int getItemCount() {
        return list.getData().size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView tvname;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            img = itemView.findViewById(R.id.img);
            tvname = itemView.findViewById(R.id.tv_name);

        }
    }
}
