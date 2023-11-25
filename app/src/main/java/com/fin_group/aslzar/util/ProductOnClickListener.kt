package com.fin_group.aslzar.util

import com.fin_group.aslzar.response.Category
import com.fin_group.aslzar.response.ResultXV2

interface ProductOnClickListener {

    fun addToCart(product: ResultXV2)
    fun inStock(product: ResultXV2)
    fun getData(product: ResultXV2)
}

interface CategoryClickListener {
    fun onCategorySelected(selectedCategory: Category)
}
