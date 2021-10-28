package com.funtash.mobileprovider.response

data class EarningClass(
    val `data`: DataX,
    val message: String,
    val success: Boolean


)
data class DataX(
    val totalBookings: Int,
    val totalCustomers: Int,
    val totalEarnings: Int
)