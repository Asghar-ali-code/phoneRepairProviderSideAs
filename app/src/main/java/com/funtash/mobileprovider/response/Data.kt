package com.funtash.mobileprovider.response

data class Data(
    val address: String,
    val api_token: String,
    val device_token: String,
    val email: String,
    val email_verified_at: String,
    val has_media: Boolean,
    val id: Int,
    val lan: Any,
    val last_name: Any,
    val lat: Any,
    val media: List<Any>,
    val name: String,
    val notification: Int,
    val otp: String,
    val phone_number: Any,
    val phone_verified_at: Any,
    val profile_pic: String,
    val profile_pic2: String,
    val rating: String,
    val status: String
)