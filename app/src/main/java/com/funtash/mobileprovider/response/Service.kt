package com.funtash.mobileprovider.response

data class Service(
    val description: Description,
    val discount_price: Int,
    val has_media: Boolean,
    val id: Int,
    val category_id:Int,
    val is_favorite: Boolean,
    val media: List<Media>,
    val name: Name,
    val price: Int,
    val rate: Int,
    val total_reviews: Int,
    val category: Category
)