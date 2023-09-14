package com.fin_group.aslzar.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class ProductInCart (
    var id: String,
    val name: String,
    val image: List<String>,
    val code: String,
    var count: Int,
    val sale: Number,
    val price: Number
): Parcelable, Serializable