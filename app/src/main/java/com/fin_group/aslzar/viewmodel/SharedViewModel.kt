package com.fin_group.aslzar.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fin_group.aslzar.cart.Cart
import com.fin_group.aslzar.models.ProductInCart
import com.fin_group.aslzar.response.Count
import com.fin_group.aslzar.response.ResultX
import com.fin_group.aslzar.response.Type

class SharedViewModel: ViewModel() {

    private val _productAdded = MutableLiveData<ProductInCart?>()
    val productAdded: MutableLiveData<ProductInCart?> = _productAdded

    private val _hideCategory = MutableLiveData<Boolean>()
    val hideCategory: LiveData<Boolean> = _hideCategory

    fun onProductAddedToCart(product: ResultX, context: Context) {
        val cartProduct = ProductInCart (
            product.id,
            product.full_name,
            product.img,
            product.name,
            1,
            product.sale,
            product.price,
            "",
            0,
            0,
            0
        )

        Cart.addProduct(cartProduct, context)
        _productAdded.value = cartProduct
    }

    fun onProductAddedToCartV2(product: ResultX, context: Context, type: Type, count: Count) {
        val cartProduct = ProductInCart (
            product.id,
            product.full_name,
            product.img,
            product.name,
            1,
            product.sale,
            product.price,
            type.id,
            type.size,
            type.weight,
            count.price
        )

        Cart.addProduct(cartProduct, context)
        _productAdded.value = cartProduct
    }

    fun setHideCategory(hide: Boolean) {
        _hideCategory.value = hide
    }

    fun removeProductFromCart(productInCart: ProductInCart, context: Context) {
        Cart.removeProduct(productInCart.id, context)
        _productAdded.value = null
    }
}