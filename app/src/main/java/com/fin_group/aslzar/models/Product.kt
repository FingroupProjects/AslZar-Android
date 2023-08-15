package com.fin_group.aslzar.models

data class Product(
    val id: String,
    val name: String,
    val image: String,
    val code: String,
    val count: Int,
    val category: String
)