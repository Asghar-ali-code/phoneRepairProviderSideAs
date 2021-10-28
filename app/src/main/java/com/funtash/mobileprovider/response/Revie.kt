package com.funtash.mobileprovider.response

data class Revie(
    val comment: String,
    val duration: Int,
    val e_service: String,
    val id: Int,
    val rate: Int,
    val service: Service,
    val user: User,
    val user_id: Int
)