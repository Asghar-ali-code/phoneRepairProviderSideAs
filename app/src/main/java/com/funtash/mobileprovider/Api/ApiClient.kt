package com.funtash.mobilerepairinguserapp.Api

import android.content.Context
import com.funtash.mobileprovider.Api.RetrofitClient
import com.funtash.mobileprovider.Utils.NetworkConnectionInterceptor
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {

    val URL = "https://projects.funtash.net/mobile_repairing_2/public/api/"
    var retro: Retrofit? = null
    val TIME_OUT = 3000


    private fun getRetrofit(context: Context): Retrofit? {

        if (retro == null) {
            val c = OkHttpClient.Builder()
            c.connectTimeout(TIME_OUT.toLong(), TimeUnit.SECONDS)
            c.readTimeout(TIME_OUT.toLong(), TimeUnit.SECONDS)
            c.writeTimeout(TIME_OUT.toLong(), TimeUnit.SECONDS)
            val i = HttpLoggingInterceptor()
            i.setLevel(HttpLoggingInterceptor.Level.BODY)
            c.addInterceptor(i)
            c.addInterceptor(NetworkConnectionInterceptor(context))
            val gson = GsonBuilder()
                .setLenient()
                .create()
            retro = Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(c.build())
                .build()
        }
        return retro
    }

   // val apiService: ApiInterface? = getRetrofit()?.create(ApiInterface::class.java)

    fun getApi(context: Context): ApiInterface? {
        return ApiClient.getRetrofit(context)?.create(ApiInterface::class.java)
    }
}