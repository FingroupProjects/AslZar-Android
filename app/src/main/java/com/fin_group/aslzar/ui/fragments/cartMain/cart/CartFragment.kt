package com.fin_group.aslzar.ui.fragments.cartMain.cart

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.fin_group.aslzar.R
import com.fin_group.aslzar.adapter.ProductInCartAdapter
import com.fin_group.aslzar.cart.Cart
import com.fin_group.aslzar.databinding.FragmentCartBinding
import com.fin_group.aslzar.models.ProductInCart
import com.fin_group.aslzar.response.ResultXV2
import com.fin_group.aslzar.ui.activities.MainActivity
import com.fin_group.aslzar.ui.fragments.cartMain.MainCartFragmentDirections
import com.fin_group.aslzar.ui.fragments.cartMain.cart.functions.cartObserver
import com.fin_group.aslzar.ui.fragments.cartMain.cart.functions.deleteAllProductFromCart
import com.fin_group.aslzar.ui.fragments.cartMain.cart.functions.fetchItemTouchHelper
import com.fin_group.aslzar.ui.fragments.cartMain.cart.functions.fetchRV
import com.fin_group.aslzar.ui.fragments.cartMain.cart.functions.itemTouchCallback
import com.fin_group.aslzar.ui.fragments.cartMain.cart.functions.updateBadge
import com.fin_group.aslzar.util.BadgeManager
import com.fin_group.aslzar.util.CartObserver
import com.fin_group.aslzar.util.EditProductInCart
import com.fin_group.aslzar.util.OnProductAddedToCartListener
import com.fin_group.aslzar.viewmodel.SharedViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton


class CartFragment : Fragment(), EditProductInCart, OnProductAddedToCartListener {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    //    lateinit var myAdapter: ProductInCartAdapter
    var myAdapter = ProductInCartAdapter(emptyList(), this)

    var allProducts: List<ProductInCart> = emptyList()
    lateinit var recyclerView: RecyclerView
    lateinit var btnDeleteAllProducts: FloatingActionButton

    lateinit var cartObserver: CartObserver

    lateinit var mainActivity: MainActivity
    lateinit var bottomNavigationView: BottomNavigationView
    lateinit var badgeManager: BadgeManager

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

        mainActivity =
            activity as? MainActivity ?: throw IllegalStateException("Activity is not MainActivity")

        sharedViewModel.productAdded.observe(viewLifecycleOwner) { product ->
            product?.let {
                onProductAddedToCart(product)
            }
        }
        cartObserver(binding)
        Cart.registerObserver(cartObserver)

        fetchItemTouchHelper()
        val itemTouchHelper = ItemTouchHelper(itemTouchCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)

        fetchRV(allProducts)
        deleteAllProductFromCart()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        badgeManager = BadgeManager(requireContext(), "badge_cart_prefs")
    }

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

    override fun openDialogDataProduct(productInCart: ProductInCart) {



        val product = ResultXV2(
            "",
            "",
            "",
            "",
            "",
            productInCart.id,
            productInCart.image,
            false,
            "",
            productInCart.name,
            productInCart.price.toInt(),
            "",
            productInCart.sale.toInt(),
            "",
            emptyList()
        )

        try {
            val action =
                MainCartFragmentDirections.actionMainCartFragmentToDataProductFragment(productInCart.id, product, "Cart")
            Navigation.findNavController(binding.root).navigate(action)
        } catch (e: Exception){
            Log.d("TAG", "openDialogDataProduct: ${e.message}")
        }
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

    override fun onResume() {
        super.onResume()
        bottomNavigationView = mainActivity.findViewById(R.id.bottomNavigationView)
        updateBadge()
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