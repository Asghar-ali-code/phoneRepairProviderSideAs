package com.funtash.mobileprovider.Api;

import android.content.Context;

import com.funtash.mobileprovider.Utils.NetworkConnectionInterceptor;
import com.funtash.mobilerepairinguserapp.Api.ApiInterface;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static final String URL = "https://projects.funtash.net/mobile_repairing_2/public/api/";
    private static Retrofit retro = null;
    private static final int TIME_OUT = 3000;


    public static Retrofit getClientRetro(Context context) {

        if (retro == null) {
            OkHttpClient.Builder c = new OkHttpClient.Builder();
            c.connectTimeout(TIME_OUT, TimeUnit.SECONDS);
            c.readTimeout(TIME_OUT, TimeUnit.SECONDS);
            c.writeTimeout(TIME_OUT, TimeUnit.SECONDS);

            HttpLoggingInterceptor i = new HttpLoggingInterceptor();
            i.setLevel(HttpLoggingInterceptor.Level.BODY);
            c.addInterceptor(i);
            c.addInterceptor(new NetworkConnectionInterceptor(context));

            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            retro = new Retrofit.Builder()
                    .baseUrl(URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(c.build())
                    .build();
        }
        return retro;
    }

    public ApiInterface getApi()
    {
        return retro.create(ApiInterface.class);
    }
}
