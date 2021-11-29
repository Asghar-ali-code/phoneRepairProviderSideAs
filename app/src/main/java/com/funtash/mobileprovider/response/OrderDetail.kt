package com.funtash.mobileprovider.response

data class OrderDetail(
    val `data`: DataO,
    val message: String,
    val success: Boolean
)


data class DataO(
    val address: String,
    val booking_at: String,
    val booking_status: String,
    val duration: Int,
    val e_provider: String,
    val e_service: String,
    val id: Int,
    val lan: String,
    val lat: String,
    val payment: Payment,
    val payment_id: Int,
    val provider: Provider,
    val service: List<Service>,
    val times: String,
    val user: User,
    val user_id: Int
)