package com.funtash.mobileprovider.response

data class Provider(
    val address: String,
    val has_media: Boolean,
    val id: Int,
    val lan: String,
    val lat: String,
    val media: List<Any>,
    val name: String,
    val phone_number: String,
    val rating: String
)