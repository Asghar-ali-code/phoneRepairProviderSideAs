package com.funtash.mobileprovider.response

data class ServiceClass(
    val `data`: List<Service>,
    val message: String,
    val success: Boolean
)