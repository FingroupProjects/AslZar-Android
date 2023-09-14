package com.fin_group.aslzar.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fin_group.aslzar.cart.Cart
import com.fin_group.aslzar.models.ProductInCart
import com.fin_group.aslzar.response.Product

class SharedViewModel: ViewModel() {

    private val _productAdded = MutableLiveData<ProductInCart>()
    val productAdded: LiveData<ProductInCart> = _productAdded

    private val _hideCategory = MutableLiveData<Boolean>()
    val hideCategory: LiveData<Boolean> = _hideCategory

    fun onProductAddedToCart(product: Product, context: Context) {
        val cartProduct = ProductInCart (
            product.id,
            product.full_name,
            product.img,
            product.name,
            1,
            product.sale,
            product.price
        )

        Cart.addProduct(cartProduct, context)
        _productAdded.value = cartProduct
    }

    fun setHideCategory(hide: Boolean) {
        _hideCategory.value = hide
    }



}