package com.fin_group.aslzar.cart

import android.content.Context
import com.fin_group.aslzar.models.ProductInCart
import com.fin_group.aslzar.util.CartObserver
import com.google.gson.Gson

object Cart {
    private val products: MutableList<ProductInCart> = mutableListOf()
    private const val PREFS_NAME = "my_app_prefs"
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

        observers.forEach { it.onCartChanged(totalPriceWithoutSale, totalSalePrice, totalCount, totalPrice) }
    }

    fun getAllProducts(): MutableList<ProductInCart> {
        return products.toMutableList()
    }

    fun getTotalPriceWithoutSale(): Number {
        return products.sumOf { it.price.toDouble() * it.count }
    }

    fun getTotalPriceWithSale(): Number {
        return products.sumOf { it.count * (it.sale.toDouble() * it.price.toDouble()) / 100 }
    }

    fun getTotalPrice(): Number {
        return getTotalPriceWithoutSale().toDouble() - getTotalPriceWithoutSale().toDouble()
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
        val existingProduct = getProductById(product.id)

        if (existingProduct == null) {
            products.add(product)
            //Toast.makeText(context, "Товар добавлен в корзину", Toast.LENGTH_SHORT).show()
            saveCartToPrefs(context)
        } else {
            plusProduct(product.id, context)
            //Toast.makeText(context, "Товар увеличен на 1", Toast.LENGTH_SHORT).show()
            saveCartToPrefs(context)
        }
        notifyObservers()
    }

    fun plusProduct(productId: String, context: Context) {
        val product = getProductById(productId)
        if (product != null) {
            updateProductCount(productId, product.count + 1, context)
            saveCartToPrefs(context)
        }
    }

    fun minusProduct(productId: String, context: Context) {
        val product = getProductById(productId)
        if (product != null) {
            updateProductCount(productId, product.count - 1, context)
            saveCartToPrefs(context)
        }
    }

    fun updateProductCount(productId: String, newCount: Int, context: Context): Boolean {
        val productToUpdate = getProductById(productId)
        return if (productToUpdate != null) {
            productToUpdate.count = newCount
            if (productToUpdate.count + newCount <= 0) {
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