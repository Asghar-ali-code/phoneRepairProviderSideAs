package com.funtash.mobileprovider.response

data class User(
    val has_media: Boolean,
    val id: Int,
    val media: List<Media>,
    val name: String,
    val rating: String,
    val phone_number:String,
    val profile_pic:String
)