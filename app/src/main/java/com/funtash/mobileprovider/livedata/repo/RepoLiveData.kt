package com.funtash.mobileprovider.livedata.repo

import com.funtash.mobileprovider.Utils.BaseDataSource
import com.funtash.mobileprovider.response.EarningClass
import com.funtash.mobilerepairinguserapp.Api.ApiHelper
import java.security.Provider


class RepoLiveData(private val apiHelper: ApiHelper) : BaseDataSource() {


    suspend fun provider_totalearnings(apitoken : String) =
        safeApiCall { apiHelper.provider_totalearnings(apitoken)}

    suspend fun service(cid : String) =
        safeApiCall { apiHelper.services(cid)}

    suspend fun provider_orders(apitoken: String,status:String) =
        safeApiCall { apiHelper.provider_orders(apitoken,status)}

    suspend fun getnotification(apitoken: String) =
        safeApiCall { apiHelper.getnotifications(apitoken)}

    suspend fun getreview(apitoken: String) =
        safeApiCall { apiHelper.getreview(apitoken)}

    suspend fun getschedule(apitoken: String) =
        safeApiCall { apiHelper.getschedule(apitoken)}

    suspend fun getbooking_details(api_token:String,url:String) =
        safeApiCall { apiHelper.getbooking_detail(api_token,url)}

    suspend fun chat_list(api_token:String,o_id:String) =
        safeApiCall { apiHelper.chat_list(api_token,o_id)}

}