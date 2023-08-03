package com.fin_group.aslzar.util

import com.fin_group.aslzar.models.Product

interface ProductOnClickListener {

    fun addToCart(product: Product)
    fun inStock(product: Product)
}