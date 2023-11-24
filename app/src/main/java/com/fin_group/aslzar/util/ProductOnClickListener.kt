package com.fin_group.aslzar.util

import com.fin_group.aslzar.models.ProductV2
import com.fin_group.aslzar.response.Category
import com.fin_group.aslzar.response.Product
import com.fin_group.aslzar.response.ResultX

interface ProductOnClickListener {

    fun addToCart(product: ResultX)
    fun inStock(product: ResultX)
    fun getData(product: ResultX)
}

interface CategoryClickListener {
    fun onCategorySelected(selectedCategory: Category)
}
