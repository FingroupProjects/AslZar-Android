package com.fin_group.aslzar.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable

data class GetAllCategoriesResponse (
    val result: List<Category>
)
@Parcelize
data class Category (
    var id: String,
    val name: String
): Parcelable, Serializable

