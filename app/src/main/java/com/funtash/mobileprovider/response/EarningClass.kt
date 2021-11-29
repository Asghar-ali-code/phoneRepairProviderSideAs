package com.funtash.mobileprovider.response

data class EarningClass(
    val `data`: DataX,
    val message: String,
    val success: Boolean


)
data class DataX(
    val totalOrders: Int,
    val newOrders: Int,
    val ActiveOrders: Int
)