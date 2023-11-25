package com.fin_group.aslzar.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class ProductInCart(
    var  id: String,
    val name: String,
    val image: List<String>,
    val code: String,
    var countInCart: Int,
    val sale: Number,
    val price: Number,
    val typeId: String,
    val size: Number,
    val weight: Number
): Parcelable, Serializable

@Parcelize
data class ProductInCartV2 (
    var id: String,
    val name: String,
    val image: List<String>,
    val code: String,
    var count: Int,
    val sale: Number,
    val price: Number,
    val typeId: String,
    val size: Number,
    val weight: String
): Parcelable, Serializable