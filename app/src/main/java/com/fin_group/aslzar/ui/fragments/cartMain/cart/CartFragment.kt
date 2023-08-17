package com.fin_group.aslzar.ui.fragments.cartMain.cart

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fin_group.aslzar.adapter.ProductInCartAdapter
import com.fin_group.aslzar.cart.Cart
import com.fin_group.aslzar.databinding.FragmentCartBinding
import com.fin_group.aslzar.models.ProductInCart
import com.fin_group.aslzar.ui.dialogs.DeleteAllProductFromCartFragmentDialog
import com.fin_group.aslzar.ui.fragments.cartMain.cart.functions.deleteAllProductFromCart
import com.fin_group.aslzar.ui.fragments.cartMain.cart.functions.fetchRV
import com.fin_group.aslzar.util.EditProductInCart


class CartFragment : Fragment(), EditProductInCart {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    lateinit var myAdapter: ProductInCartAdapter
    var allProducts: List<ProductInCart> = emptyList()
    lateinit var recyclerView: RecyclerView
    lateinit var btnDeleteAllProducts: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        recyclerView = binding.recyclerViewCart
        allProducts = Cart.getAllProducts()
        btnDeleteAllProducts = binding.delete

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchRV(allProducts)
        deleteAllProductFromCart()
    }

    override fun plusProductInCart(productInCart: ProductInCart) {
        Cart.plusProduct(productInCart.id, requireContext())
        allProducts = Cart.getAllProducts()
        myAdapter.updateList(allProducts)
    }
    override fun minusProductInCart(productInCart: ProductInCart) {
        Cart.minusProduct(productInCart.id, requireContext())
        allProducts = Cart.getAllProducts()
        myAdapter.updateList(allProducts)
    }
    override fun onProductAddedToCart(product: ProductInCart) {
        allProducts = Cart.getAllProducts()
        myAdapter.updateList(allProducts)

        Log.d("TAG", "addProductToCart: $product")
        Log.d("TAG", "addProductToCart: $allProducts")
    }
    override fun onCartCleared() {
        allProducts = Cart.getAllProducts()
        myAdapter.updateList(allProducts)
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