package com.funtash.mobileprovider.response

data class NotificationClass(
    val `data`: List<DataN>,
    val message: String,
    val success: Boolean
)

data class DataN(
    val action: String,
    val created_at: String,
    val description: String,
    val id: Int,
    val is_read: Int,
    val sender: Sender,
    val sender_id: Int,
    val title: String,
    val updated_at: String,
    val user_id: Int
)