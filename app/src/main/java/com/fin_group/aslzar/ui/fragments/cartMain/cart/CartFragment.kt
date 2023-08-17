package com.fin_group.aslzar.ui.fragments.cartMain.cart

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.fin_group.aslzar.adapter.ProductInCartAdapter
import com.fin_group.aslzar.cart.Cart
import com.fin_group.aslzar.databinding.FragmentCartBinding
import com.fin_group.aslzar.models.ProductInCart
import com.fin_group.aslzar.ui.dialogs.DeleteAllProductFromCartFragmentDialog
import com.fin_group.aslzar.util.EditProductInCart


class CartFragment : Fragment(), EditProductInCart {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    lateinit var myAdapter: ProductInCartAdapter
    var allProducts: List<ProductInCart> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)

        deleteAllProductFromCart()
        allProducts = Cart.getAllProducts()
        fetchRV(allProducts)

        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun fetchRV(productInCartList: List<ProductInCart>){
        val recyclerView = binding.recyclerViewCart
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        myAdapter = ProductInCartAdapter(productInCartList, this)
        recyclerView.adapter = myAdapter
        myAdapter.notifyDataSetChanged()
    }

    private fun deleteAllProductFromCart(){
        binding.delete.setOnClickListener {
            val delete = DeleteAllProductFromCartFragmentDialog()
            delete.show(childFragmentManager, "delete_all_product_from_cart")
        }
    }

    override fun plusProductInCart(productInCart: ProductInCart) {
        Cart.plusProduct(productInCart.id, requireContext())
        myAdapter.notifyDataSetChanged()
    }

    override fun minusProductInCart(productInCart: ProductInCart) {
        Cart.minusProduct(productInCart.id, requireContext())
        myAdapter.notifyDataSetChanged()
    }

    override fun onStart() {
        super.onStart()
        Cart.loadCartFromPrefs(requireContext())
    }
    override fun onPause() {
        super.onPause()
        Cart.saveCartToPrefs(requireContext())
    }
    override fun onDestroyView() {
        super.onDestroyView()
        Cart.saveCartToPrefs(requireContext())
        _binding = null
    }
}