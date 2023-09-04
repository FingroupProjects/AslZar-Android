package com.fin_group.aslzar.response

data class SalesPlanResponse(
    val result: SalesPlan
)

data class SalesPlan(
    val percent: Int,
    val user: String
)