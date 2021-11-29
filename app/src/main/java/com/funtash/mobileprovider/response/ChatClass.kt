package com.funtash.mobileprovider.response

data class ChatClass(
    val `data`: List<Chat>,
    val message: String,
    val success: Boolean
)

data class Chat(
    val created_at: String,
    val id: Int,
    val message: String,
    val message_type: String,
    val order_id: Int,
    val receiver_id: Int,
    val sender_id: Int
)
