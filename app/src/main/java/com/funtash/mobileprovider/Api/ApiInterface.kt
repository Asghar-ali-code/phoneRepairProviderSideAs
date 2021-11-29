package com.funtash.mobilerepairinguserapp.Api

import com.funtash.mobileprovider.response.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*
import java.util.HashMap


interface ApiInterface {

    @FormUrlEncoded
    @POST("provider/login")
    fun loginUser(
        @Field("email") email:String,
        @Field("password") pass:String,
        @Field("device_token") device_token:String
    ): Call<LoginClass>


    @Multipart
    @POST("provider/register")
    fun provider_register(
        @PartMap map: HashMap<String, RequestBody>,
        @Part file: MultipartBody.Part
    ): Call<MessageClass>

    @Multipart
    @POST("provider/update/profile")
    fun update_profile(
        @Header("apitoken") apitoken: String,
        @PartMap map: HashMap<String, RequestBody>,
        @Part file: MultipartBody.Part?,
        @Part file2: MultipartBody.Part?
    ): Call<LoginClass>


    @FormUrlEncoded
    @POST("user/send_reset_link_email")
    fun verifyemaillink(
        @Field("email") email:String
    ): Call<MessageClass>

    @FormUrlEncoded
    @POST("provider/update/status")
    fun accept_reject(
        @Field("order_id") order_id:String,
        @Field("status") status: String,
        @Field("booking_date") booking_date: String
    ): Call<MessageClass>

    @FormUrlEncoded
    @POST("provider/update/status")
    fun order_status(
        @Field("order_id") order_id:String,
        @Field("status") status: String
    ): Call<OrderStatus>

    @FormUrlEncoded
    @POST("provider/add/sechedule")
    fun provider_sechedule(
        @Header("apitoken") apitoken:String,
        @Field("available_day") available_day:String,
        @Field("available_time") available_time: String
    ): Call<MessageClass>

    @FormUrlEncoded
    @POST("provider/updateAddress")
    fun updateAddress(
        @Header("apitoken") apitoken:String,
        @Field("lat") lat:String,
        @Field("lan") lan: String,
        @Field("address") address: String
    ): Call<MessageClass>

    @FormUrlEncoded
    @POST("provider/rescheduleBooking")
    fun rescheduleBooking(
        @Header("apitoken") apitoken:String,
        @Field("times") times:String,
        @Field("booking_id") booking_id: String
    ): Call<MessageClass>

    @FormUrlEncoded
    @POST("provider/post/chat")
    fun post_chat(
        @Header("apitoken") token: String,
        @Field("receiver_id") receiver_id:String,
        @Field("message") message:String,
        @Field("order_id") order_id:String

    ): Call<MessageClass>

    @FormUrlEncoded
    @POST("provider/updateBooking")
    fun provider_updateBooking(
        @Field("e_service") e_service:String,
        @Field("booking_id") booking_id:String
    ): Call<MessageClass>

    @GET("provider/totalearnings")
    suspend fun provider_totalearnings(
        @Header("apitoken") apitoken:String
    ): Response<EarningClass>

    @GET("services")
    suspend fun services(
        @Query("category_id") category_id:String
    ): Response<ServiceClass>

    @GET("provider/orders")
    suspend fun provider_orders(
        @Header("apitoken") apitoken:String,
        @Query("status") status:String
    ): Response<OrderClass>


    @GET("provider/notificationspakistan")
    suspend fun provider_notifications(
        @Header("apitoken") apitoken:String
    ): Response<NotificationClass>

    @GET("provider/reviews")
    suspend fun provider_reviews(
        @Header("apitoken") apitoken:String
    ): Response<ReviewClass>

    @GET("provider/get/sechedule")
    suspend fun get_sechedule(
        @Header("apitoken") apitoken:String
    ): Response<ScheduleList>

    @GET("provider/delete/sechedule")
    fun delete_sechedule(
        @Header("apitoken") apitoken:String,
        @Query("sechedule_id") sechedule_id:String
    ): Call<MessageClass>

    @GET("provider/delete/notifications")
    fun delete_notifications(
        @Header("apitoken") apitoken:String,
        @Query("notification_id") notification_id:String
    ): Call<MessageClass>

    @GET("provider/orderDetail")
    suspend fun booking_details(
        @Query("order_id") order_id:String
    ): Response<OrderDetail>

    @GET("provider/get/chat")
    suspend fun chat_list(
        @Header("apitoken") token:String,
        @Query("order_id") order_id:String
    ): Response<ChatClass>
}