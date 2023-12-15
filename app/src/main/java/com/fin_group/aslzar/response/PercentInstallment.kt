package com.fin_group.aslzar.response

data class PercentInstallment(
    val payment_bonus: Number,
    val first_pay: Number,
    val sale_limit: Number,
    val result: List<Percent>
)

data class Percent(
    val coefficient: Number,
    val mounth: Number
)