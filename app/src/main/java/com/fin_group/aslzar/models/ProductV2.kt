package com.fin_group.aslzar.models

data class ProductV2(
    val id: String,
    val name: String,
    val count: Int,
    val image: List<String>,
    val barcode: String,
    val category_id: String,
    val sale: Number,
    val price: Number
)