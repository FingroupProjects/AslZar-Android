package com.fin_group.aslzar.ui.fragments.cartMain.cart.functions

import android.widget.Toast
import android.annotation.SuppressLint
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fin_group.aslzar.R
import com.fin_group.aslzar.adapter.ProductInCartAdapter
import com.fin_group.aslzar.cart.Cart
import com.fin_group.aslzar.databinding.FragmentCalculatorBinding
import com.fin_group.aslzar.databinding.FragmentCartBinding
import com.fin_group.aslzar.models.ProductInCart
import com.fin_group.aslzar.ui.dialogs.DeleteAllProductFromCartFragmentDialog
import com.fin_group.aslzar.ui.fragments.cartMain.cart.CartFragment
import com.fin_group.aslzar.ui.fragments.main.MainFragment
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
    val totalPriceWithoutSaleTv= binding.cartTvTotalPriceWithoutSale
    val totalPriceWithSaleTv = binding.cartTvTotalSale
    val cartTvCountProduct = binding.cartTvCountProduct
    val totalPriceTv = binding.cartTvTotalPrice

    cartObserver = object : CartObserver {
        @SuppressLint("SetTextI18n")
        override fun onCartChanged(totalPriceWithoutSale: Number, totalPriceWithSale: Number, totalCount: Int, totalPrice: Number) {
            totalPriceWithSaleTv.text = "${formatNumber(totalPriceWithSale)} USZ"
            totalPriceWithoutSaleTv.text = "${formatNumber(totalPriceWithoutSale)} UZS"
            cartTvCountProduct.text = "$totalCount шт"
            totalPriceTv.text = "${formatNumber(totalPrice)} UZS"
        }
    }
}

@SuppressLint("SetTextI18n")
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
            updateBadge()


            val snackBar = Snackbar.make(requireView(), "Товар удален", Snackbar.LENGTH_LONG)
            snackBar.setAction("Отменить") {
                Cart.addProduct(productToRemove, requireContext())
                allProducts = Cart.getAllProducts()
                myAdapter.updateList(allProducts)
                updateBadge()
                Cart.notifyObservers()
            }
            snackBar.show()
            updateBadge()
        }
    }
}

fun CartFragment.updateBadge(){
    val uniqueProductTypes = Cart.getUniqueProductTypesCount()
    badgeManager.saveBadgeCount(uniqueProductTypes)

    val badge = bottomNavigationView.getOrCreateBadge(R.id.mainCartFragment)
    badge.isVisible = uniqueProductTypes > 0
    badge.number = uniqueProductTypes
}