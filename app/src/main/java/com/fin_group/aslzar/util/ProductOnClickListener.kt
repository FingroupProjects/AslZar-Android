package com.fin_group.aslzar.util

import com.fin_group.aslzar.models.ProductV2
import com.fin_group.aslzar.response.Category
import com.fin_group.aslzar.response.Product

interface ProductOnClickListener {

    fun addToCart(product: Product)
    fun inStock(product: Product)
    fun getData(product: Product)
}

interface CategoryClickListener {
    fun onCategorySelected(selectedCategory: Category)
}
