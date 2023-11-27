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
    val price: Int,
    val proba: String,
    val sale: Int,
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
    val size: Int,
    val weight: Int
): Parcelable, Serializable

@Parcelize
data class Count(
    val count: Int,
    val filial: String,
    val price: Int,
    val sclad: String
): Parcelable, Serializable
