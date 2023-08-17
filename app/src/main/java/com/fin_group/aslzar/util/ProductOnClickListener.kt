package com.fin_group.aslzar.util

import com.fin_group.aslzar.models.Category
import com.fin_group.aslzar.models.Product
import com.fin_group.aslzar.models.ProductV2

interface ProductOnClickListener {

    fun addToCart(product: ProductV2)
    fun inStock(product: ProductV2)
}

interface CategoryClickListener {
    fun onCategorySelected(selectedCategory: Category)
}
