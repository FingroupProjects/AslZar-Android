package com.fin_group.aslzar.cart

import android.content.Context
import android.util.Log
import com.fin_group.aslzar.models.ProductInCart
import com.fin_group.aslzar.util.CartObserver
import com.google.gson.Gson

object Cart {
    private val products: MutableList<ProductInCart> = mutableListOf()
    private const val PREFS_NAME = "my_cart_prefs"
    private const val CART_KEY = "cart"
    private val observers = mutableListOf<CartObserver>()

    fun registerObserver(observer: CartObserver) {
        observers.add(observer)
    }

    fun unregisterObserver(observer: CartObserver) {
        observers.remove(observer)
    }

    fun notifyObservers() {
        val totalPriceWithoutSale = getTotalPriceWithoutSale()
        val totalSalePrice = getTotalPriceWithSale()
        val totalCount = getTotalCount()
        val totalPrice = getTotalPrice()

        observers.forEach {
            it.onCartChanged(
                totalPriceWithoutSale,
                totalSalePrice,
                totalCount,
                totalPrice
            )
        }
    }

    fun getAllProducts(): MutableList<ProductInCart> {
        return products.toMutableList()
    }

    fun getTotalPriceWithoutSale(): Number {
        return products.sumOf { it.price.toDouble() * it.countInCart }
    }

    fun getTotalPriceWithSale(): Number {
        return products.sumOf { it.countInCart * (it.sale.toDouble() * it.price.toDouble()) / 100 }
    }

    fun getTotalPrice(): Number {
        return products.sumOf { (it.price.toDouble() * it.countInCart) - (it.countInCart * (it.sale.toDouble() * it.price.toDouble()) / 100) }
    }

    fun getTotalCount(): Int {
        return products.size
    }

    fun isCartEmpty(): Boolean {
        return products.isEmpty()
    }

    fun getProductById(productId: String): ProductInCart? {
        return products.find { it.id == productId }
    }

    fun addProduct(product: ProductInCart, context: Context) {
        try {
            val existingProduct = getProductById(product.id)

            if (existingProduct == null) {
                products.add(product)
                saveCartToPrefs(context)
            } else {
                plusProduct(product.id, context)
                saveCartToPrefs(context)
            }
            notifyObservers()
        } catch (e: Exception){
            Log.d("TAG", "addProduct: ${e.message}")
        }
    }

    fun plusProduct(productId: String, context: Context) {
        val product = getProductById(productId)
        if (product != null) {
            updateProductCount(productId, product.countInCart + 1, context)
            saveCartToPrefs(context)
        }
    }

    fun minusProduct(productId: String, context: Context) {
        val product = getProductById(productId)
        if (product != null) {
            updateProductCount(productId, product.countInCart - 1, context)
            saveCartToPrefs(context)
        }
    }

    fun updateProductCount(productId: String, newCount: Int, context: Context): Boolean {
        val productToUpdate = getProductById(productId)
        return if (productToUpdate != null) {
            productToUpdate.countInCart = newCount
            if (productToUpdate.countInCart + newCount <= 0) {
                removeProduct(productId, context)
            }
            true
        } else {
            false
        }
    }

    fun removeProduct(productId: String, context: Context) {
        val productToRemove = getProductById(productId)
        if (productToRemove != null) {
            products.remove(productToRemove)
            saveCartToPrefs(context)
            notifyObservers()
        }
    }

    fun clearAllProducts(context: Context) {
        products.clear()
        saveCartToPrefs(context)
        notifyObservers()
    }

    fun loadCartFromPrefs(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val cartJson = prefs.getString(CART_KEY, null)
        if (cartJson != null) {
            val cartItems = Gson().fromJson(cartJson, Array<ProductInCart>::class.java)
            products.clear()
            products.addAll(cartItems)
        }
    }

    fun saveCartToPrefs(context: Context) {
        val cartJson = Gson().toJson(products.toTypedArray())
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(CART_KEY, cartJson).apply()
    }

    fun getUniqueProductTypesCount(): Int {
        val cartProducts = Cart.getAllProducts()
        val uniqueProductsIds = HashSet<String>()

        for (product in cartProducts) {
            uniqueProductsIds.add(product.id)
        }

        return uniqueProductsIds.size
    }
}