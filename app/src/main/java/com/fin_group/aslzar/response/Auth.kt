package com.fin_group.aslzar.response

data class Auth(
    val access_token: String,
    val fio: String,
    val location: String,
    val login: String,
    val sales_plan: Number,
    val mail: String,
    val phone_number: String,
    val location_id: String
)