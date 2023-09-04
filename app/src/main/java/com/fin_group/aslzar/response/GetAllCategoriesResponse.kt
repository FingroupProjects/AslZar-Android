package com.fin_group.aslzar.response

data class GetAllCategoriesResponse (
    val result: List<Category>
)

data class Category (
    var id: String,
    val name: String
)

