package com.fin_group.aslzar.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable

data class GetAllProductV2(
    val result: List<ResultX>
)
@Parcelize
data class ResultX(
    val barcode: String,
    val category_id: String,
    val color: String,
    val description: String,
    val full_name: String,
    val id: String,
    val img: List<String>,
    val is_set: Boolean,
    val metal: String,
    val name: String,
    val price: Number,
    val proba: String,
    val sale: Number,
    val stone_type: String,
    val types: List<Type>
): Parcelable, Serializable

@Parcelize
data class Type(
    val country_of_origin: String,
    val counts: List<Count>,
    val id: String,
    val name: String,
    val provider: String,
    val size: Number,
    val weight: Number,
    val filter: Boolean,
    var isExpandable: Boolean = false
): Parcelable, Serializable

@Parcelize
data class Count(
    val count: Int,
    val filial: String,
    val price: Number,
    val sclad: String,
    val is_filial: Boolean
): Parcelable, Serializable
