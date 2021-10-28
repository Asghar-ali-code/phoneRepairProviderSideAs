package com.funtash.mobilerepairinguserapp.Api

import com.funtash.mobileprovider.response.EarningClass


class ApiHelper(private val apiInterface: ApiInterface) {

    suspend fun provider_totalearnings(apitoken: String) =
        apiInterface.provider_totalearnings(apitoken)

    suspend fun provider_orders(apitoken: String,status:String) =
        apiInterface.provider_orders(apitoken,status)

    suspend fun getnotifications(apitoken: String) =
        apiInterface.provider_notifications(apitoken)

    suspend fun getreview(apitoken: String) =
        apiInterface.provider_reviews(apitoken)

    suspend fun getschedule(apitoken: String) =
        apiInterface.get_sechedule(apitoken)

    suspend fun getbooking_detail(api_token:String,url:String)=
        apiInterface.booking_details(/*api_token,*/url)


}