package com.fin_group.aslzar.ui.fragments.cartMain.cart.functions

import android.util.Log
import android.widget.Toast
import android.annotation.SuppressLint
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fin_group.aslzar.adapter.ProductInCartAdapter
import com.fin_group.aslzar.cart.Cart
import com.fin_group.aslzar.databinding.FragmentCartBinding
import com.fin_group.aslzar.models.ProductInCart
import com.fin_group.aslzar.ui.dialogs.DeleteAllProductFromCartFragmentDialog
import com.fin_group.aslzar.ui.fragments.cartMain.cart.CartFragment
import com.fin_group.aslzar.util.CartObserver
import com.fin_group.aslzar.util.formatNumber
import com.google.android.material.snackbar.Snackbar

lateinit var itemTouchCallback: ItemTouchHelper.SimpleCallback

@SuppressLint("NotifyDataSetChanged")
fun CartFragment.fetchRV(productInCartList: List<ProductInCart>){
    recyclerView.layoutManager = LinearLayoutManager(requireContext())
    myAdapter = ProductInCartAdapter(productInCartList, this)
    recyclerView.adapter = myAdapter
    myAdapter.notifyDataSetChanged()
}

fun CartFragment.cartObserver(binding: FragmentCartBinding){
    val totalPriceWithoutSale= binding.cartTvTotalPriceWithoutSale
    val totalPriceWithSale = binding.cartTvTotalSale
    val cartTvCountProduct = binding.cartTvCountProduct
    val totalPriceTv = binding.cartTvTotalPrice

    cartObserver = object : CartObserver {
        @SuppressLint("SetTextI18n")
        override fun onCartChanged(totalPrice: Number, totalSalePrice: Number, totalCount: Int) {
            totalPriceWithSale.text = "${formatNumber(totalSalePrice)} USZ"
            totalPriceWithoutSale.text = "${formatNumber(totalPrice)} UZS"
            cartTvCountProduct.text = "$totalCount шт"
            totalPriceTv.text = "${formatNumber(totalPrice.toDouble() - totalSalePrice.toDouble())} USZ"
        }
    }
}

fun CartFragment.deleteAllProductFromCart(){
    btnDeleteAllProducts.setOnClickListener {
        if (!Cart.isCartEmpty()){
            val delete = DeleteAllProductFromCartFragmentDialog()
            delete.show(childFragmentManager, "delete_all_product_from_cart")
        } else {
            Toast.makeText(requireContext(), "Ваша корзина пуста", Toast.LENGTH_SHORT).show()
        }
    }
}

fun CartFragment.fetchItemTouchHelper(){
    itemTouchCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.adapterPosition
            val productToRemove = allProducts[position]
            Cart.removeProduct(productToRemove.id, requireContext())
            allProducts = Cart.getAllProducts()
            myAdapter.updateList(allProducts)
            Cart.notifyObservers()

            val snackbar = Snackbar.make(requireView(), "Товар удален", Snackbar.LENGTH_LONG)
            snackbar.setAction("Отменить") {
                Cart.addProduct(productToRemove, requireContext())
                allProducts = Cart.getAllProducts()
                myAdapter.updateList(allProducts)
                Cart.notifyObservers()
            }
            snackbar.show()
        }
    }
}