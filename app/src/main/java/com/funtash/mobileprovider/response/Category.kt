package com.funtash.mobileprovider.response

data class Category(
    val has_media: Boolean,
    val id: Int,
    val is_sub_category: Int,
    val media: List<Media>,
    val name: Name
)