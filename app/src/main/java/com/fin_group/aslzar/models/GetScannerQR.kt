package com.fin_group.aslzar.models

data class GetScannerQR(
    val barcode: String,
    val category_id: String,
    val color: String,
    val description: String,
    val full_name: String,
    val id: String,
    val img: List<Any>,
    val is_set: Boolean,
    val metal: String,
    val name: String,
    val price: Int,
    val proba: String,
    val sale: Int,
    val stone_type: String,
    val types: List<Type>
)

data class Type(
    val country_of_origin: String,
    val counts: List<Count>,
    val filter: Boolean,
    val id: String,
    val name: String,
    val provider: String,
    val size: Int,
    val weight: Int
)

data class Count(
    val count: Int,
    val filial: String,
    val is_filial: Boolean,
    val price: Int,
    val sclad: String
)