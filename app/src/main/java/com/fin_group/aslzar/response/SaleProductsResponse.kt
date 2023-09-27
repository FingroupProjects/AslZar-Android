package com.fin_group.aslzar.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class SaleProductsResponse(
    val result: List<ProductSale>
): Parcelable, Serializable

@Parcelize
data class ProductSale(
    val full_name: String,
    val id: String,
    val img: List<String>,
    val name: String,
    val sale: Int,
    val counts: InStock
): Parcelable, Serializable