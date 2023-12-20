package com.fin_group.aslzar.util

import com.fin_group.aslzar.response.Category
import com.fin_group.aslzar.response.ResultX

interface ProductOnClickListener {

    fun addToCart(product: ResultX)

    fun getData(product: ResultX)
}


interface CategoryClickListener {
    fun onCategorySelected(selectedCategory: Category)
}
