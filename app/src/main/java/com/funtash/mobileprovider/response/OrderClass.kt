package com.funtash.mobileprovider.response

data class OrderClass(
    val `data`: List<OrderData>,
    val message: String,
    val success: Boolean
)

data class OrderData(
    val address: String,
    val booking_status: String,
    val duration: Int,
    val e_service: String,
    val id: Int,
    val lan: String,
    val lat: String,
    val payment: Payment,
    val payment_id: Int,
    val service: List<Service>,
    val times: String,
    val user: User,
    val user_id: Int,
    val booking_at:String
)


