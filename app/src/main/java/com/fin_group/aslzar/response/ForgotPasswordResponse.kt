package com.fin_group.aslzar.response

data class ForgotPasswordResponse(
    val code: Int,
    val login: String,
    val password: String,
    val result: Boolean
)