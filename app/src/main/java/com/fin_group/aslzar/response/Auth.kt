package com.fin_group.aslzar.response

data class Auth(
    val access_token: String,
    val result: Result
)
data class Result(
    val fio: String,
    val location: String,
    val login: String
)