package com.fin_group.aslzar.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable

data class GetAllProductsResponse(
    val result: List<Product>
)

data class GetProductByIdResponse(
    val result: List<Product>
)

@Parcelize
data class Product(
    val id: String,
    val full_name: String,
    val name: String,
    val price: Number,
    val barcode: String,
    val category_id: String,
    val sale: Number,
    val color: String,
    val stone_type: String,
    val metal: String,
    val content: String,
    val size: String,
    val weight: String,
    val country_of_origin: String,
    val provider: String,
    val is_set: Boolean,
    val counts: List<InStock>,
    val img: List<String>
) : Parcelable, Serializable

@Parcelize
data class InStock(
    val store_house: String,
    val subsidiary: String,
    val count: Number,
    val sale: Number
) : Parcelable, Serializable

@Parcelize
data class InStockList(
    val counts: List<InStock>
): Parcelable, Serializable