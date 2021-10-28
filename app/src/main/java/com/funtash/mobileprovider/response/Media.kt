package com.funtash.mobileprovider.response

data class Media(
    val collection_name: String,
    val disk: String,
    val file_name: String,
    val formated_size: String,
    val icon: String,
    val id: Int,
    val manipulations: List<Any>,
    val mime_type: String,
    val model_id: Int,
    val model_type: String,
    val name: String,
    val size: Int,
    val thumb: String,
    val url: String
)