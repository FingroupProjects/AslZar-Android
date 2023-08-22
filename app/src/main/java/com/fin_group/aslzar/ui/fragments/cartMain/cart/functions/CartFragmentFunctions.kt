package com.fin_group.aslzar.ui.fragments.cartMain.cart.functions

import android.util.Log
import android.widget.Toast
import android.annotation.SuppressLint
import androidx.recyclerview.widget.LinearLayoutManager
import com.fin_group.aslzar.adapter.ProductInCartAdapter
import com.fin_group.aslzar.cart.Cart
import com.fin_group.aslzar.models.ProductInCart
import com.fin_group.aslzar.ui.dialogs.DeleteAllProductFromCartFragmentDialog
import com.fin_group.aslzar.ui.fragments.cartMain.cart.CartFragment

@SuppressLint("NotifyDataSetChanged")
fun CartFragment.fetchRV(productInCartList: List<ProductInCart>){
    recyclerView.layoutManager = LinearLayoutManager(requireContext())
    myAdapter = ProductInCartAdapter(productInCartList, this)
    recyclerView.adapter = myAdapter
    myAdapter.notifyDataSetChanged()
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