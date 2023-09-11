package com.fin_group.aslzar.response

data class PercentInstallment(
    val result: List<Percent>
)

data class Percent(
    val coefficient: Double,
    val mounth: Int
)