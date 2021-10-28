package com.funtash.mobileprovider.response

data class ReviewClass(
    val `data`: Datar,
    val message: String,
    val success: Boolean
)
data class Datar(
    val revie_list: List<Revie>,
    val review_avg: String
)