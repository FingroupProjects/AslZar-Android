package com.fin_group.aslzar.ui.fragments.cartMain.cart

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fin_group.aslzar.R
import com.fin_group.aslzar.adapter.ProductInCartAdapter
import com.fin_group.aslzar.cart.Cart
import com.fin_group.aslzar.databinding.FragmentCartBinding
import com.fin_group.aslzar.models.ProductInCart
import com.fin_group.aslzar.ui.dialogs.DeleteAllProductFromCartFragmentDialog
import com.fin_group.aslzar.ui.fragments.cartMain.cart.functions.cartObserver
import com.fin_group.aslzar.ui.fragments.cartMain.cart.functions.deleteAllProductFromCart
import com.fin_group.aslzar.ui.fragments.cartMain.cart.functions.fetchItemTouchHelper
import com.fin_group.aslzar.ui.fragments.cartMain.cart.functions.fetchRV
import com.fin_group.aslzar.ui.fragments.cartMain.cart.functions.itemTouchCallback
import com.fin_group.aslzar.util.CartObserver
import com.fin_group.aslzar.util.EditProductInCart
import com.fin_group.aslzar.util.OnProductAddedToCartListener
import com.fin_group.aslzar.util.doubleFormat
import com.fin_group.aslzar.util.formatNumber
import com.fin_group.aslzar.viewmodel.SharedViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlin.math.roundToInt


class CartFragment : Fragment(), EditProductInCart, OnProductAddedToCartListener {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

//    lateinit var myAdapter: ProductInCartAdapter
    var myAdapter = ProductInCartAdapter(emptyList(), this)

    var allProducts: List<ProductInCart> = emptyList()
    lateinit var recyclerView: RecyclerView
    lateinit var btnDeleteAllProducts: FloatingActionButton

    lateinit var cartObserver: CartObserver

    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        recyclerView = binding.cartRvItemsInCart
        allProducts = Cart.getAllProducts()
        btnDeleteAllProducts = binding.btnDelete

        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedViewModel.productAdded.observe(viewLifecycleOwner) { product ->
            onProductAddedToCart(product)
        }
        cartObserver(binding)
        Cart.registerObserver(cartObserver)

        fetchItemTouchHelper()
        val itemTouchHelper = ItemTouchHelper(itemTouchCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)

        fetchRV(allProducts)
        deleteAllProductFromCart()
    }

    private val Int.dp
        get() = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            toFloat(), resources.displayMetrics
        ).roundToInt()

    override fun plusProductInCart(productInCart: ProductInCart) {
        Cart.plusProduct(productInCart.id, requireContext())
        allProducts = Cart.getAllProducts()
        myAdapter.updateList(allProducts)
        Cart.notifyObservers()

    }
    override fun minusProductInCart(productInCart: ProductInCart) {
        Cart.minusProduct(productInCart.id, requireContext())
        allProducts = Cart.getAllProducts()
        myAdapter.updateList(allProducts)
        Cart.notifyObservers()
    }
    override fun onProductAddedToCart(product: ProductInCart) {
        allProducts = Cart.getAllProducts()
        myAdapter.updateList(allProducts)
        Cart.notifyObservers()
    }
    override fun onCartCleared() {
        allProducts = Cart.getAllProducts()
        myAdapter.updateList(allProducts)
        Cart.notifyObservers()
    }
    override fun onStart() {
        super.onStart()
        allProducts = Cart.getAllProducts()
        myAdapter.updateList(allProducts)
        Cart.notifyObservers()
        Cart.loadCartFromPrefs(requireContext())
    }
    override fun onPause() {
        super.onPause()
        Cart.saveCartToPrefs(requireContext())
    }
    override fun onDestroyView() {
        super.onDestroyView()
        Cart.unregisterObserver(cartObserver)
        Cart.saveCartToPrefs(requireContext())
        _binding = null
    }
}