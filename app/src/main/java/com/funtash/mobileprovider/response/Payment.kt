package com.funtash.mobileprovider.response

data class Payment(
    val amount: Int,
    val advance_amount: Float,
    val remaining_amount : Float,
    val id: Int
)