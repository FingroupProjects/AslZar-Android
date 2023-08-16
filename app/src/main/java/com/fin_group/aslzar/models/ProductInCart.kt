package com.fin_group.aslzar.models

data class ProductInCart (
    var id: Int,
    val name: String,
    val image: String,
    val code: String,
    val count: Int,
    val salle: String,
    val price: String
)

