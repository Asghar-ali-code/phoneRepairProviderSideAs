package com.funtash.mobileprovider.response

data class ScheduleList(
    val `data`: List<Datas>,
    val message: String,
    val success: Boolean
)

data class Datas(
    val available_day: String,
    val available_time: String,
    val created_at: String,
    val id: Int,
    val provider_id: Int,
    val start_day: String,
    val updated_at: String
)