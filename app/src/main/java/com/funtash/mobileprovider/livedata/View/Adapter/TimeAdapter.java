package com.funtash.mobileprovider.livedata.View.Adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.funtash.mobileprovider.Api.RetrofitClient;
import com.funtash.mobileprovider.R;
import com.funtash.mobileprovider.livedata.View.Activity.ActivityHome;
import com.funtash.mobileprovider.response.Datas;
import com.funtash.mobileprovider.response.MessageClass;
import com.funtash.mobileprovider.response.ScheduleList;
import com.funtash.mobilerepairinguserapp.Api.ApiInterface;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.labters.lottiealertdialoglibrary.ClickListener;
import com.labters.lottiealertdialoglibrary.DialogTypes;
import com.labters.lottiealertdialoglibrary.LottieAlertDialog;
import com.pixplicity.easyprefs.library.Prefs;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TimeAdapter extends RecyclerView.Adapter<TimeAdapter.MyViewHolder> {

    private Context context;
    private ScheduleList list;
    private ArrayList<Datas> list2 = new ArrayList<>();
    private LottieAlertDialog alertDialog;

    public TimeAdapter(Context context, ScheduleList list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.timeslot_layout, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {
        try {
            holder.tvdate.setText(list.getData().get(position).getStart_day().toString());
            holder.tvtime.setText(list.getData().get(position).getAvailable_time().toString());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!list2.contains(list.getData().get(position)) && list2.size()<3) {
                        list2.add(list.getData().get(position));
                    }
                    else if(list2.contains(list.getData().get(position)))
                    {
                        list2.remove(list.getData().get(position));
                    }
                    Log.e("size", String.valueOf(list2.size()));
                    bottomDlg(position);
                }
            });
        } catch (Exception e) {
            Log.e("exp", position + " : " + e.getMessage());
        }
    }

    private void bottomDlg(int position) {
        BottomSheetDialog dialog = new BottomSheetDialog(context);
        dialog.setContentView(R.layout.timebottom_layout);

        RecyclerView rvservices = dialog.findViewById(R.id.rv_services);

        rvservices.setHasFixedSize(true);
        rvservices.setLayoutManager(new LinearLayoutManager(context));

        RescheduleAddedAdapter adapter = new RescheduleAddedAdapter(context, list2);
        rvservices.setAdapter(adapter);

        alertDialog = new LottieAlertDialog.Builder(context, DialogTypes.TYPE_LOADING)
                .setTitle("Reschedule")
                .setDescription("Please wait a moment!")
                .build();
        alertDialog.setCancelable(false);


        Button btncontinue = dialog.findViewById(R.id.btncontinue);

        btncontinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(list2.size()!=0) {
                    dialog.dismiss();
                    rescheduleApi();
                }
            }
        });

        dialog.show();

    }

    private void rescheduleApi() {
        alertDialog.show();
        String api_token = Prefs.getString("api_token", "");
        String o_id = Prefs.getString("o_id", "");
        String times = "";
        for (int i = 0; i < list2.size(); i++) {
            if (times.equals(""))
                times = list2.get(i).getStore_time().toString();
            else
                times = times + "," + list2.get(i).getStore_time().toString();
        }
        ApiInterface apiService = RetrofitClient.getClientRetro(context).create(ApiInterface.class);
        Call<MessageClass> call = apiService.rescheduleBooking(api_token.toString(), times, o_id);
        call.enqueue(new Callback<MessageClass>() {
            @Override
            public void onResponse(Call<MessageClass> call, Response<MessageClass> response) {
                try {
                    Log.e("response", "" + response.body());
                    if (response.body().getSuccess() == true) {
                        try {
                            alertDialog.dismiss();

                            LottieAlertDialog successDlg = new LottieAlertDialog.Builder(context, DialogTypes.TYPE_SUCCESS)
                                    .setTitle("Reschedule")
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
                        }
                    } else {
                        alertDialog.dismiss();
                        try {
                            LottieAlertDialog successDlg = new LottieAlertDialog.Builder(context, DialogTypes.TYPE_WARNING)
                                    .setTitle("Reschedule")
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

                    } catch (Exception exp) {
                        Toast.makeText(context, exp.toString(), Toast.LENGTH_LONG)
                                .show();
                    }

                }
            }

            @Override
            public void onFailure(Call<MessageClass> call, Throwable t) {
                try {

                    String message =t.getMessage().toString();
                    LottieAlertDialog successDlg = new LottieAlertDialog.Builder(context, DialogTypes.TYPE_ERROR)
                            .setTitle("Reschedule Failed")
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
                }catch (Exception e){

                }
            }
        });
    }

    public int getItemCount() {
        return list.getData().size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvtime, tvdate;
        CardView ll_time;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            tvdate = itemView.findViewById(R.id.tvdate);
            tvtime = itemView.findViewById(R.id.tvtime);
            ll_time=itemView.findViewById(R.id.ll_time);
        }
    }

}
