package com.fin_group.aslzar.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class GetSimilarProductsResponse(
    val result: List<SimilarProduct>
): Parcelable, Serializable


@Parcelize
data class SimilarProduct(
    val id: String,
    val full_name: String,
    val name: String,
    val img: List<String>
) : Parcelable, Serializable

