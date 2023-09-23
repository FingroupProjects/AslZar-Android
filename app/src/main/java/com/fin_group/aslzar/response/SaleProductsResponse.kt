package com.fin_group.aslzar.response

data class SaleProductsResponse(
    val result: List<ResultX>
)

data class ResultX(
    val full_name: String,
    val id: String,
    val img: List<String>,
    val name: String,
    val sale: Int
)