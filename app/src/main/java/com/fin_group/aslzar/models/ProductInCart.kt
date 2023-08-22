package com.fin_group.aslzar.models

data class ProductInCart (
    var id: String,
    val name: String,
    val image: List<String>,
    val code: String,
    var count: Int,
    val sale: Number,
    val price: Number
)