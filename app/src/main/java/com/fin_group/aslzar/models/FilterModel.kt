package com.fin_group.aslzar.models

import android.os.Parcelable
import com.fin_group.aslzar.response.Category
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class FilterModel(
    val priceFrom: Number,
    val priceTo: Number,
    val sizeFrom: Number,
    val sizeTo: Number,
    val weightFrom: Number,
    val weightTo: Number,
    val category: Category
): Parcelable, Serializable
