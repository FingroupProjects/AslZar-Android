package com.fin_group.aslzar.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fin_group.aslzar.models.ProductInCart

class SharedViewModel: ViewModel() {

    private val _productAdded = MutableLiveData<ProductInCart>()
    val productAdded: LiveData<ProductInCart> = _productAdded

    fun onProductAddedToCart(product: ProductInCart) {
        _productAdded.value = product
    }
}